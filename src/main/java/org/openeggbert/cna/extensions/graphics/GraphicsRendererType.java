package org.openeggbert.cna.extensions.graphics;

import java.util.Objects;

/**
 * One renderer identity CNA knows how to name.
 *
 * <p>A CNA extension with no XNA counterpart at all: XNA had one renderer, on one operating
 * system, and no notion of choosing. CNA defines identities for backends spanning four decades of
 * graphics APIs, of which any given build compiles in a handful -- see
 * {@link GraphicsRenderer#available()} for which ones this build has, which is a different and
 * much shorter list.
 *
 * <p>The identities are CNA's own numbers, deliberately not consecutive: a retired backend keeps
 * its number rather than letting a later one inherit it, so a value recorded in a config file or
 * a crash report still means what it meant. {@link #UNKNOWN} is the zero value and names nothing.
 */
public enum GraphicsRendererType {

    /** No renderer; the value CNA reports when it has no answer. */
    UNKNOWN(0),
    /** SDL's own 2D renderer. */
    SDL_RENDERER(1),
    /** OpenGL ES 2.0. */
    OPENGLES2(2),
    /** OpenGL ES 3.0 and above. */
    OPENGLES3(3),
    /** Desktop OpenGL 3.3 core, which in practice receives whatever newer context the driver gives. */
    OPENGL33(4),
    /** WebGL 1. */
    WEBGL1(5),
    /** WebGL 2. */
    WEBGL2(6),
    /** bgfx. */
    BGFX(7),
    /** Vulkan. */
    VULKAN(8),
    /** WebGPU. */
    WEBGPU(9),
    /** No output at all; the renderer a test suite runs on. */
    HEADLESS(11),
    /** A CPU rasterizer. */
    SOFTWARE(12),
    /** A renderer that accepts everything and draws nothing. */
    STUB(13),
    /** Direct3D 11. */
    DIRECTX11(14),
    /** Direct3D 12. */
    DIRECTX12(15),
    /** Direct2D. */
    DIRECT2D(16),
    /** An HTML canvas. */
    CANVAS(17),
    /** The HTML DOM itself. */
    HTML_DOM(18),
    /** FreeDirect. */
    FREEDIRECT(21),
    /** Direct3D 9. */
    DIRECTX9(22),
    /** Direct3D 1. */
    DIRECTX1(23),
    /** Direct3D 2. */
    DIRECTX2(24),
    /** Direct3D 3. */
    DIRECTX3(25),
    /** Direct3D 5. */
    DIRECTX5(26),
    /** Direct3D 6. */
    DIRECTX6(27),
    /** Direct3D 7. */
    DIRECTX7(28),
    /** Direct3D 8. */
    DIRECTX8(29),
    /** Direct3D 10. */
    DIRECTX10(30),
    /** SDL's GPU API. */
    SDL_GPU(31),
    /** OpenGL ES 1.x. */
    OPENGLES1(32),
    /** Desktop OpenGL 4. */
    OPENGL4(33),
    /** Desktop OpenGL 1.x. */
    OPENGL1(34),
    /** Desktop OpenGL 2.x. */
    OPENGL2(35),
    /** 3dfx Glide. */
    GLIDE(39),
    /** Windows GDI. */
    GDI(40),
    /** Metal. */
    METAL(42),
    /** FNA3D. */
    FNA3D(43),
    /** An SVG DOM. */
    SVG_DOM(44),
    /** PortableGL. */
    PORTABLEGL(46),
    /** PixiJS. */
    PIXIJS(49);

    private final int value;

    GraphicsRendererType(int value) {
        this.value = value;
    }

    /**
     * Returns CNA's own number for this identity.
     *
     * @return the number
     */
    public int toValue() {
        return value;
    }

    /**
     * Returns the identity CNA gives one number.
     *
     * @param value CNA's number
     * @return the identity
     * @throws IllegalArgumentException when no identity has that number
     */
    public static GraphicsRendererType fromValue(long value) {
        for (GraphicsRendererType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("no CNA graphics renderer has the identity " + value);
    }

    /**
     * Returns the identity CNA parses one name into, asking CNA rather than matching here.
     *
     * <p>Matched case-insensitively against the same spellings the {@code CNA_GRAPHICS_RENDERER}
     * build option accepts. A name CNA does not recognise is an answer, not a failure: this
     * returns {@code null}.
     *
     * <p>Deliberately not {@link #valueOf}: that would answer from this enum, and this enum is a
     * copy of CNA's table rather than the table itself. A build newer than this projection would
     * still parse its own names correctly here and throw there.
     *
     * @param name the name to parse
     * @return the identity, or {@code null} when CNA does not recognise the name
     * @throws ExtensionNotSupportedException when this build has no extended runtime layer
     */
    public static GraphicsRendererType parse(String name) {
        return GraphicsRenderer.parse(Objects.requireNonNull(name, "name"));
    }
}
