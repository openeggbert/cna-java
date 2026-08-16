# CNA-Java

CNA-Java is the Java/JVM language binding for
[CNA](https://github.com/openeggbert/cna), the native C++ XNA-inspired game
framework. It aims to offer familiar `Game`, `GraphicsDevice`, `Texture2D`,
`SpriteBatch`, content, and input concepts through an idiomatic Java API while
the actual engine and all renderers continue to run in CNA.

```text
Java or Kotlin game → CNA-Java → CNA stable C ABI → CNA C++ → native renderer
```

## Status

**Early scaffold.** This first commit contains project metadata, documentation,
the initial local value types, the `Game` lifecycle shape, and a reserved
private native layer. It cannot run a game yet because `openeggbert/cna` has not
implemented the stable C ABI. `Game.run()` reports that limitation explicitly.

## Design direction

- Preserve CNA/XNA concepts, with Java naming and standard-library types.
- Keep pure math and geometry in Java.
- Map native failures to `CnaException` subclasses.
- Use `AutoCloseable` and `try`-with-resources for owned native resources.
- Keep opaque handles and all FFM/JNI details in internal packages.
- Keep Sharp Runtime completely private to CNA's C++ implementation.

See [the architecture](docs/architecture.md) and [implementation plan](plan.md).

## Development

The scaffold targets Java 17 and has no third-party dependencies:

```bash
mvn test
```

A native toolchain and CNA shared library become requirements only after the
interop layer exists. Kotlin and other JVM languages will reuse the same Java
artifact rather than receive separate native bindings.

## License

CNA-Java is licensed under the [Microsoft Public License](LICENSE), matching
CNA. See [NOTICE.md](NOTICE.md) for compatibility and attribution notices.
