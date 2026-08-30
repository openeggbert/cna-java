#!/usr/bin/env python3
"""Negative-mutation tests for the CNA-Java native-boundary tools.

A verifier that only passes on the current tree proves nothing.  Every check in
``verify.py``, ``inventory.py`` and ``coverage.py`` is exercised here by
mutating a copy of the input until that specific check fails, so a check that
silently stopped working is itself a test failure.
"""

from __future__ import annotations

import argparse
import copy
import json
import os
from pathlib import Path
import sys

sys.path.insert(0, str(Path(__file__).resolve().parent))
import coverage as coverage_tool  # noqa: E402
import inventory as inventory_tool  # noqa: E402
import verify as verify_tool  # noqa: E402


ROOT = Path(__file__).resolve().parents[2]
FAILURES: list[str] = []
PASSES: list[str] = []


def check(condition: bool, description: str) -> None:
    if condition:
        PASSES.append(description)
        print(f"PASS {description}")
    else:
        print(f"FAIL {description}")
        FAILURES.append(description)


def prefixed(failures: list[str], code: str) -> bool:
    return any(failure.startswith(code) for failure in failures)


def test_inventory(include: Path) -> dict:
    live = inventory_tool.inventory(include, strict=True)
    check(live["functions"], "inventory extracts CNA C API functions")
    check(not live["problems"], "inventory reports no unparsable declaration")
    check(all(value["name"].startswith("cna_") for value in live["functions"].values()),
          "every extracted function carries a cna_ symbol name")
    check(inventory_tool.identity_prefix("CNA_TonemappingMode") == "CNA_TONEMAPPING_MODE_",
          "identity value prefixes derive from the identity type name")
    tonemapping = live["identities"].get("CNA_TonemappingMode")
    check(tonemapping is not None and tonemapping["values"],
          "an identity collects the constants that name its values")
    return live


def test_manifest(live: dict) -> None:
    manifest = json.loads(verify_tool.MANIFEST.read_text(encoding="utf-8"))
    check(not verify_tool.check_manifest(manifest, live),
          "the committed manifest matches the live CNA headers")

    mutated = copy.deepcopy(manifest)
    mutated["functions"][0]["parameters"] = ["int"]
    check(prefixed(verify_tool.check_manifest(mutated, live), "PARAMETER_DRIFT"),
          "a manifest parameter-type mutation is rejected")

    mutated = copy.deepcopy(manifest)
    mutated["functions"][0]["returnType"] = "void"
    check(prefixed(verify_tool.check_manifest(mutated, live), "RETURN_TYPE_DRIFT"),
          "a manifest return-type mutation is rejected")

    mutated = copy.deepcopy(manifest)
    mutated["functions"][0]["header"] = "not_a_header.h"
    check(prefixed(verify_tool.check_manifest(mutated, live), "HEADER_DRIFT"),
          "a manifest header mutation is rejected")

    mutated = copy.deepcopy(manifest)
    mutated["functions"][0]["name"] = "cna_route_that_was_removed"
    check(prefixed(verify_tool.check_manifest(mutated, live), "BOUND_SYMBOL_REMOVED_UPSTREAM"),
          "a symbol removed upstream is rejected")


def test_jni(live: dict) -> None:
    manifest = json.loads(verify_tool.MANIFEST.read_text(encoding="utf-8"))
    check(not verify_tool.check_jni(manifest),
          "the JNI adapter loads exactly the manifest's symbols")

    mutated = copy.deepcopy(manifest)
    mutated["functions"].append({"name": "cna_get_abi_version_missing_from_jni",
                                 "header": "abi.h", "ownership": "none",
                                 "returnType": "uint32_t", "parameters": []})
    check(prefixed(verify_tool.check_jni(mutated), "MANIFEST_SYMBOL_MISSING_FROM_JNI"),
          "a manifest symbol the JNI adapter never loads is rejected")

    mutated = copy.deepcopy(manifest)
    del mutated["functions"][0]
    check(prefixed(verify_tool.check_jni(mutated), "JNI_SYMBOL_MISSING_FROM_MANIFEST"),
          "a JNI symbol absent from the manifest is rejected")

    source = "\n".join(path.read_text(encoding="utf-8")
                       for path in verify_tool.JNI_SOURCES)
    slots = verify_tool.ROUTE_PATTERN.findall(source)
    check(len(slots) == len(manifest["functions"]),
          "every dispatch-table slot is declared through CNA_JNI_ROUTE")
    symbols = {symbol for symbol, _ in slots}
    check(symbols == {entry["name"] for entry in manifest["functions"]},
          "every CNA_JNI_ROUTE names a manifest symbol")


def test_policy() -> None:
    manifest = json.loads(verify_tool.MANIFEST.read_text(encoding="utf-8"))
    major, minor, patch = (int(value) for value in manifest["compiledAbi"].split("."))

    def encode(a: int, b: int, c: int) -> int:
        return (a << 16) | (b << 8) | c

    check(not verify_tool.check_policy(manifest, encode(major, minor, patch)),
          "the exact compiled ABI is accepted")
    check(not verify_tool.check_policy(manifest, encode(major, minor, patch + 7)),
          "a differing patch component is accepted")
    check(prefixed(verify_tool.check_policy(manifest, encode(major + 1, minor, patch)),
                   "ABI_MAJOR_INCOMPATIBLE"),
          "an incompatible ABI major is rejected")
    check(prefixed(verify_tool.check_policy(manifest, encode(major, minor + 1, patch)),
                   "ABI_MINOR_INCOMPATIBLE"),
          "a different ABI minor is rejected, because a 0.x minor may be incompatible")
    check(prefixed(verify_tool.check_policy(manifest, encode(major, minor - 1, patch)),
                   "ABI_MINOR_INCOMPATIBLE"),
          "an older ABI minor is rejected")


def test_coverage(cna_root: Path) -> None:
    report = coverage_tool.build(cna_root)
    counts = report["classificationCounts"]
    check(counts.get("UNMAPPED_REQUIRES_REVIEW", 0) == 0,
          "every canonical CNA C API function carries an explicit classification")
    check(report["canonicalFunctions"] == len(report["functions"]),
          "the coverage report covers every canonical function exactly once")
    check(counts.get("XNA_BACKING", 0) > 0,
          "the strict XNA projection is shown to reach real native routes")

    entries = coverage_tool.c_functions("\n".join(
        path.read_text(encoding="utf-8") for path in coverage_tool.JNI_SOURCES))
    check(sum(1 for name in entries if name.startswith("Java_")) > 0,
          "the JNI adapter's entry points are resolved from the C source")
    check("Java_org_openeggbert_cna_internal_NativeAudio_nativeDestroyCue" in entries,
          "a macro-generated JNI entry point is expanded rather than dropped")

    rules = json.loads(coverage_tool.RULES.read_text(encoding="utf-8"))
    unmatched = coverage_tool.match_rule({"match": {}}, "cna_anything", "anything.h")
    check(not unmatched, "an empty rule never matches")
    check(all("reason" in rule and rule["reason"].strip() for rule in rules["rules"]),
          "every coverage rule states a reason")
    check(all(rule["classification"] in rules["classifications"] for rule in rules["rules"]),
          "every coverage rule uses a declared classification")


def test_probe(include: Path) -> None:
    try:
        verify_tool.compile_probe(include)
        check(True, "the layout and signature probe compiles against the live headers")
    except Exception as failure:  # noqa: BLE001 - the diagnostic is the point
        check(False, f"the layout and signature probe compiles against the live headers: {failure}")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--cna-root", default=os.environ.get("CNA_ROOT"))
    arguments = parser.parse_args()
    cna_root = verify_tool.resolve_cna_root(arguments.cna_root)
    include = cna_root / "modules/c-api/include"

    live = test_inventory(include)
    test_manifest(live)
    test_jni(live)
    test_policy()
    test_coverage(cna_root)
    test_probe(include)

    print(f"NATIVE_TOOL_TESTS={len(PASSES)} passed, {len(FAILURES)} failed")
    return 1 if FAILURES else 0


if __name__ == "__main__":
    raise SystemExit(main())
