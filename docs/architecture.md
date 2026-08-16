# Architecture

```text
Java/Kotlin game
      ↓
org.openeggbert.cna.framework (Java-native public API)
      ↓
org.openeggbert.cna.internal (private FFM/JNI layer)
      ↓
CNA stable C ABI
      ↓
CNA C++ core → Sharp Runtime, subsystems, renderers
```

The public API preserves CNA/XNA object-oriented concepts while following JVM
conventions: Java records and `java.time` model values, failures become Java
exceptions, and expensive native resources implement `AutoCloseable` for
deterministic `try`-with-resources cleanup. `Cleaner` may become a fallback,
never the main GPU-resource lifetime mechanism.

The interop implementation will be selected only after CNA's C headers and the
minimum supported JDK are settled. Whether it uses the Foreign Function and
Memory API, JNI, or a small generated bridge is an implementation detail kept
out of the public packages.

Opaque native handles, fixed-width primitives, UTF-8, ABI-version checks,
structured native errors, callback rooting/thread attachment, snapshot input,
and batched transfers are required boundary properties.

Sharp Runtime remains an internal C++ dependency of CNA. It is not a Java
runtime requirement and none of its objects or layouts may cross this binding.
