# CNA-Java measured engineering plan

**Status:** the complete XNA 4.0 runtime superset is structurally at zero diagnostics

**Updated:** 2026-08-31

**Profiles:** the seven-assembly XNA 4.0 Windows runtime subset gate, and the ten-assembly full
runtime superset that adds GamerServices, Net and Avatar

**Native dependency:** `../../cnanext` built against `../../sharp-runtimenext`, CNA C ABI 0.21.0

**Runtime-qualified platform:** Linux x86-64, CNA SDL3 platform, NULL audio, and five renderers
compiled into one library and chosen at run time: HEADLESS, SOFTWARE, OPENGL4, OPENGLES3 and
OPENGL33. The whole suite runs green on each of the five, on the host display and on a virtual one.

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
| Full-profile diagnostics | 0 | 0 |
| Allowlist entries | 0 | 0 |
| CNA C ABI | 0.21.0 | 0.21.0 |
| Canonical C API functions | 4,054 | 4,054 |
| Bound native routes | 2,410 | 2,510 |
| Engine-layer routes bound | 778 of 857 | 844 of 857 |
| Effects routes bound | 212 of 290 | 241 of 290 |
| Unexplained native routes | 0 | 0 |
| Bound routes no JNI entry point reaches | 0 | 0 |
| Bound routes no Java call site reaches | 0 | 0 |
| Renderers qualified | 1 | 5 |
| Compute-capable renderers | 0 | 2 |
| Native tool tests | 125 | 146 |
| Tests | 455 | 504 |

## Milestones reached this session

**G1, the renderer qualification.** Every engine-layer measurement this projection had was taken
on the HEADLESS renderer, which compiles no shader and reads back no pixel, and three families
were recorded as blocked on that fact rather than on CNA. A multi-renderer CNA build settles what
was actually blocked: five renderers in one library, one chosen before the first `GraphicsDevice`
through an environment variable.

The answer was not the one the previous handoff predicted. It named desktop GL 4.3 as the
high-value target; the compute-capable renderers here are `OPENGLES3`, which gets an OpenGL ES 3.2
context, and `OPENGL33`, which asks Mesa for a 3.3 core profile and is handed 4.6 on the host's
AMD GPU or 4.5 on llvmpipe. `OPENGL4` -- the renderer whose *name* suggested it -- has no compute
at all: CNA's compute lives in the EasyGL family, which `OPENGL4` is not part of. And the shading
dialect is GLSL ES rather than desktop GLSL, because every shader inside CNA's own engine layer
is written in it.

**G2, compute and automatic exposure.** `JAVA-EXT-012` and `JAVA-EXT-011`, thirty-nine routes.
`ComputeShader`, `StorageBuffer`, `MemoryBarrier`, `ImageAccess`, `IndirectDraw` and the two
indirect-argument value types over the GPU's own wire format; `AutoExposure` and its whole control
loop. Neither is qualified by a call succeeding: four known integers go to the GPU and come back
doubled and offset, and the meter is handed greys of 8/255 and 240/255 and returns those
luminances to within a thousandth.

**G3, the borrowed handles.** `JAVA-EXT-010`, twenty-two routes, and the reason it took
measurement rather than reading: one word covered two opposite contracts. A *counted* borrow
blocks its lender's destruction until it is given back; a *retaining* one keeps its lender alive
and may outlive it. Seven of each kind, measured in C before a facade was written, and nothing
dangles.

**G4, the callbacks.** The light-probe baker's three bake routes and the render pipeline's two
scene callbacks, both hand-written because CNA takes a C function pointer, and treated as the
opposite lifetimes they are: a bake callback lives only inside the call and pins nothing; a
pipeline scene callback is registered once and runs in every later frame, so it is pinned by an
explicit token the pipeline owns. The pipeline pair also corrected an earlier reading -- they were
recorded as blocked on the renderer and are gated by the settings, and run on HEADLESS too.

**G5, the shader effect.** Twenty-three routes, and the family that made two others useful:
`ShaderEffectFactory` compiled and cached an effect and `FullscreenPass` drew through one, and
nothing could give that effect a value to work with. Qualified by a pixel -- a fragment shader
that writes nothing but a uniform -- which is also how `JAVA-UPSTREAM-016` was found.

**G6, the pixels.** Two values crossed into CNA and never came back, and a planted swap of one of
them passed every test this projection had. On a renderer that draws, a filter is a pixel: a
two-by-two checkerboard magnified is hard blocks under point filtering and a gradient under
linear, and the same planted swap now fails. The post-process context is verified in three of its
fields and the limit is stated rather than glossed.

**G7, five upstream findings**, each reproduced in pure C before it was claimed: a compute shader
that does not compile reported as an internal failure; one shadow map of four that does not count
what it lends; a process that exits with a live vertex buffer aborting on EasyGL; a post-process
pass that reports itself unsupported and runs anyway; and a shader uniform set before the effect
is applied being silently discarded. A sixth, `JAVA-UPSTREAM-007`, was widened rather than closed.

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
BOUND_FUNCTIONS=2510
MANIFEST_SIGNATURE_CHECK=PASS
MANIFEST_JNI_BINDING_CHECK=PASS
JNI_HEADER_DERIVED_SLOT_CHECK=PASS
LAYOUT_SIGNATURE_PROBE=PASS
LIBRARY_ABI=0.21.0
LIBRARY_SYMBOL_CHECK=PASS (2510/2510)
ABI_POLICY_CHECK=PASS
NATIVE_TOOL_TESTS=146 passed, 0 failed
ENGINE_LAYER_BOUND=844 of 857
EFFECTS_BOUND=241 of 290
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
CNA_EXTENSION_CANDIDATE 1927
DEFERRED_RUNTIME         405
NOT_USEFUL_IN_JAVA       727
UNMAPPED_REQUIRES_REVIEW   0
BOUND_BUT_UNREACHED        0
BOUND_WITHOUT_JAVA_CALL_SITE 0
```

`NOT_USEFUL_IN_JAVA` is dominated by value math -- vectors, matrices, quaternions, geometry,
curves, packed vectors -- which is managed in XNA and managed here; routing it through JNI would
add cost and a native failure mode the original API cannot produce.

## Next

`docs/backlog.json` is the machine-readable backlog, and what is left in it is external.

The engine layer is 844 of its 857 routes. The thirteen that remain are not waiting on a
renderer: eight are getters whose answer Java already holds and would leak a facade per call, one
is a pure value test `java.util.EnumSet` answers for nothing, one is a non-owning view
`cna_effect_destroy` refuses and no Java facade can own, one mints a handle that must not be
released, and one makes a game undestroyable -- `JAVA-UPSTREAM-011`, still reproduced on the live
CNA.

Eight upstream findings are open, six of them opened or widened here and every one reproduced in
pure C before it was claimed. Three are the same shape and worth naming as one: a capability query
that does not predict the behaviour, in the cube shadow map's face passes, the clustered lighting
routes' documented parameter and a post-process pass's own support answer. Two more are exception
barriers flattening a refusal a game could act on into the result code that also means a defect
inside CNA.

`JAVA-EXT-007` was rechecked against the live headers and has gained no new door: a clip still
enters a skinned model only through a descriptor pointer graph. `JAVA-UPSTREAM-004` was retaken
and the content manager's model teardown still segfaults, which is why `CnaModel.Load` does not
exist. `cna_content_manager_load_effect` is `ASSET_PENDING` rather than blocked: no `.xnb` effect
and no `.cnj` describing one exists in the checkout this qualifies against.

What would genuinely unblock more is external. A GPU whose timer query answers with a duration
rather than a sentinel would let the GPU timer be qualified as a measurement rather than as a
protocol. A licensed authored XACT bank, a second machine for a real network session, and an
authored `.xnb` effect are each an asset or a host this qualification does not have.
