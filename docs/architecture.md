# Architecture

```text
Microsoft.Xna.Framework compatibility packages
                       ↓
org.openeggbert.cna.internal (private FFM/JNI bridge)
                       ↓
CNA stable C ABI
                       ↓
CNA C++: Microsoft::Xna::Framework
```

The only framework-facing public package tree mirrors XNA 4.0. The internal
bridge owns ABI loading, UTF-8, result conversion, opaque handles, callback
rooting, JVM thread attachment, ownership, and shutdown.

There is deliberately no `CNA.Framework` layer. A future Java package rooted
at `CNA` is valid only for concrete extensions that mirror types actually
declared under native `CNA::...` namespaces.
