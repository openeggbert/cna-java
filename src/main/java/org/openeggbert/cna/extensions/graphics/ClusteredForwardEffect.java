package org.openeggbert.cna.extensions.graphics;

import Microsoft.Xna.Framework.Graphics.GraphicsDevice;
import Microsoft.Xna.Framework.Graphics.Texture2D;
import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Vector3;
import Microsoft.Xna.Framework.Graphics.Effect;
import org.openeggbert.cna.internal.FacadeFactory;
import org.openeggbert.cna.internal.NativeBindings;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.Objects;

/**
 * The shader that reads a clustered light buffer and shades a surface with it.
 *
 * <p>A CNA extension, and where the whole lighting stack arrives: a {@link ClusteredLightSet}
 * sorted into a {@link ClusteredLightGrid} by a {@link ClusteredLightAssignment} and uploaded
 * into a {@link ClusteredLightBuffer} is read here, per pixel, along with an {@link AreaLight},
 * a {@link LightProbe} or a {@link LightProbeVolume} for indirect light, and a
 * {@link PbrMaterialExtensions} for whatever the material is made of.
 *
 * <p>{@link #contribution} is the shading itself as a pure function -- one light, one surface,
 * the material's numbers, and the colour that comes out. No device, no frame: a game can check
 * what a light will do to a material before drawing anything, and a tool can plot it.
 *
 * <p><strong>Everything set here is borrowed and retained.</strong> The effect names a light
 * probe, a volume, a BRDF table, a material and an opaque frame; it owns none of them. This
 * object holds a Java reference to each so nothing collects one while the effect names it, and
 * closing it disposes none of them.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class ClusteredForwardEffect implements AutoCloseable {

    private final long handle;
    // The Java side of each borrowed slot: what keeps the object alive, and what a getter can
    // honestly return. CNA's own getters mint fresh borrowed handles, so asking it would
    // allocate and free one per read -- the same reasoning PbrMaterialExtensions records.
    private LightProbe probe;
    private LightProbeVolume volume;
    private AreaLight areaLight;
    private AreaLightBrdfTable brdfTable;
    private PbrMaterialExtensions materialExtensions;
    private Texture2D opaqueFrame;
    private boolean closed;

    private ClusteredForwardEffect(long handle) {
        this.handle = handle;
    }

    /**
     * Creates the effect on a device.
     *
     * @param graphicsDevice the device to compile on
     * @return the effect, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static ClusteredForwardEffect create(GraphicsDevice graphicsDevice) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] effect = new long[1];
        GraphicsExtension.check("ClusteredForwardEffect.create",
                NativeEngineLayerRoutes.clusteredForwardEffectCreate(
                        NativeBindings.nativeGraphicsDeviceValue(graphicsDevice), effect));
        return new ClusteredForwardEffect(effect[0]);
    }

    /**
     * Returns what one clustered light contributes to a surface.
     *
     * <p>The shading itself, on the CPU, with every material parameter the shader takes.
     * {@link #contribution(ClusteredLight, Vector3, Vector3, Vector3, Vector3, float, float,
     * PbrMaterialExtensions)} is the same thing with the extensions carrying the last eight.
     *
     * @param light the light
     * @param surface the world-space point being lit
     * @param normal the surface normal
     * @param cameraPosition where the camera is
     * @param baseColor the surface's base colour
     * @param metallic how metallic the surface is
     * @param roughness how rough it is
     * @param clearcoat the clearcoat factor
     * @param clearcoatRoughness the clearcoat's roughness
     * @param sheenColor the sheen colour
     * @param sheenRoughness the sheen's roughness
     * @param iridescence the iridescence factor
     * @param iridescenceIor the iridescent film's index of refraction
     * @param iridescenceThickness the film's thickness in nanometres
     * @param subsurfaceColor the subsurface colour
     * @param subsurfaceWrap how far light wraps around the surface
     * @return the contribution per channel
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Vector3 contribution(ClusteredLight light, Vector3 surface, Vector3 normal,
            Vector3 cameraPosition, Vector3 baseColor, float metallic, float roughness,
            float clearcoat, float clearcoatRoughness, Vector3 sheenColor, float sheenRoughness,
            float iridescence, float iridescenceIor, float iridescenceThickness,
            Vector3 subsurfaceColor, float subsurfaceWrap) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(light, "light");
        float[] contribution = new float[3];
        GraphicsExtension.check("ClusteredForwardEffect.contribution",
                NativeEngineLayerRoutes.clusteredForwardEffectContribution(new byte[3],
                        light.integral(), light.floating(),
                        EngineValues.floats(surface, "surface"),
                        EngineValues.floats(normal, "normal"),
                        EngineValues.floats(cameraPosition, "cameraPosition"),
                        EngineValues.floats(baseColor, "baseColor"), metallic, roughness,
                        clearcoat, clearcoatRoughness,
                        EngineValues.floats(sheenColor, "sheenColor"), sheenRoughness,
                        iridescence, iridescenceIor, iridescenceThickness,
                        EngineValues.floats(subsurfaceColor, "subsurfaceColor"), subsurfaceWrap,
                        contribution));
        return new Vector3(contribution[0], contribution[1], contribution[2]);
    }

    /**
     * Returns what one clustered light contributes, with the material's extensions.
     *
     * @param light the light
     * @param surface the world-space point being lit
     * @param normal the surface normal
     * @param cameraPosition where the camera is
     * @param baseColor the surface's base colour
     * @param metallic how metallic the surface is
     * @param roughness how rough it is
     * @param extensions the material's glTF extensions
     * @return the contribution per channel
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Vector3 contribution(ClusteredLight light, Vector3 surface, Vector3 normal,
            Vector3 cameraPosition, Vector3 baseColor, float metallic, float roughness,
            PbrMaterialExtensions extensions) {
        GraphicsExtension.requireBackend();
        Objects.requireNonNull(light, "light");
        Objects.requireNonNull(extensions, "extensions");
        float[] contribution = new float[3];
        GraphicsExtension.check("ClusteredForwardEffect.contribution",
                NativeEngineLayerRoutes.clusteredForwardEffectContributionWithExtensions(
                        new byte[3], light.integral(), light.floating(),
                        EngineValues.floats(surface, "surface"),
                        EngineValues.floats(normal, "normal"),
                        EngineValues.floats(cameraPosition, "cameraPosition"),
                        EngineValues.floats(baseColor, "baseColor"), metallic, roughness,
                        extensions.handle(), contribution));
        return new Vector3(contribution[0], contribution[1], contribution[2]);
    }

    /**
     * Returns how much light survives a thickness of an absorbing volume.
     *
     * <p>Beer's law, which is what makes thick glass green and thin glass clear.
     *
     * @param attenuationColor the colour that survives one attenuation distance
     * @param attenuationDistance the distance that colour is quoted at
     * @param thickness how far the light travels through the volume
     * @return the surviving fraction per channel
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static Vector3 volumeAttenuation(Vector3 attenuationColor, float attenuationDistance,
            float thickness) {
        GraphicsExtension.requireBackend();
        float[] attenuation = new float[3];
        GraphicsExtension.check("ClusteredForwardEffect.volumeAttenuation",
                NativeEngineLayerRoutes.clusteredForwardEffectVolumeAttenuation(
                        EngineValues.floats(attenuationColor, "attenuationColor"),
                        attenuationDistance, thickness, attenuation));
        return new Vector3(attenuation[0], attenuation[1], attenuation[2]);
    }

    /**
     * Reports whether this renderer can run the effect.
     *
     * @return whether the shader exists and links
     */
    public boolean isSupported() {
        boolean[] supported = new boolean[1];
        GraphicsExtension.check("ClusteredForwardEffect.isSupported",
                NativeEngineLayerRoutes.clusteredForwardEffectIsSupported(open(), supported));
        return supported[0];
    }

    /**
     * Binds the effect for a draw.
     *
     * @param world the object's world transform
     * @param view the camera's view matrix
     * @param projection the camera's projection matrix
     * @param cameraPosition where the camera is
     * @param lights the uploaded clustered lights, or {@code null} for none
     */
    public void begin(Matrix world, Matrix view, Matrix projection, Vector3 cameraPosition,
            ClusteredLightBuffer lights) {
        GraphicsExtension.check("ClusteredForwardEffect.begin",
                NativeEngineLayerRoutes.clusteredForwardEffectBegin(open(),
                        EngineValues.floats(world, "world"),
                        EngineValues.floats(view, "view"),
                        EngineValues.floats(projection, "projection"),
                        EngineValues.floats(cameraPosition, "cameraPosition"),
                        lights == null ? 0L : lights.handleForBorrow()));
    }

    /** @return the surface's base colour */
    public Vector3 getBaseColor() {
        return vector("ClusteredForwardEffect.getBaseColor",
                NativeEngineLayerRoutes::clusteredForwardEffectGetBaseColor);
    }

    /**
     * @param color the surface's base colour
     */
    public void setBaseColor(Vector3 color) {
        GraphicsExtension.check("ClusteredForwardEffect.setBaseColor",
                NativeEngineLayerRoutes.clusteredForwardEffectSetBaseColor(open(),
                        EngineValues.floats(color, "color")));
    }

    /** @return how metallic the surface is */
    public float getMetallic() {
        return number("ClusteredForwardEffect.getMetallic",
                NativeEngineLayerRoutes::clusteredForwardEffectGetMetallic);
    }

    /**
     * @param metallic how metallic the surface is
     */
    public void setMetallic(float metallic) {
        GraphicsExtension.check("ClusteredForwardEffect.setMetallic",
                NativeEngineLayerRoutes.clusteredForwardEffectSetMetallic(open(), metallic));
    }

    /** @return how rough the surface is */
    public float getRoughness() {
        return number("ClusteredForwardEffect.getRoughness",
                NativeEngineLayerRoutes::clusteredForwardEffectGetRoughness);
    }

    /**
     * @param roughness how rough the surface is
     */
    public void setRoughness(float roughness) {
        GraphicsExtension.check("ClusteredForwardEffect.setRoughness",
                NativeEngineLayerRoutes.clusteredForwardEffectSetRoughness(open(), roughness));
    }

    /** @return the surface's index of refraction */
    public float getIor() {
        return number("ClusteredForwardEffect.getIor",
                NativeEngineLayerRoutes::clusteredForwardEffectGetIor);
    }

    /**
     * @param ior the surface's index of refraction
     */
    public void setIor(float ior) {
        GraphicsExtension.check("ClusteredForwardEffect.setIor",
                NativeEngineLayerRoutes.clusteredForwardEffectSetIor(open(), ior));
    }

    /** @return the constant ambient term added to every surface */
    public Vector3 getAmbient() {
        return vector("ClusteredForwardEffect.getAmbient",
                NativeEngineLayerRoutes::clusteredForwardEffectGetAmbient);
    }

    /**
     * @param ambient the constant ambient term
     */
    public void setAmbient(Vector3 ambient) {
        GraphicsExtension.check("ClusteredForwardEffect.setAmbient",
                NativeEngineLayerRoutes.clusteredForwardEffectSetAmbient(open(),
                        EngineValues.floats(ambient, "ambient")));
    }

    /**
     * Gives the effect an area light and the table it is shaded through.
     *
     * <p>Both are borrowed; the table is retained here so it cannot be collected while the
     * effect names it.
     *
     * @param light the area light
     * @param table the BRDF table
     */
    public void setAreaLight(AreaLight light, AreaLightBrdfTable table) {
        Objects.requireNonNull(light, "light");
        Objects.requireNonNull(table, "table");
        GraphicsExtension.check("ClusteredForwardEffect.setAreaLight",
                NativeEngineLayerRoutes.clusteredForwardEffectSetAreaLight(open(), new byte[3],
                        light.integral(), light.floating(), table.handle()));
        synchronized (this) {
            areaLight = light;
            brdfTable = table;
        }
    }

    /**
     * Returns the area light the effect was given.
     *
     * @return the light, or {@code null} when there is none
     */
    public synchronized AreaLight getAreaLight() {
        open();
        return areaLight;
    }

    /**
     * Reports whether the effect has an area light.
     *
     * @return CNA's own answer
     */
    public boolean hasAreaLight() {
        return flag("ClusteredForwardEffect.hasAreaLight",
                NativeEngineLayerRoutes::clusteredForwardEffectHasAreaLight);
    }

    /** Forgets the area light. */
    public void clearAreaLight() {
        GraphicsExtension.check("ClusteredForwardEffect.clearAreaLight",
                NativeEngineLayerRoutes.clusteredForwardEffectClearAreaLight(open()));
        synchronized (this) {
            areaLight = null;
            brdfTable = null;
        }
    }

    /**
     * Gives the effect one light probe for indirect light.
     *
     * @param value the probe, borrowed and retained here
     */
    public void setLightProbe(LightProbe value) {
        Objects.requireNonNull(value, "probe");
        GraphicsExtension.check("ClusteredForwardEffect.setLightProbe",
                NativeEngineLayerRoutes.clusteredForwardEffectSetLightProbe(open(),
                        value.handle()));
        synchronized (this) {
            probe = value;
            volume = null;
        }
    }

    /**
     * Gives the effect a probe volume for indirect light.
     *
     * @param value the volume, borrowed and retained here
     */
    public void setLightProbeVolume(LightProbeVolume value) {
        Objects.requireNonNull(value, "volume");
        GraphicsExtension.check("ClusteredForwardEffect.setLightProbeVolume",
                NativeEngineLayerRoutes.clusteredForwardEffectSetLightProbeVolume(open(),
                        value.handle()));
        synchronized (this) {
            volume = value;
            probe = null;
        }
    }

    /**
     * Returns the probe the effect was given.
     *
     * @return the probe, or {@code null} when it was given a volume or nothing
     */
    public synchronized LightProbe getLightProbe() {
        open();
        return probe;
    }

    /**
     * Returns the probe volume the effect was given.
     *
     * @return the volume, or {@code null} when it was given a probe or nothing
     */
    public synchronized LightProbeVolume getLightProbeVolume() {
        open();
        return volume;
    }

    /**
     * Reports whether the effect has indirect light.
     *
     * @return CNA's own answer
     */
    public boolean hasLightProbe() {
        return flag("ClusteredForwardEffect.hasLightProbe",
                NativeEngineLayerRoutes::clusteredForwardEffectHasLightProbe);
    }

    /** Forgets the light probe or volume. */
    public void clearLightProbe() {
        GraphicsExtension.check("ClusteredForwardEffect.clearLightProbe",
                NativeEngineLayerRoutes.clusteredForwardEffectClearLightProbe(open()));
        synchronized (this) {
            probe = null;
            volume = null;
        }
    }

    /**
     * Gives the effect the material's glTF extensions.
     *
     * <p><strong>There is no way to say "none".</strong> CNA refuses an invalid handle here, so
     * a material with no extensions is a neutral {@link PbrMaterialExtensions} -- which is what
     * {@link PbrMaterialExtensions#create()} makes and what
     * {@link PbrMaterialExtensions#isNeutral()} reports.
     *
     * @param extensions the extensions, borrowed and retained here
     */
    public void setMaterialExtensions(PbrMaterialExtensions extensions) {
        Objects.requireNonNull(extensions, "extensions");
        GraphicsExtension.check("ClusteredForwardEffect.setMaterialExtensions",
                NativeEngineLayerRoutes.clusteredForwardEffectSetMaterialExtensions(open(),
                        extensions.handle()));
        synchronized (this) {
            materialExtensions = extensions;
        }
    }

    /**
     * Returns the material extensions the effect was given.
     *
     * @return the extensions, or {@code null} when there are none
     */
    public synchronized PbrMaterialExtensions getMaterialExtensions() {
        open();
        return materialExtensions;
    }

    /**
     * Gives the effect the opaque frame a transmissive material refracts.
     *
     * @param frame the frame drawn so far, borrowed and retained here, or {@code null} for none
     */
    public void setOpaqueFrame(Texture2D frame) {
        GraphicsExtension.check("ClusteredForwardEffect.setOpaqueFrame",
                NativeEngineLayerRoutes.clusteredForwardEffectSetOpaqueFrame(open(),
                        frame == null ? 0L : NativeBindings.nativeResourceHandle(frame)));
        synchronized (this) {
            opaqueFrame = frame;
        }
    }

    /**
     * Returns the opaque frame the effect was given.
     *
     * @return the frame, or {@code null} when there is none
     */
    public synchronized Texture2D getOpaqueFrame() {
        open();
        return opaqueFrame;
    }

    /**
     * Returns the shader effect this one shades with, borrowed.
     *
     * <p>A CNA extension. The clustered forward effect is a wrapper around a real shader effect,
     * and this is the way to reach that shader -- to set a parameter the wrapper has no property
     * for, or to hand it to a {@code SpriteBatch}. The effect belongs to this object; what comes
     * back is a <em>view</em>, and disposing the view gives the borrow back.
     *
     * <p><strong>A fresh view every call, and this object refuses to close while one is
     * outstanding.</strong> Disposing them is what makes closing possible, and the refusal is
     * recoverable rather than fatal.
     *
     * @param graphicsDevice the device the effect belongs to
     * @return the effect, which the caller disposes, or {@code null} where there is none
     */
    public Effect getEffect(GraphicsDevice graphicsDevice) {
        Objects.requireNonNull(graphicsDevice, "graphicsDevice");
        long[] effect = new long[1];
        GraphicsExtension.check("ClusteredForwardEffect.getEffect", NativeEngineLayerRoutes
                .clusteredForwardEffectGetEffect(open(), effect));
        return effect[0] == 0L ? null
                : FacadeFactory.createBorrowedEffect(graphicsDevice, effect[0]);
    }

    /**
     * Returns the material extensions this effect actually shades with, borrowed.
     *
     * <p>Not the same object as {@link #getMaterialExtensions()}, and the difference is the point.
     * {@link #setMaterialExtensions} gives CNA a <strong>copy</strong>, so what the effect shades
     * with is its own object with its own corrections applied -- the same relationship
     * {@link RenderPipelineSettingsExt#normalize()} exists for. This is that object.
     *
     * <p><strong>A retaining borrow.</strong> Unlike the shader effect above, this effect may be
     * closed while a view is outstanding: the view keeps what it names alive, and closing it
     * afterwards is fine.
     *
     * <p><strong>The view's texture slots are CNA's, not this binding's.</strong> A borrowed view
     * was never given a texture through Java, so {@link PbrMaterialExtensions#getClearcoatTexture}
     * and its eight siblings answer {@code null} on one. What is bound is still askable:
     * {@link PbrMaterialExtensions#hasClearcoatTexture()} and its siblings ask CNA and give the
     * name straight back.
     *
     * @return the extensions, which the caller closes
     */
    public PbrMaterialExtensions getEffectMaterialExtensions() {
        long[] extensions = new long[1];
        GraphicsExtension.check("ClusteredForwardEffect.getEffectMaterialExtensions",
                NativeEngineLayerRoutes.clusteredForwardEffectGetMaterialExtensions(open(),
                        extensions));
        return PbrMaterialExtensions.adoptBorrowed(extensions[0]);
    }

    /** Releases the effect. What it named is untouched. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
        }
        // Marked closed only after CNA agrees, and the retained references dropped only then.
        // Since getEffect() lends this effect's shader as a counted borrow, the release has a
        // reachable refusal -- and an object that recorded itself closed on the way to one would
        // be unusable and undestroyable at once, with the native effect leaked. A refused close
        // leaves it exactly as it was, so the caller can dispose the view and close again.
        GraphicsExtension.check("ClusteredForwardEffect.close",
                NativeEngineLayerRoutes.clusteredForwardEffectDestroy(handle));
        synchronized (this) {
            closed = true;
            probe = null;
            volume = null;
            areaLight = null;
            brdfTable = null;
            materialExtensions = null;
            opaqueFrame = null;
        }
    }

    /** A boolean CNA answers about one effect. */
    @FunctionalInterface
    private interface FlagRoute {
        int call(long effect, boolean[] answer);
    }

    /** A number CNA answers about one effect. */
    @FunctionalInterface
    private interface FloatRoute {
        int call(long effect, float[] answer);
    }

    private boolean flag(String operation, FlagRoute route) {
        boolean[] answer = new boolean[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    private float number(String operation, FloatRoute route) {
        float[] answer = new float[1];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return answer[0];
    }

    private Vector3 vector(String operation, FloatRoute route) {
        float[] answer = new float[3];
        GraphicsExtension.check(operation, route.call(open(), answer));
        return new Vector3(answer[0], answer[1], answer[2]);
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This ClusteredForwardEffect is closed");
            }
        }
        return handle;
    }
}
