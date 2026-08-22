# Architecture

## Public and implementation boundaries

```text
Microsoft.Xna.Framework.*
        strict, machine-verified Java projection
                    ↓
org.openeggbert.cna.internal.*
        private adapters, ownership, errors, JNI entry points
                    ↓
src/main/c/cna_java_jni.c
        dynamically resolved CNA stable C ABI only
                    ↓
CNA C++
```

The strict projection contains no raw address, JNI wrapper, CNA handle, or
implementation class in public/protected signatures. `tools/api-compat` checks
that boundary from compiled class metadata rather than Java source text.

There is deliberately no invented `CNA.Framework` layer. Opt-in CNA-specific
Java APIs will live under `org.openeggbert.cna.extensions` unless they project a
real native `CNA::...` public concept.

## Why JNI

The retained Java baseline is 17 and Android remains a future target. Stable
Foreign Function & Memory API is not available on Java 17, while JNI is present
on Java 17 desktop and Android. One C adapter is therefore the implemented
backend. It resolves only unmangled `cna_*` functions from the CNA C ABI; it
never binds the C++ ABI. A second backend is not justified until this path has
broader functional coverage.

Library discovery is explicit and portable:

- `cna.java.jniLibrary` or `CNA_JNI_LIBRARY` selects the adapter;
- `cna.native.library` or `CNA_NATIVE_LIBRARY` selects a CNA library;
- `CNA_NATIVE_DIR` supplies a directory containing the platform library name;
- the platform loader name is the final fallback.

The adapter owns callback global references, attaches/detaches callback threads,
copies UTF-8 at the boundary, converts fixed-width ABI types, and turns CNA
results into Java exceptions.

## Ownership

Internal handles record one of four modes: owned, borrowed, parent-owned, or
adopted/transferred. Only owned/adopted wrappers destroy native objects. Close is
explicit, deterministic, retryable after a failed native release, and
idempotent after success. Raw handle values never reach application code.

`Game` owns its content/device facades and native CNA game. Content is unloaded
while the native parent is still live, the CNA game is then destroyed, and
borrowed Java facades are invalidated. Deprecated finalization is not used.

## Contract layers

Compatibility is tracked independently as:

1. the authoritative XNA CLR reference contract;
2. the deterministic XNA Java projection produced by the mapping rules;
3. CNA-backed behavior coverage of each mapped API.

A member can therefore be mapped but unimplemented without being silently
counted as complete. The report-only verifier records the baseline; the strict
verifier stays red until all unreviewed differences are resolved.
