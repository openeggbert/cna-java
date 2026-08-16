# Architecture

```text
Microsoft.Xna.Framework[.Graphics|.Input|.Content]
                         ↓
CNA.Framework[.Graphics|.Input|.Content]
                         ↓
CNA.Interop
                         ↓
CNA stable C ABI
                         ↓
CNA C++ core
```

The `Microsoft.Xna.Framework` tree owns XNA 4.0 compatibility. `CNA.Framework`
owns the CNA-native Java surface. Compatibility values may convert to their CNA
counterparts; native-backed objects ultimately share the same CNA handles.

Only `CNA.Interop` may use FFM, JNI, or native declarations. Public packages
must never expose addresses, C result codes, C++ exceptions, or Sharp Runtime
types. Owned GPU/audio resources will implement `AutoCloseable`; callback
rooting, JVM thread attachment, ABI versioning, UTF-8, and shutdown are part of
the binding boundary.
