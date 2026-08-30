#!/usr/bin/env python3
"""Classifies every canonical CNA C API function for CNA-Java.

The goal is not to bind every C function.  Many are pure value math CNA-Java
performs in Java, and many are C-only conveniences.  The goal is that **no
canonical function is unexplained**: each one is either reached from Java --
and the report says from which public surface -- or carries an explicit rule
saying why it is not.

For a bound function the classification is *derived*, never declared:

1. the JNI adapter is parsed into its top-level C functions and a call graph,
   so each ``Java_org_openeggbert_cna_internal_*`` entry point resolves to the
   transitive set of ``cna.<slot>`` routes it can reach;
2. the Java sources are parsed for ``native`` declarations and their call
   sites, so each route resolves to the set of Java packages that reach it;
3. a route reached from ``Microsoft.Xna.Framework.*`` is ``XNA_BACKING``, one
   reached only from ``org.openeggbert.cna.extensions.*`` is a bound CNA
   extension, and one reached only from internal plumbing is
   ``JAVA_INTERNAL_ONLY``.

For an unbound function ``coverage-rules.json`` supplies the classification and
the exact reason.  Anything no rule matches is ``UNMAPPED_REQUIRES_REVIEW``,
which ``--check`` treats as a failure.
"""

from __future__ import annotations

import argparse
import json
from pathlib import Path
import re
import sys

sys.path.insert(0, str(Path(__file__).resolve().parent))
from inventory import abi_version, inventory  # noqa: E402


ROOT = Path(__file__).resolve().parents[2]
MANIFEST = ROOT / "tools/native-abi/bindings.json"
RULES = ROOT / "tools/native-abi/coverage-rules.json"
JNI_SOURCES = sorted(list((ROOT / "src/main/c").glob("*.c"))
                     + list((ROOT / "src/main/c").glob("*.inc"))
                     + list((ROOT / "src/main/c/generated").glob("*.inc")))
MAIN_JAVA = ROOT / "src/main/java"
TEST_JAVA = ROOT / "src/test/java"

LOAD_PATTERN = re.compile(r'LOAD\(\s*([A-Za-z0-9_]+)\s*,\s*"(cna_[A-Za-z0-9_]+)"\s*\)')
SLOT_USE = re.compile(r"\bcna\.([A-Za-z0-9_]+)\b")
JNI_ENTRY = re.compile(r"^Java_(?P<symbol>[A-Za-z0-9_]+)$")
NATIVE_METHOD = re.compile(
    r"\bnative\s+[A-Za-z0-9_$.<>,\[\] ?]+?\s+(?P<name>[A-Za-z0-9_]+)\s*\(", re.MULTILINE)

STRICT_PACKAGE = "Microsoft.Xna.Framework"
EXTENSION_PACKAGE = "org.openeggbert.cna.extensions"
INTERNAL_PACKAGE = "org.openeggbert.cna.internal"


MACRO_INVOCATION = re.compile(r"^(?P<macro>[A-Z][A-Z0-9_]+)\((?P<arguments>[^)]*)\)\s*$")
MACRO_CALL_SHAPE = re.compile(r"^[A-Z][A-Z0-9_]+\(")


def macro_generated_entries(source: str) -> dict[str, object]:
    """Return expanders for the JNI entry points defined by generator macros.

    A macro such as ``AUDIO_UNARY_JNI(nativeDestroyCue, cue_destroy)`` defines a
    real ``Java_...`` entry point that a line scan cannot see.  Each definition
    is read once, and its parameter positions are used to rebuild the entry
    name and the dispatch-table slot the expansion calls.
    """
    expanders: dict[str, object] = {}
    for match in re.finditer(
            r"^#define\s+(?P<macro>[A-Z][A-Z0-9_]+)\((?P<parameters>[^)]*)\)"
            r"(?P<body>(?:[^\n]*\\\n)*[^\n]*)", source, re.MULTILINE):
        body = match.group("body")
        entry = re.search(r"(Java_[A-Za-z0-9_]*?)##([A-Za-z0-9_]+)", body)
        if entry is None:
            continue
        parameters = [value.strip() for value in match.group("parameters").split(",")]
        name_index = parameters.index(entry.group(2)) if entry.group(2) in parameters else None
        if name_index is None:
            continue
        prefix = entry.group(1)

        def expander(arguments: list[str], prefix: str = prefix, body: str = body,
                     parameters: list[str] = parameters, name_index: int = name_index):
            expanded = body
            for parameter, argument in zip(parameters, arguments):
                expanded = re.sub(r"\b" + re.escape(parameter) + r"\b", argument, expanded)
            return prefix + arguments[name_index], expanded

        expanders[match.group("macro")] = expander
    return expanders


def c_functions(source: str) -> dict[str, str]:
    """Split the JNI translation unit into its top-level function bodies.

    Every definition in this file starts at column zero and ends with a closing
    brace at column zero, so a line scan is exact and needs no C parser.  A
    definition's header may span several lines; it ends at the line whose last
    character is the opening brace.
    """
    functions: dict[str, str] = {}
    macro_definitions = macro_generated_entries(source)
    lines = source.split("\n")
    index = 0
    while index < len(lines):
        line = lines[index]
        stripped = line.rstrip()
        macro = MACRO_INVOCATION.match(line)
        if macro is not None:
            # A generator macro such as AUDIO_UNARY_JNI(nativeX, slot) defines a
            # JNI entry point that no line scan can see. Expand it from the macro
            # definition so the generated route is attributed, never dropped.
            expansion = macro_definitions.get(macro.group("macro"))
            if expansion is not None:
                arguments = [value.strip() for value in macro.group("arguments").split(",")]
                entry, body = expansion(arguments)
                functions[entry] = functions.get(entry, "") + "\n" + body
            index += 1
            continue
        if not line or line[0].isspace() or line[0] in "#}*/" or stripped.endswith((";", "\\")) \
                or "(" not in line or MACRO_CALL_SHAPE.match(line):
            index += 1
            continue
        header = stripped
        start = index
        while not header.endswith("{"):
            index += 1
            if index >= len(lines):
                return functions
            candidate = lines[index].strip()
            if candidate.endswith(";") or lines[index].startswith("}"):
                header = ""
                break
            header += " " + candidate
        if not header.endswith("{"):
            index = start + 1
            continue
        match = re.search(r"([A-Za-z_][A-Za-z0-9_]*)\s*\(", header)
        if match is None:
            index += 1
            continue
        body: list[str] = []
        index += 1
        while index < len(lines) and not lines[index].startswith("}"):
            body.append(lines[index])
            index += 1
        name = match.group(1)
        functions[name] = functions.get(name, "") + "\n" + "\n".join(body)
        index += 1
    return functions


def transitive_slots(functions: dict[str, str]) -> dict[str, set[str]]:
    """Resolve each C function to every dispatch-table slot it can reach."""
    direct = {name: set(SLOT_USE.findall(body)) for name, body in functions.items()}
    calls = {
        name: {value for value in re.findall(r"\b([A-Za-z_][A-Za-z0-9_]*)\s*\(", body)
               if value in functions and value != name}
        for name, body in functions.items()
    }
    resolved: dict[str, set[str]] = {}

    def resolve(name: str, seen: frozenset[str]) -> set[str]:
        if name in resolved:
            return resolved[name]
        if name in seen:
            return set()
        result = set(direct.get(name, ()))
        for callee in calls.get(name, ()):
            result |= resolve(callee, seen | {name})
        if not (seen - {name}):
            resolved[name] = result
        return result

    return {name: resolve(name, frozenset()) for name in functions}


def java_sources(directory: Path) -> dict[str, str]:
    return {str(path.relative_to(directory)): path.read_text(encoding="utf-8")
            for path in directory.rglob("*.java")}


def java_package(relative: str) -> str:
    return str(Path(relative).parent).replace("/", ".")


MEMBER_DECLARATION = re.compile(
    r"^[ \t]+(?:@[A-Za-z0-9_.]+\s+)*"
    r"(?:public|protected|private|static|final|synchronized|abstract|native|strictfp|default|<[^>]*>|\s)+"
    r"[A-Za-z0-9_$.<>,\[\] ?]+?\s+(?P<name>[A-Za-z0-9_]+)\s*\([^;{]*\)[^;{]*(?P<end>[;{])",
    re.MULTILINE | re.DOTALL)


def java_methods(text: str) -> dict[str, str]:
    """Return each declared method's body, keyed by method name.

    Overloads are merged, which is what the coverage question needs: it asks
    which routes a name can reach, not which overload reached them.  A brace
    scan is used rather than a Java parser because the only structure that
    matters here is where one member's body ends.
    """
    methods: dict[str, str] = {}
    for match in MEMBER_DECLARATION.finditer(text):
        name = match.group("name")
        if name in ("if", "for", "while", "switch", "catch", "return", "new", "synchronized"):
            continue
        if match.group("end") == ";":
            methods.setdefault(name, "")
            continue
        depth = 0
        index = match.end() - 1
        start = index
        while index < len(text):
            if text[index] == "{":
                depth += 1
            elif text[index] == "}":
                depth -= 1
                if depth == 0:
                    break
            index += 1
        methods[name] = methods.get(name, "") + "\n" + text[start:index]
    return methods


# A Java call site is either `name(` or a `Type::name` method reference. Missing the second
# would report a route as unreached when a facade passes it as a functional-interface value.
CALL = re.compile(r"\b([A-Za-z_][A-Za-z0-9_]*)\s*\(|::\s*([A-Za-z_][A-Za-z0-9_]*)")


def called_names(text: str) -> set[str]:
    return {name for pair in CALL.findall(text) for name in pair if name}


def internal_route_map(sources: dict[str, str]) -> tuple[dict[str, set[str]], set[str]]:
    """Resolve every internal Java method to the native methods it can reach."""
    native_names: set[str] = set()
    bodies: dict[str, str] = {}
    for relative, text in sources.items():
        if not java_package(relative).startswith(INTERNAL_PACKAGE):
            continue
        for match in NATIVE_METHOD.finditer(text):
            native_names.add(match.group("name"))
        for name, body in java_methods(text).items():
            bodies[name] = bodies.get(name, "") + "\n" + body

    calls = {name: {value for value in called_names(body) if value in bodies and value != name}
             for name, body in bodies.items()}
    resolved: dict[str, set[str]] = {}

    def resolve(name: str, seen: frozenset[str]) -> set[str]:
        if name in resolved:
            return resolved[name]
        if name in seen:
            return set()
        result = {name} if name in native_names else set()
        for callee in calls.get(name, ()):
            result |= resolve(callee, seen | {name})
        if not (seen - {name}):
            resolved[name] = result
        return result

    return {name: resolve(name, frozenset()) for name in bodies}, native_names


def surface_of(package: str) -> str:
    """Classify one Java package as a public surface or as private plumbing.

    ``System.*`` counts as public: XNA's own public API hands the game a
    ``System.IO.Stream`` and a ``System.Collections.Generic.List``, so those
    Java projections are part of the strict surface, not internal plumbing.
    """
    if package.startswith(STRICT_PACKAGE) or package.split(".", 1)[0] == "System":
        return "xna"
    if package.startswith(EXTENSION_PACKAGE):
        return "extension"
    if package.startswith(INTERNAL_PACKAGE):
        return "internal"
    return "other"


# A `native` declaration ends at the first semicolon and has no body. Its own name
# looks exactly like a call site to the call scanner, so it is removed before the
# scan: counting it would let a bound route report itself as reached and make the
# bound-but-unreached invariant vacuous for every generated declaration.
NATIVE_DECLARATION = re.compile(
    r"\bnative\s+[A-Za-z0-9_$.<>,\[\] ?]+?\s+[A-Za-z0-9_]+\s*\([^;{]*\)\s*;",
    re.MULTILINE | re.DOTALL)


def without_native_declarations(text: str) -> str:
    return NATIVE_DECLARATION.sub(" ", text)


def surface_reach(sources: dict[str, str], routes: dict[str, set[str]]) -> dict[str, set[str]]:
    """Map each native method to the Java packages that can reach it."""
    reach: dict[str, set[str]] = {}
    for relative, text in sources.items():
        package = java_package(relative)
        for name in called_names(without_native_declarations(text)):
            for native in routes.get(name, ()):
                reach.setdefault(native, set()).add(package)
    return reach


def jni_entry_methods(functions: dict[str, str]) -> dict[str, str]:
    """Map each JNI entry point's C symbol to the Java method name it implements."""
    return {name: name.rsplit("_", 1)[-1] for name in functions if name.startswith("Java_")}


def match_rule(rule: dict, name: str, header: str) -> bool:
    criteria = rule["match"]
    if "header" in criteria and criteria["header"] != header:
        return False
    if "prefix" in criteria and not name.startswith(criteria["prefix"]):
        return False
    if "symbols" in criteria and name not in criteria["symbols"]:
        return False
    if "contains" in criteria and criteria["contains"] not in name:
        return False
    return "header" in criteria or "prefix" in criteria or "symbols" in criteria \
        or "contains" in criteria


def build(cna_root: Path) -> dict:
    include = cna_root / "modules/c-api/include"
    live = inventory(include, strict=True)
    manifest = json.loads(MANIFEST.read_text(encoding="utf-8"))
    rules = json.loads(RULES.read_text(encoding="utf-8"))
    bound = {entry["name"]: entry for entry in manifest["functions"]}

    source = "\n".join(path.read_text(encoding="utf-8") for path in JNI_SOURCES)
    slot_symbol = {field: symbol for field, symbol in LOAD_PATTERN.findall(source)}
    functions = c_functions(source)
    reachable = transitive_slots(functions)

    main_sources = java_sources(MAIN_JAVA)
    test_sources = java_sources(TEST_JAVA)
    routes, native_names = internal_route_map(main_sources)
    main_reach = surface_reach(main_sources, routes)
    test_reach = surface_reach(test_sources, routes)

    # symbol -> Java surfaces and covering test packages
    symbol_surfaces: dict[str, set[str]] = {name: set() for name in bound}
    symbol_tests: dict[str, set[str]] = {name: set() for name in bound}
    symbol_methods: dict[str, set[str]] = {name: set() for name in bound}
    for entry, method in jni_entry_methods(functions).items():
        symbols = {slot_symbol[slot] for slot in reachable.get(entry, ()) if slot in slot_symbol}
        if not symbols or method not in native_names:
            continue
        surfaces = {surface_of(package) for package in main_reach.get(method, set())}
        for symbol in symbols:
            symbol_methods.setdefault(symbol, set()).add(method)
            symbol_surfaces.setdefault(symbol, set()).update(surfaces)
            symbol_tests.setdefault(symbol, set()).update(test_reach.get(method, set()))

    entries = []
    for name in sorted(live["functions"]):
        declaration = live["functions"][name]
        header = declaration["header"]
        record = {
            "name": name,
            "header": header,
            "bound": name in bound,
        }
        if name in bound:
            surfaces = symbol_surfaces.get(name, set())
            record["ownership"] = bound[name]["ownership"]
            record["javaNativeMethods"] = sorted(symbol_methods.get(name, ()))
            record["javaSurfaces"] = sorted(surfaces)
            record["coveringTestPackages"] = sorted(symbol_tests.get(name, ()))
            if "xna" in surfaces:
                record["classification"] = "XNA_BACKING"
                record["reason"] = "Reached from the strict Microsoft.Xna.Framework projection."
            elif "extension" in surfaces:
                record["classification"] = "CNA_EXTENSION_CANDIDATE"
                record["reason"] = "Reached from the org.openeggbert.cna.extensions surface."
            elif not record["javaNativeMethods"]:
                record["classification"] = "JAVA_INTERNAL_ONLY"
                record["reason"] = ("Bound and required at library-load time, but no JNI entry "
                                    "point reaches it.")
            else:
                record["classification"] = "JAVA_INTERNAL_ONLY"
                record["reason"] = ("Bound and reached only from org.openeggbert.cna.internal "
                                    "plumbing, not from a public Java surface.")
        else:
            rule = next((value for value in rules["rules"] if match_rule(value, name, header)), None)
            if rule is None:
                record["classification"] = "UNMAPPED_REQUIRES_REVIEW"
                record["reason"] = "No coverage rule explains this canonical route."
            else:
                record["classification"] = rule["classification"]
                record["reason"] = rule["reason"]
                if "task" in rule:
                    record["task"] = rule["task"]
        entries.append(record)

    counts: dict[str, int] = {}
    for record in entries:
        counts[record["classification"]] = counts.get(record["classification"], 0) + 1
    return {
        "schemaVersion": 1,
        "cnaRoot": str(cna_root),
        "abiVersion": abi_version(include),
        "cnaInventorySha256": live["inventorySha256"],
        "canonicalFunctions": len(entries),
        "boundFunctions": sum(1 for record in entries if record["bound"]),
        "classificationCounts": dict(sorted(counts.items())),
        "functions": entries,
    }


def summarize(report: dict) -> dict:
    """Reduce the per-function matrix to a committable summary.

    The full matrix is over a megabyte and changes whenever any CNA
    declaration moves, so only the summary is version-controlled -- the same
    rule CNA itself applies to its own coverage inventory.
    """
    headers: dict[str, dict[str, int]] = {}
    for record in report["functions"]:
        bucket = headers.setdefault(record["header"], {})
        bucket[record["classification"]] = bucket.get(record["classification"], 0) + 1
        bucket["total"] = bucket.get("total", 0) + 1
    tasks: dict[str, int] = {}
    for record in report["functions"]:
        if "task" in record:
            tasks[record["task"]] = tasks.get(record["task"], 0) + 1
    return {
        "schemaVersion": 1,
        "abiVersion": report["abiVersion"],
        "cnaInventorySha256": report["cnaInventorySha256"],
        "canonicalFunctions": report["canonicalFunctions"],
        "boundFunctions": report["boundFunctions"],
        "classificationCounts": report["classificationCounts"],
        "unexplainedFunctions": report["classificationCounts"].get("UNMAPPED_REQUIRES_REVIEW", 0),
        "byHeader": {name: dict(sorted(value.items())) for name, value in sorted(headers.items())},
        "deferredTasks": dict(sorted(tasks.items())),
        "boundButUnreached": sorted(
            record["name"] for record in report["functions"]
            if record["bound"] and not record.get("javaNativeMethods")),
        # A JNI entry point exists, but nothing in src/main/java calls the Java native
        # method it implements. Such a route makes the library demand a symbol from
        # libcna_c_api that no consumer can ever use. Kept separate from
        # boundButUnreached, which is the stronger failure of loading a symbol no JNI
        # entry point reaches at all.
        "boundWithoutJavaCallSite": sorted(
            record["name"] for record in report["functions"]
            if record["bound"] and record.get("javaNativeMethods")
            and not record.get("javaSurfaces")),
        "internalOnly": sorted(
            record["name"] for record in report["functions"]
            if record["classification"] == "JAVA_INTERNAL_ONLY"),
        "fullMatrixCommand":
            "python3 tools/native-abi/coverage.py --cna-root <cna> --output build/cna-c-api-coverage.json",
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--cna-root", required=True)
    parser.add_argument("--output", help="full per-function matrix (not version-controlled)")
    parser.add_argument("--summary-output", help="committable summary")
    parser.add_argument("--check", action="store_true")
    arguments = parser.parse_args()

    report = build(Path(arguments.cna_root).resolve())
    for target, payload in ((arguments.output, report),
                            (arguments.summary_output, summarize(report))):
        if not target:
            continue
        path = Path(target)
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(json.dumps(payload, indent=2, sort_keys=True) + "\n", encoding="utf-8")

    print(f"ABI_VERSION={report['abiVersion']}")
    print(f"CANONICAL_FUNCTIONS={report['canonicalFunctions']}")
    print(f"BOUND_FUNCTIONS={report['boundFunctions']}")
    for code, count in report["classificationCounts"].items():
        print(f"{code}={count}")
    summary = summarize(report)
    print(f"BOUND_BUT_UNREACHED={len(summary['boundButUnreached'])}")
    print(f"BOUND_WITHOUT_JAVA_CALL_SITE={len(summary['boundWithoutJavaCallSite'])}")
    unmapped = report["classificationCounts"].get("UNMAPPED_REQUIRES_REVIEW", 0)
    if arguments.check and summary["boundButUnreached"]:
        for name in summary["boundButUnreached"]:
            print(f"BOUND_BUT_UNREACHED={name}")
        return 1
    if arguments.check and unmapped:
        for record in report["functions"]:
            if record["classification"] == "UNMAPPED_REQUIRES_REVIEW":
                print(f"UNMAPPED={record['name']} ({record['header']})")
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
