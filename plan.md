# CNA-Java implementation plan

**Status:** foundation scaffold in place

**Date:** 2026-08-16

**Sources:** `../cnabinding/analysis_binding.md`,
`../cnabinding/analysis_binding_sharp_runtime.md`, and
`../cna/analysis_binding_languages.md`

## Goal

Provide one idiomatic Java API that Java, Kotlin, and the wider JVM ecosystem
can use to drive CNA's canonical C++ engine. Preserve XNA concepts and behavior,
not awkward C# syntax or a second engine implementation.

## Phase 0 — repository scaffold (this commit)

- [x] README, plan, architecture, license, notices, editor settings, ignores.
- [x] Dependency-free Maven project targeting Java 17.
- [x] Local `Vector2`, `Color`, and `GameTime` values.
- [x] `Game` lifecycle and explicit unavailable-runtime failure.
- [x] Reserved private interop package with no guessed native declarations.

## Phase 1 — canonical native ABI

- [ ] Wait for ABI headers and implementation in `openeggbert/cna`.
- [ ] Select FFM or JNI from supported-JDK and packaging evidence, while
      keeping that choice private.
- [ ] Validate ABI version at startup and map `CNA_Result` plus error detail to
      a clear Java exception hierarchy.
- [ ] Test UTF-8, missing libraries, stale handles, double close, callbacks,
      wrong-thread calls, and shutdown order.

## Phase 2 — first playable loop

- [ ] Root Java callback objects and attach native callback threads correctly.
- [ ] Implement `GraphicsDevice`, `Texture2D`, `SpriteBatch`, `ContentManager`,
      and keyboard snapshots.
- [ ] Make owned wrappers `AutoCloseable`; distinguish owned and borrowed
      native handles; use `Cleaner` only as a last-resort safety net.
- [ ] Run HelloGame: clear, load/draw a texture, read Escape, cleanly exit.

## Phase 3 — packaging and performance

- [ ] Batch SpriteBatch commands and bulk buffer transfers.
- [ ] Package supported native binaries with predictable loading diagnostics.
- [ ] Test at least Linux and Windows and more than one supported renderer.
- [ ] Publish an experimental Maven artifact after the end-to-end sample works.

## Phase 4 — broader CNA/XNA concepts

- [ ] Complete local math/geometry and input value types.
- [ ] Incrementally add audio, fonts, effects, render targets, models, and 3D.
- [ ] Verify Kotlin consumption and publish an honest compatibility matrix.

## Invariants

1. CNA C++ stays canonical; only the CNA C ABI crosses into the JVM.
2. C++ exceptions and Sharp Runtime types never cross the boundary.
3. Strings are UTF-8, ABI primitives fixed-width, and ownership explicit.
4. Math stays local; input uses snapshots; repetitive work is batched.
5. Public Java packages expose neither raw addresses nor native result codes.
