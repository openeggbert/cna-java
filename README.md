# CNA-Java

CNA-Java exposes [CNA](https://github.com/openeggbert/cna) to the JVM through
packages matching XNA 4.0 namespaces.

```text
Java/Kotlin game
      ↓
Microsoft.Xna.Framework[.Graphics|.Input|.Content]
      ↓
org.openeggbert.cna.internal
      ↓
CNA stable C ABI
      ↓
CNA C++ Microsoft::Xna::Framework implementation
```

## Status

**Early scaffold.** The compatibility packages and first value/lifecycle types
exist. Native execution waits for CNA's canonical C ABI.

```java
import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Game;
import Microsoft.Xna.Framework.Vector2;
```

The capitalized package segments intentionally mirror the established XNA
namespace. There is no `CNA.Framework` Java package because CNA C++ has no
`CNA::Framework` namespace. Binding internals live under
`org.openeggbert.cna.internal` and are not application API.

See [architecture](docs/architecture.md) and [plan](plan.md).

## License

CNA-Java is licensed under the [Microsoft Public License](LICENSE), matching CNA.
