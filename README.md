# CNA-Java

CNA-Java exposes [CNA](https://github.com/openeggbert/cna) to the JVM with
public packages matching CNA and XNA 4.0 namespaces.

```text
Java/Kotlin game
      ↓
Microsoft.Xna.Framework compatibility packages
      ↓
CNA.Framework packages
      ↓
CNA.Interop → stable CNA C ABI → CNA C++
```

## Status

**Early scaffold.** The corrected package hierarchy and first lifecycle/value
types are present. Native execution waits for the canonical CNA C ABI.

## Public package roots

- `CNA.Framework`
- `CNA.Framework.Graphics`
- `CNA.Framework.Input`
- `CNA.Framework.Content`
- `Microsoft.Xna.Framework`
- `Microsoft.Xna.Framework.Graphics`
- `Microsoft.Xna.Framework.Input`
- `Microsoft.Xna.Framework.Content`

`CNA.Interop` is reserved for the private FFM/JNI implementation and must not
be used by applications.

```java
import Microsoft.Xna.Framework.Color;
import Microsoft.Xna.Framework.Game;
import Microsoft.Xna.Framework.Vector2;
```

The capitalized package segments are intentional: this compatibility surface
mirrors the established namespaces rather than normal Java package conventions.

See [architecture](docs/architecture.md) and [plan](plan.md).

## License

CNA-Java is licensed under the [Microsoft Public License](LICENSE), matching CNA.
