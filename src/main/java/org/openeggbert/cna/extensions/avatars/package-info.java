/**
 * CNA's own avatar rendering, and the content behind XNA's canonical avatar presets.
 *
 * <p>Not part of the XNA 4.0 API. XNA's {@code AvatarRenderer} draws through the Xbox runtime and
 * names none of this: no colours it can be told to use, no clip a preset maps to, no asset a body
 * type maps to, and no way to draw anything but the runtime's own avatar. CNA answers all four,
 * and the answers live here so {@code Microsoft.Xna.Framework.GamerServices} stays exactly what
 * Microsoft shipped.
 */
package org.openeggbert.cna.extensions.avatars;
