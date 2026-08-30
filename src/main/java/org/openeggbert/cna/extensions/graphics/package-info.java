/**
 * CNA's extended graphics layer, projected for Java.
 *
 * <p>Nothing in this package is part of Microsoft XNA 4.0. It is CNA's own capability -- HDR and
 * tonemapping, render and shadow quality, physically based materials, and the post-process
 * effects CNA ships -- and it lives here rather than in {@code Microsoft.Xna.Framework} for
 * exactly that reason: the strict packages stay a faithful XNA projection, and a game that only
 * wants XNA never sees any of this.
 *
 * <p>The extended layer is an opt-in CNA build option. Its declarations exist in every build so
 * the exported ABI never changes shape, and the routes that need a native extension object
 * answer {@code NOT_SUPPORTED} when the layer is absent. {@link
 * org.openeggbert.cna.extensions.graphics.GraphicsExtension#isAvailable()} reports which build
 * is loaded; the pure value operations here work either way.
 */
package org.openeggbert.cna.extensions.graphics;
