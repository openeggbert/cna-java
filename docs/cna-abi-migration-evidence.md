# CNA C ABI 0.7.0 -> 0.20.0 migration evidence

**Measured:** 2026-08-30

CNA-Java's development dependency is now the live sibling `cnanext` checkout, built against the
live sibling `sharp-runtimenext` checkout. The previous baseline -- an unrelated historical CNA
checkout and a pinned `/tmp` artifact at ABI 0.7.0 -- is gone, and `build.gradle` no longer falls
back to `../cna` or `../../cna`: a missing CNA checkout is now a clear configure failure naming the
header it could not find.

## Qualified native dependency

```text
cnanext HEAD              72262a33ed5ae7657024c7f1251338748a3feee5 (branch next, clean)
sharp-runtimenext HEAD    df1b42abfcdefda030d63e97f16d2f7ea883837f (branch next)
compiler                  g++ (Debian 14.2.0-19) 14.2.0, C++23, ccache
build type                Debug, Ninja
CNA_SHARP_RUNTIME_ROOT    /rv/data/development/github.com/openeggbert/sharp-runtimenext
CNA_BUILD_C_API           ON
CNA_PLATFORM              HEADLESS
CNA_GRAPHICS_RENDERER     HEADLESS
CNA_AUDIO_PLATFORM        NULL
CNA_CNAEXT                ON
CNA_DEVICES               ON
CNA_ENABLE_NET            ON
CNA_ENABLE_VIDEO          AUTO -> enabled (FFmpeg backend cna_video_ffmpeg)
CNA_BUILD_TESTS           ON
CNA_BUILD_EXAMPLES        OFF
artifact                  cmake-build-javanext/modules/c-api/libcna_c_api.so
SHA-256                   c67b1750393584a14d33c8030ad731cd3477908040d42353fb907bcdf50d0451
reported ABI              0.20.0
exported cna_ symbols     4051
```

The build directory follows `cnanext`'s own mandatory convention -- a stable in-repo
`cmake-build-<variant>/` directory, matched by that repository's `.gitignore`. `git status` in
`cnanext` is clean and no tracked file there was modified.

## What actually changed at the boundary

The header inventory extracted from `modules/c-api/include/CNA/C/*.h` contains **exactly** the 4051
symbols the built library exports -- no header-only declaration and no undeclared export. Against
that inventory:

```text
BOUND_FUNCTIONS                723
BOUND_SYMBOL_REMOVED_UPSTREAM    0
RETURN_TYPE_DRIFT                0
PARAMETER_DRIFT                  0
HEADER_DRIFT                     3  (cna_song_get_album/_artist/_genre moved
                                     media.h -> media_library.h; manifest updated)
UNBOUND_FUNCTIONS             3328
```

Thirteen ABI minor versions moved, and every route CNA-Java binds survived them unchanged in
signature. The version number was therefore **not** the migration; three behaviour contracts were.

## Behaviour contracts that changed, and what Java now asserts

`docs/c-api/ABI_VERSIONING.md` records seven contract changes in ABI 0.9.0. Three of them reach
CNA-Java's tests, and in each case the new CNA behaviour is *closer* to the XNA reference, so the
Java expectation was corrected rather than the behaviour worked around:

| Contract | Before (0.7.0) | Now (0.20.0) | Java change |
|---|---|---|---|
| `SoundEffectInstance.Apply3D` listener count | every count but one refused with `NOT_SUPPORTED` | any positive count accepted; nearest listener decides attenuation, pan and Doppler | the test now exercises two listeners successfully; an empty array still fails |
| Non-finite sprite/transform values | refused with `INVALID_ARGUMENT` | carried into the vertex path, as the reference `SpriteBatch` does -- it validates no float | `SpriteBatch.Begin` with a NaN transform component now succeeds |
| SpriteFont glyph order | unchecked | strictly ascending characters required, because lookup is a binary search | the `.cnj` fixture is ordered `'?'` then `'A'`, and `Characters` reports that order |

## ABI compatibility policy

CNA states that ABI `0.x` is experimental and that **an incompatible change requires a
minor-version increment**. A consumer compiled against one `0.x` minor therefore cannot assume the
next one is compatible, whatever the CMake package's `SameMajorVersion` file accepts. CNA-Java's
gate is exact major **and** exact minor, with the patch component free, and the required version is
read from `tools/native-abi/bindings.json` rather than duplicated as a literal.

Regression coverage for the policy lives in `tools/native-abi/test_verify.py`: the exact version is
accepted, a differing patch is accepted, and a different major, a higher minor and a lower minor
are each rejected with their own diagnostic.

## Stale JNI declarations are now a compile error

The dispatch table used to carry 426 hand-written function-pointer typedefs. `dlsym` returns
`void*`, so a typedef that drifted from the real declaration compiled cleanly and crashed at
runtime. Every one of the 723 slots is now declared as

```c
#define CNA_JNI_ROUTE(symbol) __typeof__(&symbol)
...
    CNA_JNI_ROUTE(cna_game_run) game_run;
```

so each slot's type *is* the header's declaration and every call site is type-checked against it.
407 dead typedefs were removed; the 19 still used as local variable types remain. The whole
adapter compiles with `-std=c11 -Wall -Wextra -Werror` against the 0.20.0 headers with no
diagnostic, which is itself the proof that no bound route's signature moved.

`tools/native-abi/verify.py` additionally requires that every `CNA_JNI_ROUTE` slot names the same
symbol the matching `LOAD` line loads, so a slot cannot be declared from one route and filled from
another.

## Verification layers

```text
inventory      4051 header declarations == 4051 exported symbols
manifest       723 bound routes, exact return type and parameter types
jni            LOAD set == manifest set; every slot header-derived and consistent
layout         probe.c compiles: ABI identity, sizes, alignments, field offsets
library        723/723 symbols exported by the built libcna_c_api.so
policy         loaded ABI 0.20.0 satisfies the manifest's stated policy
tool tests     29 checks, each with a negative mutation that must fail
```

## Result

```text
./gradlew check (native enabled)      BUILD SUCCESSFUL
TESTS=156 SUITES=32 FAILURES=0 ERRORS=0 SKIPPED=0
apiCompatCheck (selected profile)     TOTAL_DIAGNOSTICS=0
nativeAbiCheck                        all layers PASS
nativeCoverageCheck                   UNMAPPED_REQUIRES_REVIEW=0
```
