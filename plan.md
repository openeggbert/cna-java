# CNA-Java measured engineering plan

**Status:** the complete XNA 4.0 runtime superset is structurally at zero diagnostics

**Updated:** 2026-08-31

**Profiles:** the seven-assembly XNA 4.0 Windows runtime subset gate, and the ten-assembly full
runtime superset that adds GamerServices, Net and Avatar

**Native dependency:** `../../cnanext` built against `../../sharp-runtimenext`, CNA C ABI 0.21.0

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
| CNA C ABI | 0.7.0 | 0.21.0 |
| Canonical C API functions | not measured | 4,054 |
| Bound native routes | 723 | 2,410 |
| Unexplained native routes | 3,328 | 0 |
| Bound routes no JNI entry point reaches | 1 | 0 |
| Bound routes no Java call site reaches | not measurable | 0 |
| Deferred input routes | 132 | 0 |
| CNA extension packages | 0 | 9 |
| Tests | 156 | 455 |

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

**J12, the input and device frontier.** `JAVA-EXT-002` and `JAVA-NATIVE-022` are done and no
input route is deferred. The audit found that none of the 132 deferred input routes was an
XNA-shaped route lacking backing -- the whole XNA input surface was already bound. 39 were CNA
capabilities XNA has no shape for and are now projected; the other 92 are value operations on
XNA's input structs, managed in XNA and managed here. The new families are the four host motion
sensors, device and sensor enumeration with hot plug, raw joysticks, force feedback with a sealed
typed effect family, and the modern game pad, keyboard, mouse and touch panel.

**J13, the string-carrying events.** `JAVA-EXT-005`. Composition drafts and candidate lists
travel over a second transport that copies each borrowed UTF-8 view inside CNA's callback and
frees it before Java sees it, and the two queues are merged by a sequence stamped when CNA raised
the event, so a committed character and the composition update that cleared it reach the game in
the order they happened.

**J14, the model graph and the .cnb container.** `JAVA-NATIVE-011` is done: `CnaModel.From`
builds CNA's own model over the buffers and effects an XNA model already owns, retained rather
than copied, and CNA draws it. `Load<Model>` stays on the managed reader because XNB records the
effect's type and CNA publishes a loaded effect as a handle whose type is only a name. CNA's own
model loader could not be used at all -- it segfaults during teardown for any asset with a mesh
part, reproduced in C as `JAVA-UPSTREAM-004`. `JAVA-EXT-003`'s first slice reads and writes
CNA's `.cnb`: container, checked byte reader, texture data, writer, and a Texture2D a game draws
with, every fixture produced by CNA's own encoder.

**J15, the Content Pipeline decision.** `JAVA-XNA-006` is measured and answered:
partial/interop, in `docs/content-pipeline-decision.md`.

**J16, the reachability sweep.** `JAVA-NATIVE-023`. Every bound route reaches a Java call site,
and both reachability facts are hard gates. It also found three leaks on the way:
`NetworkSessionProperties`, `AvatarDescription` and every `LeaderboardEntry` owned a CNA handle
that nothing released, and none of the three is disposable in XNA. They are released now on the
thread CNA will take them from.

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

## What the engine layer cost, and what it did not

The layer is 857 routes and the wrong way to spend them is mechanically. Every family here was
chosen by measuring what this runtime can actually do before any Java was written -- fourteen C
probes under `tools/native-abi/probes/`, each with its measured output recorded beside it -- and
several of those measurements ended in *not binding* something. That is the point of taking them.

The generator did most of the work and refused the rest. It grew five shapes this way: fixed
arrays inside a structure, version stamping for every element of an array of versioned structures,
a declared version prefix for a structure that grew a pointer field, a refusal for a null struct
carrier, and an optional structure whose absence must reach CNA as a null pointer rather than an
all-zero value. Each came with tool tests, and each left the refusals intact -- 70 tool tests
became 125.

Three findings went upstream, each reproduced in pure C first, and one correction came back the
other way: a "borrowed" handle whose header says it keeps its lender alive really does, and the
probe is what established that rather than the reading that led to it.

## Native boundary

```text
HEADER_ABI=0.21.0
CANONICAL_FUNCTIONS=4054
BOUND_FUNCTIONS=2410
MANIFEST_SIGNATURE_CHECK=PASS
MANIFEST_JNI_BINDING_CHECK=PASS
JNI_HEADER_DERIVED_SLOT_CHECK=PASS
LAYOUT_SIGNATURE_PROBE=PASS
LIBRARY_ABI=0.21.0
LIBRARY_SYMBOL_CHECK=PASS (2410/2410)
ABI_POLICY_CHECK=PASS
NATIVE_TOOL_TESTS=125 passed, 0 failed
ENGINE_LAYER_BOUND=778 of 857
EFFECTS_BOUND=212 of 290
```

Every slot is declared `CNA_JNI_ROUTE(symbol)`, so a signature that moves upstream stops the
adapter compiling instead of crashing at runtime. `tools/native-abi/generate_jni.py` derives the
mechanical half of the boundary from the headers and refuses a shape it does not understand rather
than guessing; `generateJniCheck` fails the build when the committed output goes stale.

Two routes are hand-written rather than generated, and both for the same reason: they take a C
function pointer, which is a shape the generator has no way to derive and must not guess one for.
They are the transparent draw list's `submit` and `draw_sorted`, and the trampoline between them
takes no global references -- the callbacks only run inside one call, so they are passed in for
its duration and the context is an index into them. The whole suite runs clean under
`-Xcheck:jni`.

## Coverage

```text
XNA_BACKING              986
JAVA_INTERNAL_ONLY         9
CNA_EXTENSION_CANDIDATE 1916
DEFERRED_RUNTIME         416
NOT_USEFUL_IN_JAVA       727
UNMAPPED_REQUIRES_REVIEW   0
BOUND_BUT_UNREACHED        0
BOUND_WITHOUT_JAVA_CALL_SITE 0
```

`NOT_USEFUL_IN_JAVA` is dominated by value math -- vectors, matrices, quaternions, geometry,
curves, packed vectors -- which is managed in XNA and managed here; routing it through JNI would
add cost and a native failure mode the original API cannot produce.

## Next

`docs/backlog.json` is the machine-readable backlog. Every gap named in the paragraph this
replaces is closed: the deferred Model routes, the Content Pipeline decision, both reachability
questions, and 206 of the 272 `.cnb` routes. What is left is smaller and each piece is recorded
with its own evidence -- the `.cnj` builder, the model's morph targets, a skinned-model slice
blocked on a marshalling shape the generator refuses rather than guesses at, and an
out-of-memory-only leak in generated multi-array routes.

CNA's engine layer is no longer the bulk of what is unbound: 778 of its 857 routes are projected
under `org.openeggbert.cna.extensions.graphics`, never inside the strict packages, and each of the
remaining 79 has a written reason rather than a plan. Thirty-eight of them are two families this
renderer refuses at construction -- compute and automatic exposure -- and about a dozen more lend
a handle whose lifetime the declaration does not state. Those are measurements, recorded in
`JAVA-EXT-009`, `JAVA-EXT-010` and `JAVA-EXT-011` with the probes that took them, not work waiting
to be typed.

The `.cnb` work is worth naming separately, because it changed what this projection is for. Eight
asset families read and write, five of them crossing to the XNA type a game uses, and `CnbImport`
reads PNG, JPEG, WAV and DDS. That is the ingest half of a content pipeline, working with no
window, no device and no frame -- which is what `docs/content-pipeline-decision.md` concluded
should exist instead of a reimplementation of Microsoft's MSBuild system.

Both reachability questions are now answered and both are gates. `boundButUnreached` -- a loaded
symbol no JNI entry point reaches -- is empty. So is `boundWithoutJavaCallSite`, the weaker
question of whether a Java source actually *calls* the entry point, which was unanswerable until
the call scan stopped counting a `native` declaration as its own call site. It measured 162, and
`JAVA-NATIVE-023` resolved every one: seven reached, 155 unbound with a stated reason. Binding a
route nothing calls makes the library demand a symbol from `libcna_c_api` for no reason.
