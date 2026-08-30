# CNA-Java measured engineering plan

**Status:** the complete XNA 4.0 runtime superset is structurally at zero diagnostics

**Updated:** 2026-08-30

**Profiles:** the seven-assembly XNA 4.0 Windows runtime subset gate, and the ten-assembly full
runtime superset that adds GamerServices, Net and Avatar

**Native dependency:** `../../cnanext` built against `../../sharp-runtimenext`, CNA C ABI 0.20.0

**Runtime-qualified platform:** Linux x86-64, CNA HEADLESS platform and renderer, NULL audio

## Authority and normative boundaries

Microsoft XNA 4.0 metadata, IL, and reference behavior are authoritative. CNA-C# is the strongest
behavioral engineering comparison; CNA-TS/Rust, FNA, and MonoGame remain comparisons only. CNA C
headers are authoritative only at the native boundary.

The binding qualifies against the live sibling `cnanext`, itself built against the live sibling
`sharp-runtimenext`. It does not fall back to an unrelated CNA checkout: doing so would make every
ABI, symbol and layout result in this build describe a library nobody ships.

Structural API completeness and runtime capability are separate. Zero diagnostics means every
formally mapped type and member matches the pinned original assemblies. It does not mean every
runtime capability is present; `docs/runtime-capabilities.json` and the honest-boundaries section
of `NEXT.md` are the machine-readable and prose records of what is not.

Do not weaken the verifier, add an allowlist, expose CNA implementation types in strict packages,
or put non-XNA API inside `Microsoft.Xna.Framework.*`.

## Measured result

| Metric | Session start | Now |
|---|---:|---:|
| Selected-profile diagnostics | 0 | 0 |
| Full-profile reference types | not measured | 331 |
| Full-profile expected Java types | not measured | 340 |
| Java target types | 265 | 340 |
| Java target members | 3,206 | 4,022 |
| Full-profile diagnostics | 74 | 0 |
| Allowlist entries | 0 | 0 |
| CNA C ABI | 0.7.0 | 0.20.0 |
| Canonical C API functions | not measured | 4,051 |
| Bound native routes | 723 | 1,225 |
| Unexplained native routes | 3,328 | 0 |
| Bound routes no JNI entry point reaches | 1 | 0 |
| Bound routes no Java call site reaches | not measurable | 162 |
| CNA extension packages | 0 | 4 |
| Tests | 156 | 191 |

## Milestones reached this session

**J1, the native migration.** ABI 0.7.0 to 0.20.0 against the live dependencies. Thirteen minor
versions moved and every one of the bound routes kept its exact signature, so the version constant
was not the migration. Three documented ABI 0.9.0 behaviour contracts were, and in each case CNA
moved closer to the XNA reference: `Apply3D` accepts any positive listener count, `SpriteBatch`
carries a non-finite transform component into the vertex path as the reference does, and a
`SpriteFont`'s glyph table must be strictly ascending. Each Java expectation was corrected rather
than the behaviour worked around. `docs/cna-abi-migration-evidence.md` records the whole
measurement.

**J2 and J3, the reference corpus.** The seven-assembly profile was never the whole API. The full
ten-assembly runtime superset and the seven-assembly Content Pipeline build-time profile are now
inventoried and SHA-256 pinned from the original Microsoft assemblies. The selected profile became
a subset gate so its zero keeps meaning what it meant.

**J4, the high-fanout mapping correction.** `Dispose()` keeps its XNA name and `close()` is the
delegating bridge. That was the one place the projection dropped a legal XNA member name for Java
convention, against its own identity rule. The change is additive: 33 types gained `Dispose()` and
no member was removed. `Equals`, `GetHashCode` and `ToString` stay lowered, and the mapping
document now says why that is the one reviewed exception.

**J7, the full-profile families.** GamerServices, Avatar and Net are projected in full, backed by
CNA rather than staged, taking the full profile from 74 missing types to zero.

**J9 through J11, the CNA extension surface.** Four families outside the strict packages --
`extensions.graphics`, `extensions.runtime`, `extensions.devices` and `extensions.input` --
reach 64 routes CNA has and XNA never did. `NOT_SUPPORTED` keeps its own identity in all of them: a build without a layer
says so rather than downgrading, and an absent host fact is absent rather than zero. The template
gained an opt-in `--extensions-smoke` that proves an external consumer can reach them, and it
found a real defect on its first run.

**J8, the native inventory.** Every one of the 4,051 canonical C API functions carries an explicit
classification with zero unexplained, and the public surface that reaches each bound route is
derived from the JNI call graph and the Java sources rather than declared by hand.

## Mapping rules added this session

Each is in `tools/api-compat/mapping-rules.json` with its reason, and each is a general rule or an
explicitly reviewed full signature, never a suppression:

- `clrSerializationParameterTypes` omits every member whose whole parameter list is the CLR
  `(SerializationInfo, StreamingContext)` pair. Java has no equivalent and Java serialization is a
  different contract with different security properties.
- `omittedCovariantInterfaces` records that `GamerCollection<T>` keeps `Iterable<T>` and bounds
  `T` by `Gamer`. CLR adds a covariant `IEnumerable<Gamer>`; Java forbids two parameterizations of
  one interface, and the bound gives the same call sites what they wanted.
- `clrInternalMembers` records the two members that replace CLR `internal` access across packages:
  a parameterless protected `Gamer` constructor and a protected `GamerCollection` constructor over
  two functional interfaces. The native handle is recorded through an internal handle table
  instead of crossing a protected signature.
- `javaMapBridgeTypes` gives `PropertyDictionary` the full `java.util.Map` bridge beside its
  XNA-named members; both reach one native dictionary.
- Three `refOutMemberMappings` entries collapse `LocalNetworkGamer.ReceiveData`'s CLR
  `out NetworkGamer` into one `ReceiveResult`, and one collapses
  `PropertyDictionary.TryGetValue` to the value, as `ModelBoneCollection.TryGetValue` already did.
- A generic struct's copy constructor takes its own parameterization, the only form Java can write
  without a raw type.

## Native boundary

```text
HEADER_ABI=0.20.0
CANONICAL_FUNCTIONS=4051
BOUND_FUNCTIONS=1225
MANIFEST_SIGNATURE_CHECK=PASS
MANIFEST_JNI_BINDING_CHECK=PASS
JNI_HEADER_DERIVED_SLOT_CHECK=PASS
LAYOUT_SIGNATURE_PROBE=PASS
LIBRARY_SYMBOL_CHECK=PASS (1225/1225)
ABI_POLICY_CHECK=PASS
NATIVE_TOOL_TESTS=43 passed, 0 failed
```

Every slot is declared `CNA_JNI_ROUTE(symbol)`, so a signature that moves upstream stops the
adapter compiling instead of crashing at runtime. `tools/native-abi/generate_jni.py` derives the
mechanical half of the boundary from the headers and refuses a shape it does not understand rather
than guessing; `generateJniCheck` fails the build when the committed output goes stale.

## Coverage

```text
XNA_BACKING              976
JAVA_INTERNAL_ONLY       170
CNA_EXTENSION_CANDIDATE 1730
DEFERRED_RUNTIME         679
NOT_USEFUL_IN_JAVA       496
UNMAPPED_REQUIRES_REVIEW   0
BOUND_BUT_UNREACHED        0
BOUND_WITHOUT_JAVA_CALL_SITE 162
```

`NOT_USEFUL_IN_JAVA` is dominated by value math -- vectors, matrices, quaternions, geometry,
curves, packed vectors -- which is managed in XNA and managed here; routing it through JNI would
add cost and a native failure mode the original API cannot produce.

## Next

`docs/backlog.json` is the machine-readable backlog. The largest remaining gap is the roughly
1,700 extension candidates still unreached: CNA's engine layer, sensors, haptics, raw joysticks,
device enumeration and the `.cnb` content format. They belong under
`org.openeggbert.cna.extensions.*`, never inside the strict packages.

`boundButUnreached` -- a loaded symbol no JNI entry point reaches -- is empty and `nativeCoverageCheck`
now fails when it is not. The weaker question, whether a Java source actually *calls* the entry
point, was previously unanswerable: a `native` declaration names its own method, so the call scan
counted every generated declaration as its own call site. With that filtered,
`boundWithoutJavaCallSite` is 162, concentrated in the GamerServices and Net families, and
`JAVA-NATIVE-023` owns reaching or unbinding each one. Binding a route nothing calls makes the
library demand a symbol from `libcna_c_api` for no reason.
