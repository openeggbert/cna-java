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
import generate_jni as generator_tool  # noqa: E402
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


def test_java_abi() -> None:
    """The Java load-time constant must be pinned to the manifest, and a drift must fail."""
    manifest = json.loads(verify_tool.MANIFEST.read_text(encoding="utf-8"))
    check(not verify_tool.check_java_abi(manifest),
          "the Java load-time ABI constant matches the manifest")
    drifted = copy.deepcopy(manifest)
    drifted["compiledAbi"] = "0.99.0"
    check(prefixed(verify_tool.check_java_abi(drifted), "JAVA_COMPILED_ABI_DRIFT"),
          "a manifest that moved without the Java constant is rejected")


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

    # A facade often hands a route to a functional interface as `Type::method` rather than
    # calling it. Missing that form would report a reached route as unreached.
    check(coverage_tool.called_names("a(); Type::b; c ();") == {"a", "b", "c"},
          "a Java method reference counts as a call site")
    check("nativeRun" not in coverage_tool.called_names("// nativeRun"),
          "a bare identifier is not a call site")

    rules = json.loads(coverage_tool.RULES.read_text(encoding="utf-8"))
    unmatched = coverage_tool.match_rule({"match": {}}, "cna_anything", "anything.h")
    check(not unmatched, "an empty rule never matches")
    check(all("reason" in rule and rule["reason"].strip() for rule in rules["rules"]),
          "every coverage rule states a reason")
    check(all(rule["classification"] in rules["classifications"] for rule in rules["rules"]),
          "every coverage rule uses a declared classification")

    # A `native` declaration names its own method, so an unfiltered call scan reads it
    # as a call site and every generated route reports itself as reached. That would
    # make the reachability question unanswerable, which is why it is filtered.
    declaration = ("class Probe {\n"
                   "    public static native int probeRoute(long handle, long[] out);\n"
                   "}\n")
    check("probeRoute" not in coverage_tool.called_names(
              coverage_tool.without_native_declarations(declaration)),
          "a native declaration is not a call site for itself")
    check("probeRoute" in coverage_tool.called_names(
              coverage_tool.without_native_declarations(
                  "class Probe {\n    void use() { probeRoute(0L, null); }\n}\n")),
          "a real call to a native method is still a call site")


def test_generator(live: dict) -> None:
    """The generated boundary must not silently narrow or silently drop data."""

    def plan(symbol: str) -> dict:
        return generator_tool.plan({"java": "probe", "symbol": symbol}, live)

    def struct_step(entry: dict) -> dict:
        return next(step for step in entry["steps"]
                    if step["shape"] in ("struct", "struct_array", "struct_value"))

    # A C double must travel in its own double[] carrier. Sharing the float one would
    # narrow every reading silently, which is the failure this separation prevents.
    compass = plan("cna_compass_get_current_value")
    reading = struct_step(compass)
    check([path for path, _ in reading["doubles"]]
          == ["heading_accuracy", "magnetic_heading", "true_heading"],
          "a struct's C double leaves travel in their own double carrier")
    check(all(leaf == "float" for _, leaf in reading["floating"]),
          "the float carrier holds no double leaf")
    _, parameters = generator_tool.java_signature(compass)
    check(any(value.startswith("double[]") for value in parameters),
          "a struct with double leaves declares a double[] parameter")
    check("jdoubleArray" in generator_tool.render_c("Probe", [compass]),
          "the generated adapter reads the double carrier as a jdoubleArray")

    # A structure carrying CNA_StringView fields: each one is its own byte[] rather than a
    # slot in a shared array, because a view is a pointer and a length. Before this, a whole
    # route was refused because flatten_struct reached the view's const char*.
    discovered = plan("cna_available_network_session_create_ext")
    info = struct_step(discovered)
    check([path for path, _ in info["views"]] == ["host_gamertag", "host_address"],
          "a struct's CNA_StringView fields are collected as views, in declaration order")
    check(all(leaf != "CNA_StringView" for _, leaf in info["integral"]),
          "a view never lands in the integral carrier")
    _, parameters = generator_tool.java_signature(discovered)
    check("byte[] createInfoHostAddress" in parameters,
          "each view field declares its own byte[] parameter named after the field")
    adapter = generator_tool.render_c("Probe", [discovered])
    check("create_info_value.host_address.data = (const char*)createInfoHostAddress_bytes"
          in adapter,
          "the adapter points the view at the pinned Java bytes")
    check("create_info_value.host_address.byte_length = (uint64_t)createInfoHostAddress_size"
          in adapter,
          "the adapter sets the view's length from the array, not from a terminator")
    check(adapter.count("ReleaseByteArrayElements(environment, createInfoHostAddress") == 1,
          "the pinned bytes are released exactly once")

    # A non-const struct pointer is an output, or it is read and written, and the declaration
    # does not say which. Guessing wrong is silent: an in/out structure treated as an output
    # starts zeroed, so the caller's values are discarded and the route answers about a structure
    # nobody asked about. That is exactly what happened to the render-pipeline settings.
    try:
        plan("cna_render_pipeline_settings_ext_normalize")
        check(False, "an undeclared non-const struct pointer is refused rather than guessed at")
    except generator_tool.Unsupported:
        check(True, "an undeclared non-const struct pointer is refused rather than guessed at")

    declared = generator_tool.plan(
        {"java": "probe", "symbol": "cna_render_pipeline_settings_ext_normalize",
         "inOut": ["settings"]}, live)
    adapter = generator_tool.render_c("Probe", [declared])
    check("GetLongArrayRegion" in adapter,
          "a declared in/out structure reads the caller's values in")
    check("SetLongArrayRegion" in adapter,
          "and writes the corrected values back out")

    # CNA's own names for a pure output still pass without a declaration, which is what keeps
    # every copy-out route working: `out_*` and `destination` are its two spellings.
    check(struct_step(plan("cna_model_copy_bone_transforms"))["shape"] == "struct_array",
          "a parameter named destination is still read as an output")

    # A route that acquires more than one thing must release what it holds when a later
    # acquisition fails. Before this, the second failed pin of a four-array route returned
    # straight out and stranded the first -- on an out-of-memory path, which is when leaking is
    # least affordable.
    multi = plan("cna_available_network_session_create_ext")
    adapter = generator_tool.render_c("Probe", [multi])
    first = adapter.index("createInfoHostGamertag_bytes == NULL")
    second = adapter.index("createInfoHostAddress_bytes == NULL")
    check("ReleaseByteArrayElements" not in adapter[first:adapter.index("}", first)],
          "the first acquisition's failure has nothing to release")
    check("createInfoHostGamertag_bytes, JNI_ABORT"
          in adapter[second:adapter.index("return (jint)CNA_RESULT_OUT_OF_MEMORY", second)],
          "a later acquisition's failure releases the earlier one")

    # And each is still released exactly once on the success path. The earlier one appears
    # twice in the file because the later one's failure branch also releases it; the last
    # acquisition appears once, because a pin that failed has nothing to give back.
    check(adapter.count("ReleaseByteArrayElements(environment, createInfoHostGamertag, ") == 2,
          "an earlier acquisition is released on the success path and on the later failure")
    check(adapter.count("ReleaseByteArrayElements(environment, createInfoHostAddress, ") == 1,
          "the last acquisition is released once, because a pin that failed acquired nothing")

    # An input array whose length CNA states in prose rather than in a count parameter. The
    # generator will not infer it -- a wrong guess is a read past the end inside CNA -- so
    # routes.json declares it and the adapter checks the caller's array against the declaration.
    skeleton = generator_tool.plan({
        "java": "probe", "symbol": "cna_cnb_model_set_skeleton",
        "arrayLengths": {
            "bind_pose": {"per": 16, "count": "joint_count"},
            "inverse_bind_pose": {"per": 16, "count": "joint_count"},
            "root_prefix": {"per": 16, "count": "joint_count", "nullable": True},
        }}, live)
    sized = [step for step in skeleton["steps"] if step["shape"] == "sized_array"]
    check(len(sized) == 3, "each declared array becomes its own sized_array step")
    adapter = generator_tool.render_c("Probe", [skeleton])
    guard = adapter[:adapter.index("GetIntArrayElements")]
    check(guard.count("CNA_RESULT_INVALID_ARGUMENT") == 3,
          "every declared length is checked before anything is acquired, so a wrong length "
          "refuses without leaking what earlier parameters pinned")
    check("bind_pose == NULL ||" in adapter,
          "a non-nullable declared array refuses null")
    check("root_prefix != NULL &&" in adapter,
          "a nullable declared array accepts null and checks the length only when present")
    check("16 * (*environment)->GetArrayLength(environment, hierarchy)" in adapter,
          "a length stated per element of another array resolves to that array's own length")

    # A fixed extent needs no count at all.
    bone = generator_tool.plan({"java": "probe", "symbol": "cna_cnb_model_add_bone",
                                "arrayLengths": {"transform": {"length": 16}}}, live)
    check("!= (jsize)16" in generator_tool.render_c("Probe", [bone]),
          "a fixed-extent array is checked against the literal length")

    # And a declaration naming something the route cannot read up front is refused, rather than
    # emitting C that names a variable which does not exist.
    try:
        generator_tool.plan({"java": "probe", "symbol": "cna_cnb_model_add_bone",
                             "arrayLengths": {"transform": {"per": 4, "count": "nonsense"}}},
                            live)
        check(False, "a declared count that is not a readable parameter is refused")
    except generator_tool.Unsupported:
        check(True, "a declared count that is not a readable parameter is refused")

    # And a view whose lifetime the declaration does not state is refused rather than guessed.
    # An output structure's view would point at memory CNA owns on terms the header does not
    # give, and an array of such structures would need one pointer to outlive its element's
    # marshalling. Both are refused, which is checked by asking the planner to accept the same
    # structure in each of those positions.
    for direction, position in (("CNA_AvailableNetworkSessionCreateInfo*", "output structure"),
                                ("const CNA_AvailableNetworkSessionCreateInfo*", "array")):
        synthetic = dict(live)
        synthetic["functions"] = dict(live["functions"])
        parameters = [{"type": direction, "name": "info"}]
        if position == "array":
            parameters.append({"type": "uint64_t", "name": "count"})
        synthetic["functions"]["cna_probe_views"] = {
            "name": "cna_probe_views", "header": "probe.h", "returnType": "CNA_Result",
            "parameters": parameters,
        }
        try:
            generator_tool.plan({"java": "probe", "symbol": "cna_probe_views"}, synthetic)
            check(False, f"a CNA_StringView in an {position} is refused rather than guessed at")
        except generator_tool.Unsupported:
            check(True, f"a CNA_StringView in an {position} is refused rather than guessed at")

    # An output array of structs has to be copied back, or the route returns success
    # and Java sees nothing. This was a real defect: the only such route bound at the
    # time never wrote a single element back.
    copy_to = plan("cna_network_session_properties_copy_to")
    check(struct_step(copy_to)["shape"] == "struct_array" and not struct_step(copy_to)["input"],
          "a non-const struct pointer followed by a count is an output struct array")
    emitted = generator_tool.render_c("Probe", [copy_to])
    check("SetLongArrayRegion" in emitted,
          "an output array of structs is copied back to Java")
    check(emitted.index("SetLongArrayRegion") < emitted.rindex("free(destination_values);"),
          "the copy back happens before the C buffer is freed")

    # One struct is split across up to four parallel Java arrays, and only one of them supplies
    # the element count. A caller whose other arrays are shorter would have every element past
    # their end read out of bounds in C -- a heap overread inside the marshalling loop, not a
    # Java exception -- so a mismatch has to be refused before anything is allocated.
    lights = plan("cna_clustered_light_set_copy_lights")
    check(struct_step(lights)["shape"] == "struct_array",
          "a light set's copy-out is an array of structs")
    emitted = generator_tool.render_c("Probe", [lights])
    check("destination_count * 2" in emitted and "destination_count * 13" in emitted,
          "every parallel carrier's length is checked against the element count")
    check("return (jint)CNA_RESULT_INVALID_ARGUMENT;" in emitted,
          "a mismatched carrier is refused rather than read past its end")
    guard = emitted.index("CNA_RESULT_INVALID_ARGUMENT")
    check(guard < emitted.index("calloc("),
          "the refusal happens before anything is allocated")
    check(guard < emitted.index("GetByteArrayRegion"),
          "and before any carrier is read")

    # A bare `T*` is one structure or an array of them, and the C declaration does not say
    # which. CNA states the difference in prose -- "destination for eight corners" -- so the
    # generator reads the parameter's own documentation and refuses rather than marshalling one
    # element and handing it to a function that writes eight. This was a real defect: both
    # cascade helpers were generated as single Vector3 parameters, which is a stack overflow on
    # the way out and a heap overread on the way in.
    corners = generator_tool.plan(
        {"java": "probe", "symbol": "cna_cascaded_shadow_map_compute_frustum_corners",
         "arrayLengths": {"out_corners": {"length": 8}}}, live)
    step = next(entry for entry in corners["steps"] if entry["name"] == "out_corners")
    check(step["shape"] == "struct_array" and step["extent"] == 8,
          "a declared eight-element destination is marshalled as eight, not one")
    emitted = generator_tool.render_c("Probe", [corners])
    check("const jsize out_corners_count = 8;" in emitted,
          "the extent is the declaration's, not the Java array's length")
    check("!= out_corners_count * 3" in emitted,
          "a Java array of the wrong length is refused rather than overrun")
    check("(uint64_t)out_corners_count" not in emitted,
          "a fixed-extent array passes no count, because CNA already knows it")

    try:
        generator_tool.plan({"java": "probe",
                             "symbol": "cna_cascaded_shadow_map_compute_frustum_corners"}, live)
        check(False, "an undeclared counted destination is refused rather than guessed at")
    except generator_tool.Unsupported as refusal:
        check("arrayLengths" in str(refusal),
              "an undeclared counted destination is refused rather than guessed at")

    # The detector reads prose, so it has to tell a number of *things* from a number. CNA
    # describes an area light's defaults as "range twenty" and its BRDF terms as "the four
    # terms"; the first is a value and the second is one structure with four fields, and
    # neither is an array. A number alone never fires, and a route can say so explicitly.
    check(not generator_tool.counts_more_than_one(
              "Receives a rectangle at the origin, half a unit across each way, white, at "
              "intensity one and range twenty."),
          "a number that is a value rather than a count does not fire the detector")
    check(generator_tool.counts_more_than_one("Destination for eight corners."),
          "a number of things does")
    check(generator_tool.counts_more_than_one(
              "Receives CNA_AREA_LIGHT_QUAD_CORNER_COUNT corners."),
          "and so does one of CNA's own count constants")
    terms = generator_tool.plan(
        {"java": "probe", "symbol": "cna_area_light_brdf_table_evaluate",
         "singleStructs": ["out_terms"]}, live)
    check(any(entry["shape"] == "struct" and entry["name"] == "out_terms"
              for entry in terms["steps"]),
          "singleStructs says the count in the prose is the structure's own fields")

    # And the detector must not fire on an ordinary single structure, or every route taking a
    # box or a matrix would need a declaration it does not want.
    single = plan("cna_shadow_map_begin")
    check(any(entry["shape"] == "struct" and entry["name"] == "scene_bounds"
              for entry in single["steps"]),
          "a single structure whose documentation names no count stays a single structure")

    # The adapter's own JNI parameters are called `environment` and `declaring_class`, and CNA
    # has routes whose parameter is called `environment` too. Two parameters of one C function
    # cannot share a name, and the first version of this generator emitted exactly that -- the
    # whole adapter stopped compiling the moment the skybox family was bound.
    skybox = plan("cna_skybox_set_environment")
    emitted = generator_tool.render_c("Probe", [skybox])
    check("jlong environment_parameter" in emitted,
          "a CNA parameter that collides with the adapter's own is renamed in C")
    check("JNIEnv* environment," in emitted,
          "and the adapter keeps its own name")
    _, parameters = generator_tool.java_signature(skybox)
    check(any(value == "long environment" for value in parameters),
          "the Java declaration is untouched, because nothing a caller sees changed")

    # An input struct must not be written back over the caller's data.
    haptic = plan("cna_haptic_device_get_is_effect_supported")
    effect = next(step for step in haptic["steps"] if step["shape"] == "struct")
    check(effect["input"], "a const struct pointer is an input parameter")
    check("SetLongArrayRegion" not in generator_tool.render_c("Probe", [haptic]),
          "an input struct is never written back over the caller's array")

    # A required-size output has to cross even when the buffer was too small, or the two-call
    # size protocol -- ask with no buffer, be told the size, ask again -- can never work.
    encode = plan("cna_cnb_encode_texture2d")
    emitted = generator_tool.render_c("Probe", [encode])
    check("call_result == CNA_RESULT_BUFFER_TOO_SMALL" in emitted,
          "an output is copied back when the caller's buffer was too small")
    check(emitted.count("if (call_result == CNA_RESULT_SUCCESS)") == 1,
          "the array write-back still happens only on success, because a refused copy wrote "
          "nothing into the buffer")

    # The generator refuses a shape it cannot prove rather than guessing at it.
    try:
        plan("cna_text_input_subscribe_text_input_ext")
        check(False, "a callback route is refused rather than guessed at")
    except generator_tool.Unsupported:
        check(True, "a callback route is refused rather than guessed at")


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
    test_java_abi()
    test_policy()
    test_coverage(cna_root)
    test_generator(live)
    test_probe(include)

    print(f"NATIVE_TOOL_TESTS={len(PASSES)} passed, {len(FAILURES)} failed")
    return 1 if FAILURES else 0


if __name__ == "__main__":
    raise SystemExit(main())
