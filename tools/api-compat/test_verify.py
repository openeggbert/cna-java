#!/usr/bin/env python3
"""Focused regression tests for contract mapping and compiled metadata reading."""

from __future__ import annotations

import importlib.util
import json
from pathlib import Path
import subprocess
import tempfile
import unittest


ROOT = Path(__file__).resolve().parents[2]
SPEC = importlib.util.spec_from_file_location("cna_java_api_verify", Path(__file__).with_name("verify.py"))
if SPEC is None or SPEC.loader is None:
    raise RuntimeError("cannot load verifier module")
VERIFY = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(VERIFY)


class VerifyTests(unittest.TestCase):

    @classmethod
    def setUpClass(cls) -> None:
        cls.rules = json.loads(
            (ROOT / "tools/api-compat/mapping-rules.json").read_text(encoding="utf-8"))
        VERIFY.GENERIC_RENAMES.clear()
        VERIFY.GENERIC_RENAMES.update(cls.rules.get("genericTypeRenames", {}))
        VERIFY.TYPE_RENAMES.clear()
        VERIFY.TYPE_RENAMES.update(cls.rules.get("frameworkTypeMappings", {}))

    def test_interface_property_and_event_accessors_are_abstract(self) -> None:
        contract = {
            "name": "Microsoft.Xna.Framework.IProbe",
            "kind": "interface",
            "members": [
                {"kind": "property", "name": "Enabled", "type": "System.Boolean",
                 "static": False, "getterAccess": "public", "setterAccess": "none",
                 "parameters": []},
                {"kind": "event", "name": "Changed", "type": "System.EventHandler`1[System.EventArgs]",
                 "static": False, "addAccess": "public", "removeAccess": "public"},
            ],
        }

        members = VERIFY.mapped_members(contract, self.rules, [])
        self.assertEqual(3, len(members))
        self.assertTrue(all(member["abstract"] for member in members))

    def test_framework_compatibility_types_are_counted_in_expected_contract(self) -> None:
        reference = {"types": [{
            "name": "Microsoft.Xna.Framework.Probe",
            "kind": "class",
            "members": [],
        }]}
        expected_types, expected_members = VERIFY.expected_java_counts(reference, self.rules)
        self.assertEqual(1 + len(self.rules["syntheticTypes"]), expected_types)
        self.assertEqual(sum(len(value["members"]) for value in self.rules["syntheticTypes"]),
                         expected_members)

    def test_unsigned_clr_byte_maps_to_range_checked_java_int(self) -> None:
        self.assertEqual("int", VERIFY.map_type("System.Byte"))
        self.assertEqual("byte", VERIFY.map_type("System.SByte"))

    def test_stream_direction_and_missing_parameter_names_are_explicit(self) -> None:
        member = {
            "name": "FromStream",
            "parameters": [
                {"name": "graphicsDevice", "type": "Microsoft.Xna.Framework.Graphics.GraphicsDevice"},
                {"name": "stream", "type": "System.IO.Stream"},
            ],
        }
        parameters = VERIFY.map_member_parameters(
            "Microsoft.Xna.Framework.Graphics.Texture2D", member, self.rules)
        self.assertEqual("java.io.InputStream", parameters[1]["type"])
        unnamed = VERIFY.map_parameters([{"name": "", "type": "System.Boolean"}])
        self.assertEqual("arg0", unnamed[0]["name"])

    def test_named_color_properties_map_uniformly_to_same_cased_fields(self) -> None:
        contract = {
            "name": "Microsoft.Xna.Framework.Color", "kind": "struct",
            "interfaces": [],
            "members": [
                {"kind": "property", "name": "AliceBlue",
                 "type": "Microsoft.Xna.Framework.Color", "static": True,
                 "getterAccess": "public", "setterAccess": "none", "parameters": []},
                {"kind": "property", "name": "R", "type": "System.Byte", "static": False,
                 "getterAccess": "public", "setterAccess": "public", "parameters": []},
            ],
        }
        members = VERIFY.mapped_members(contract, self.rules, [])
        alice_blue = next(value for value in members if value["name"] == "AliceBlue")
        self.assertEqual("field", alice_blue["kind"])
        self.assertTrue(alice_blue["static"])
        self.assertTrue(alice_blue["final"])
        self.assertTrue(any(value["name"] == "getR" for value in members))
        self.assertTrue(any(value["name"] == "setR" for value in members))

    def test_explicit_disposable_implementation_still_projects_close(self) -> None:
        contract = {
            "name": "Microsoft.Xna.Framework.DisposableProbe",
            "kind": "class", "abstract": False,
            "interfaces": ["System.IDisposable"], "members": [],
        }
        members = VERIFY.mapped_members(contract, self.rules, [])
        close = next(value for value in members if value["name"] == "close")
        self.assertEqual("public", close["access"])
        self.assertTrue(close["final"])

    def test_explicit_excluded_clr_member_is_a_mapping_rule_not_allowlist(self) -> None:
        contract = {
            "name": "Microsoft.Xna.Framework.Content.ContentLoadException",
            "kind": "class", "interfaces": [],
            "members": [{
                "kind": "constructor", "name": ".ctor", "access": "protected",
                "static": False, "abstract": False, "final": False,
                "genericArity": 0, "returnType": None,
                "parameters": [
                    {"name": "info", "type": "System.Runtime.Serialization.SerializationInfo"},
                    {"name": "context", "type": "System.Runtime.Serialization.StreamingContext"},
                ],
            }],
        }
        self.assertEqual([], VERIFY.mapped_members(contract, self.rules, []))

    def test_flags_enum_maps_to_composable_value_contract(self) -> None:
        contract = {
            "name": "Microsoft.Xna.Framework.ProbeFlags",
            "kind": "enum",
            "flags": True,
            "members": [
                {"kind": "field", "name": "None", "type": "Microsoft.Xna.Framework.ProbeFlags",
                 "static": True, "final": True, "constant": "0", "access": "public"},
                {"kind": "field", "name": "First", "type": "Microsoft.Xna.Framework.ProbeFlags",
                 "static": True, "final": True, "constant": "1", "access": "public"},
                {"kind": "field", "name": "value__", "type": "System.Int32",
                 "static": False, "final": False, "constant": None, "access": "public"},
            ],
        }

        members = VERIFY.mapped_enum_members(contract)
        self.assertNotIn("value__", [member["name"] for member in members])
        self.assertEqual(
            {"getValue", "FromValue", "Or", "Contains", "equals", "hashCode"},
            {member["name"] for member in members if member["kind"] == "method"})

    def test_sequential_enum_detection_is_independent_of_metadata_sort_order(self) -> None:
        contract = {
            "name": "Microsoft.Xna.Framework.ProbeEnum",
            "kind": "enum",
            "flags": False,
            "members": [
                {"kind": "field", "name": "Second", "type": "Microsoft.Xna.Framework.ProbeEnum",
                 "static": True, "final": True, "constant": "1", "access": "public"},
                {"kind": "field", "name": "First", "type": "Microsoft.Xna.Framework.ProbeEnum",
                 "static": True, "final": True, "constant": "0", "access": "public"},
                {"kind": "field", "name": "value__", "type": "System.Int32",
                 "static": False, "final": False, "constant": None, "access": "public"},
            ],
        }
        self.assertFalse(any(member["kind"] == "method"
                             for member in VERIFY.mapped_enum_members(contract)))

    def test_instance_method_in_final_class_is_effectively_final(self) -> None:
        method = {"kind": "method", "static": False, "final": False}
        self.assertTrue(VERIFY.effective_member_final(method, True))
        self.assertFalse(VERIFY.effective_member_final(method, False))
        self.assertFalse(VERIFY.effective_member_final({**method, "static": True}, True))
        self.assertFalse(VERIFY.effective_member_final(
            {"kind": "field", "static": False, "final": False}, True))

    def test_parameter_pairing_never_steals_an_exact_overload(self) -> None:
        reference = {"types": [{
            "name": "Probe.Value", "kind": "class", "access": "public",
            "abstract": False, "sealed": False, "baseType": "System.Object",
            "interfaces": [], "genericArity": 0,
            "members": [
                {"kind": "method", "name": "Equals", "access": "public",
                 "static": False, "abstract": False, "final": False, "virtual": True,
                 "genericArity": 0, "returnType": "System.Boolean",
                 "parameters": [{"name": "obj", "type": "System.Object"}]},
                {"kind": "method", "name": "Equals", "access": "public",
                 "static": False, "abstract": False, "final": False, "virtual": False,
                 "genericArity": 0, "returnType": "System.Boolean",
                 "parameters": [{"name": "other", "type": "Probe.Value"}]},
            ],
        }]}
        target = {"types": [{
            "name": "Probe.Value", "kind": "class", "access": "public",
            "abstract": False, "sealed": False, "baseType": "java.lang.Object",
            "interfaces": [], "genericArity": 0,
            "members": [{"kind": "method", "name": "equals", "access": "public",
                         "static": False, "abstract": False, "final": False,
                         "genericArity": 0, "returnType": "boolean",
                         "parameters": [{"name": "obj", "type": "java.lang.Object"}]}],
        }]}

        findings = [value for value in VERIFY.compare(reference, target, self.rules)
                    if value["subject"].startswith("Probe.Value")]
        self.assertEqual(1, len(findings))
        self.assertEqual("MISSING_MEMBER", findings[0]["code"])
        self.assertIn("Probe.Value", findings[0]["subject"])

    def test_inaccessible_clr_interface_is_not_projected(self) -> None:
        reference = {"types": [{
            "name": "Probe.Value", "kind": "class", "access": "public",
            "abstract": False, "sealed": False, "baseType": "System.Object",
            "interfaces": ["Probe.InternalMarker"], "genericArity": 0,
            "members": [],
        }]}
        target = {"types": [{
            "name": "Probe.Value", "kind": "class", "access": "public",
            "abstract": False, "sealed": False, "baseType": "java.lang.Object",
            "interfaces": [], "genericArity": 0, "members": [],
        }]}
        findings = VERIFY.compare(reference, target, self.rules)
        self.assertFalse(any(value["code"] == "INTERFACE_MISMATCH" for value in findings))

    def test_leak_guard_finds_internal_types_and_raw_long_handles(self) -> None:
        target = {"types": [{
            "name": "Microsoft.Xna.Framework.BadFacade",
            "baseType": "java.lang.Object",
            "interfaces": [],
            "members": [
                {"name": "getAdapter", "returnType": "org.openeggbert.cna.internal.NativeBindings",
                 "parameters": []},
                {"name": "getNativeHandle", "returnType": "long", "parameters": []},
            ],
        }]}
        self.assertEqual(2, len(VERIFY.leak_diagnostics(target)))

    def test_class_reader_normalizes_multi_argument_generic_type_names(self) -> None:
        with tempfile.TemporaryDirectory(prefix="cna-java-api-test-") as directory:
            temporary = Path(directory)
            source = temporary / "source/Microsoft/Xna/Framework/ProbeMap.java"
            source.parent.mkdir(parents=True)
            source.write_text(
                "package Microsoft.Xna.Framework;\n"
                "public class ProbeMap extends java.util.LinkedHashMap<String, String> {\n"
                "    private static final long serialVersionUID = 1L;\n"
                "}\n",
                encoding="utf-8")
            classes = temporary / "classes"
            classes.mkdir()
            subprocess.run(["javac", "--release", "17", "-d", str(classes), str(source)], check=True)
            reader = temporary / "reader"
            reader.mkdir()
            target = VERIFY.read_target(str(classes), reader)
            self.assertEqual(
                "java.util.LinkedHashMap<java.lang.String,java.lang.String>",
                target["types"][0]["baseType"])

    def test_reference_profile_requires_exact_assembly_hashes(self) -> None:
        with tempfile.TemporaryDirectory(prefix="cna-java-reference-test-") as directory:
            root = Path(directory)
            assembly = root / "Reference.dll"
            assembly.write_bytes(b"reference metadata")
            profile = {
                "referenceAssemblies": [assembly.name],
                "referenceSha256": {
                    assembly.name: "5715de24b778e528a96639e0a88e4ab2ed3426b45226a0db96fc36a36ab590cf",
                },
            }
            VERIFY.validate_reference_files(str(root), profile)
            profile["referenceSha256"][assembly.name] = "0" * 64
            with self.assertRaisesRegex(ValueError, "SHA-256 mismatch"):
                VERIFY.validate_reference_files(str(root), profile)


if __name__ == "__main__":
    unittest.main()
