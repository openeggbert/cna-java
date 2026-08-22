#!/usr/bin/env python3
"""Compiles signature/layout assertions and optionally checks one native library's exports."""

from __future__ import annotations

import argparse
import ctypes
import json
import os
from pathlib import Path
import subprocess
import sys
import tempfile


ROOT = Path(__file__).resolve().parents[2]


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--cna-root", default=os.environ.get("CNA_ROOT"))
    parser.add_argument("--library", default=os.environ.get("CNA_NATIVE_LIBRARY"))
    arguments = parser.parse_args()
    if not arguments.cna_root:
        print("CNA_ROOT or --cna-root is required", file=sys.stderr)
        return 2

    cna_root = Path(arguments.cna_root).resolve()
    include = cna_root / "modules/c-api/include"
    header = include / "CNA/C/cna.h"
    if not header.is_file():
        print(f"CNA C header not found: {header}", file=sys.stderr)
        return 2

    manifest = json.loads((ROOT / "tools/native-abi/bindings.json").read_text(encoding="utf-8"))
    functions = [value["name"] for value in manifest["functions"]]
    compiler = os.environ.get("CC", "cc")
    with tempfile.TemporaryDirectory(prefix="cna-java-abi-") as directory:
        output = Path(directory) / "probe.o"
        command = [compiler, "-std=c11", "-Wall", "-Wextra", "-Werror", "-pedantic",
                   "-I", str(include), "-c", str(ROOT / "tools/native-abi/probe.c"), "-o", str(output)]
        subprocess.run(command, check=True)

    print(f"HEADER_ABI={manifest['compiledAbi']}")
    print(f"BOUND_FUNCTIONS={len(functions)}")
    print("LAYOUT_SIGNATURE_PROBE=PASS")

    if not arguments.library:
        print("LIBRARY_SYMBOL_CHECK=SKIPPED (CNA_NATIVE_LIBRARY not set)")
        return 0

    library_path = Path(arguments.library).resolve()
    if not library_path.is_file():
        print(f"native library not found: {library_path}", file=sys.stderr)
        return 2
    symbols = subprocess.run(["nm", "-D", "--defined-only", str(library_path)], check=True,
                             text=True, stdout=subprocess.PIPE).stdout
    exported = {line.split()[-1] for line in symbols.splitlines() if line.split()}
    missing = sorted(set(functions) - exported)
    if missing:
        for name in missing:
            print("MISSING_SYMBOL=" + name)
        return 1

    library = ctypes.CDLL(str(library_path))
    library.cna_get_abi_version.restype = ctypes.c_uint32
    encoded = int(library.cna_get_abi_version())
    version = f"{encoded >> 16}.{(encoded >> 8) & 0xff}.{encoded & 0xff}"
    print(f"LIBRARY_ABI={version}")
    print(f"LIBRARY_SYMBOL_CHECK=PASS ({len(functions)}/{len(functions)})")
    expected = (0 << 16) | (7 << 8)
    return 0 if encoded >> 16 == 0 and encoded >= expected else 1


if __name__ == "__main__":
    raise SystemExit(main())
