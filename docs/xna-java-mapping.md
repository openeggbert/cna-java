# Normative XNA 4.0 to Java mapping

This document defines the public metadata transformation used by CNA-Java. The authority on the
left is legally obtained Microsoft XNA Framework 4.0 CLR metadata. CNA headers and CNA-C# are
implementation references, not API authorities. Changes to these rules are compatibility changes
and must change `tools/api-compat/mapping-rules.json` in the same commit.

## Identity and casing

An XNA namespace becomes the identically cased Java package and a non-nested CLR type keeps its
name after removing CLR generic-arity suffixes. Nested CLR types become Java nested types. XNA
identifier spelling and casing is retained whenever Java syntax permits it. Consequently ordinary
methods remain `Run`, `Begin`, `Draw`, `Clear`, `GetState`, `CreateScale`, and so on. Java keywords
are escaped by appending `Value`; the mapping report emits `XNA_MAPPING_MISMATCH` unless the escape
is declared by the rules file.

CLR permits a generic and non-generic type with the same base name while Java does not. The
non-generic type keeps the XNA name and the colliding generic type deterministically receives an
`OfT` suffix (currently `ContentTypeReaderOfT<T>` and `IPackedVectorOfT<T>`). Every collision and
rename is explicit in `mapping-rules.json`; silently collapsing two CLR types is forbidden.

Public XNA fields remain public Java fields with the same names. Ordinary enum types become Java
enums and their members keep XNA spelling (`Keys.Escape`). CLR numeric values use Java ordinals
when they are exactly sequential; otherwise the enum receives `getValue()` and compiled-metadata
inspection verifies every named number.

CLR `[Flags]` enums cannot be Java enums because Java enum instances cannot represent unnamed bit
combinations. They therefore become final immutable value classes with same-cased named constants,
`getValue()`, `FromValue(value)`, `Or(other)`, and `Contains(value)`. Equality and hashing use the
underlying bit set. Thus `SpriteEffects.None` remains recognizable while a combined flags value is
representable without an invalid pseudo-enum constant. The CLR extractor records `FlagsAttribute`
directly; this transformation is not inferred from names.

An ordinary CLR enum remains a Java enum. Java cannot reproduce C# casts that manufacture an
unnamed numeric enum value. Numeric values of all declared constants are preserved and verified;
an API that can observe undeclared native values must specify its deterministic adaptation. For
`KeyboardState`, all 256 native bits remain part of snapshot equality and hashing, while
`GetPressedKeys()` can return only the 160 declared `Keys` constants. This is a recorded Java
language limitation, not permission to renumber or invent enum members.

## Properties and indexers

Instance and static properties use one rule:

```text
Foo { get; }       -> getFoo()
Foo { get; set; }  -> getFoo(), setFoo(value)
IsMouseVisible     -> getIsMouseVisible(), setIsMouseVisible(value)
```

Accessor visibility is mapped independently. Internal/private accessors do not become public.
Indexer properties become `get(index...)` and, when settable, `set(index..., value)`. A get-only
static value property may additionally have a same-cased named field only when the mapped value is
immutable; this is an explicit rule, never inferred ad hoc. The strict verifier treats any such
field not declared in `mapping-rules.json` as unexpected.

## Methods, operators, overloads, and defaults

Methods preserve XNA spelling, overloads, generic arity, static/abstract state, overridability,
parameter order, and mapped types. A CLR non-virtual instance method is a Java `final` method when
its declaring class remains extensible. In a Java `final` class, the class modifier already makes
every instance method non-overridable, so a redundant method-level `final` flag is not required and
the verifier compares effective overridability. Static methods and fields retain their literal
modifier state. Java has no optional-parameter metadata equivalent, so each callable XNA arity
becomes an overload. Default values are recorded in the neutral contract even when they do not
change the Java descriptor.

An operator is mapped to the identically purposed named XNA method (`op_Addition` to `Add`,
`op_Multiply` to `Multiply`, and so on) and is deduplicated when that method already exists.
Equality operators map to `equals(Object)`/`hashCode()`. Conversion operators map to deterministic
`from<Type>` static factories and `to<Type>` instance methods. An operator with no rule is a hard
`XNA_MAPPING_MISMATCH`, not a guessed API.

The redundant XNA performance overload pattern whose only difference is `ref` inputs plus one
`out` result maps to the ordinary return-value overload and is deduplicated. Other `ref`/`out`
parameters map to `org.openeggbert.cna.extensions.Ref<T>` and `Out<T>` only when an explicit rule
marks the transformation. The strict packages never expose these extension holders without that
rule.

`Matrix.Decompose(out scale, out rotation, out translation)` is the first explicit multi-output
rule: Java receives `Matrix.Decomposition Decompose()`, whose result records the XNA boolean and
the three output values. XNA `ContentManager.Load<T>(name)` receives the class-token parameter
required by erasure: `Load(Class<T> assetType, String assetName)`.

The parameterless `IDisposable.Dispose()` contract maps to `close()`. A distinct protected
`Dispose(boolean)` lifetime hook keeps its XNA name as `Dispose(boolean)`. CLR finalization is not
projected: Java explicit cleanup is normative and deprecated Java finalization must not be added.
When CLR implements `IDisposable.Dispose` explicitly, Java still requires a public `close()` to
implement `AutoCloseable`; the verifier synthesizes that member deterministically and checks it as
non-overridable for a concrete implementation.

## CLR and framework types

The core deterministic mappings are:

```text
System.Boolean/SByte/Int16/Int32/Int64/Single/Double/Char -> Java primitives
System.Byte                                              -> int (validated 0 through 255)
System.String                                            -> java.lang.String
System.Object                                            -> java.lang.Object
System.Exception                                         -> java.lang.RuntimeException
System.Type                                              -> java.lang.Class<?>
System.IntPtr                                            -> Microsoft.Xna.Framework.WindowHandle
System.TimeSpan                                          -> java.time.Duration (100 ns precision)
System.IDisposable                                       -> java.lang.AutoCloseable
System.Collections.Generic.IEnumerable<T>                -> java.lang.Iterable<T>
System.Collections.Generic.IList<T>                      -> java.util.List<T>
System.Nullable<T>                                       -> boxed T or Optional<T>, by explicit rule
System.EventArgs                                         -> Microsoft.Xna.Framework.EventArgs
System.EventHandler<T>                                   -> Microsoft.Xna.Framework.EventHandler<T>
System.IServiceProvider                                  -> Microsoft.Xna.Framework.ServiceProvider
System.Collections.ObjectModel.Collection<T>             -> java.util.AbstractList<T>
System.Collections.Generic.Dictionary<K,V>               -> java.util.LinkedHashMap<K,V>
```

At a `TimeSpan` API boundary, `Duration` is normalized downward to the nearest
100-nanosecond CLR tick; a value outside the signed `TimeSpan` tick range is rejected.
Individual XNA properties retain their own range rules (for example, positive target elapsed time
and non-negative inactive sleep time). `RuntimeException` is used for CLR `Exception` because a
checked Java base exception would introduce call-site obligations absent from the CLR contract.
Unsigned CLR `Byte` deliberately does not become signed Java `byte`: values 128 through 255 must
remain numerically observable, so it projects to `int` and setters/constructors validate the XNA
0-through-255 domain. This is the same width-preserving policy used for other unsigned CLR values
whose full range a same-width Java primitive cannot represent.
The protected `ContentLoadException(SerializationInfo, StreamingContext)` constructor has no Java
source or serialization-protocol equivalent and is explicitly excluded by its full CLR signature.
Java exception serialization instead uses `RuntimeException`'s serial form and `serialVersionUID`;
the three ordinary public constructors remain part of the strict mapped contract. This is a
mapping rule, not an allowlist entry.
`WindowHandle` is an opaque value that supports equality and a zero test but intentionally has no
numeric/address accessor. It preserves the XNA window-token round trip without exposing a raw
native address to game code; CNA-specific native-window interop belongs in the extensions layer.

XNA collections that are fixed-size or read-only use dedicated facade types or unmodifiable Java
views; mapping to `List<T>` does not grant mutation that XNA refused. Generic bounds are preserved
where the Java type system can express them. CLR attributes with behavioral or contract meaning
map to annotations listed in the rules file; other attributes remain recorded as unmapped
diagnostics until reviewed.

## Delegates and events

An XNA delegate becomes a same-package `@FunctionalInterface`; its `Invoke` signature becomes
`invoke`. The standard CLR `EventHandler<TEventArgs>` delegate maps once to the synthetic,
machine-declared compatibility interface `Microsoft.Xna.Framework.EventHandler<TEventArgs>`.
`System.EventArgs` similarly maps to the small `Microsoft.Xna.Framework.EventArgs` compatibility
value. These synthetic Java necessities are exact contracts in `mapping-rules.json`, not
allowlisted unexpected types.

An event `Foo` maps to `addFooListener(EventHandler<TEventArgs>)` and
`removeFooListener(EventHandler<TEventArgs>)`. Listener invocation order is registration order,
duplicate registrations remain duplicate, removal removes one matching registration, and listener
mutation during dispatch observes a stable snapshot. Native callback pointers and contexts are
always hidden below `org.openeggbert.cna.internal`.

## Value types and copying

CLR structs become value-oriented Java classes. Mutable public fields stay mutable where required,
but Java assignment aliases an object instead of copying a struct. This unavoidable difference is
never described as source-semantic identity. Constructors, collection insertion, retained
properties, and every managed/native boundary snapshot mutable values so later caller mutation
cannot alter the already-passed value. Value classes implement XNA-aware `equals` and `hashCode`;
floating-point equality treats NaNs and signed zero as CLR `Single.Equals` does.

Get-only static struct properties return fresh objects when their Java representation is mutable.
Immutable named values may be public same-cased constants. Native handles, memory addresses, JNI
types, and implementation adapters are not part of a value type's public contract.

## Lifetime and unsupported behavior

`IDisposable` maps to `AutoCloseable`; `close()` is idempotent for CNA-owned resources. Owned,
borrowed, parent-owned, and adopted handles are distinct internal states. Explicit cleanup is the
contract; no Java finalizer is used. A Cleaner may only be a safety net.

An API may be shape-present while returning a documented deterministic unsupported exception.
That is implementation coverage, not API-contract completeness, and is tracked separately from
metadata. CNA-specific renderer diagnostics and capabilities live under
`org.openeggbert.cna.extensions`, never under `Microsoft.Xna.Framework.*`.
