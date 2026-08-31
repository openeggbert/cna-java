package org.openeggbert.cna.extensions.graphics;

import org.openeggbert.cna.extensions.content.CnaModelMeshPartHandle;
import org.openeggbert.cna.internal.ExtensionHandles;
import org.openeggbert.cna.internal.generated.NativeEngineLayerRoutes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Chooses which level of detail to draw at a distance.
 *
 * <p>A CNA extension, and one XNA has no counterpart for at all: XNA draws whatever mesh you hand
 * it, and every game that wanted level of detail wrote this itself. A group holds an ordered set
 * of thresholds and answers, for a distance, which level applies.
 *
 * <p>Two things make it worth using rather than writing again. It can select on
 * <strong>projected size</strong> rather than distance -- how many pixels the object's radius
 * actually covers, which is what decides whether a simpler mesh is noticeable, and which depends
 * on the field of view and the viewport height as well as the distance. And it applies
 * <strong>hysteresis</strong>: an object sitting exactly on a threshold would otherwise switch
 * level every frame as it jitters across it, and the margin is how far it has to move back before
 * the group changes its mind.
 *
 * <p><strong>A level may carry a mesh part, or only a threshold.</strong> The threshold-only form
 * came first and is still the simpler one: {@link #selectIndex} returns the level's index and the
 * game draws whatever it keeps at that index. It was for a while the only form, on the reasoning
 * that XNA's {@link Microsoft.Xna.Framework.Graphics.ModelMeshPart} is a managed object with no
 * native handle -- which stopped being true when {@link CnaModelMeshPartHandle} arrived, so
 * {@link #addLevel(float, CnaModelMeshPartHandle)} and {@link #select} exist too and the group can
 * hold the mapping itself.
 *
 * <p><strong>A part added here stays the caller's.</strong> CNA borrows it, and {@link #select}
 * hands back the very object that was added rather than a second view of it -- so there is one
 * owner, the one that created the part, and closing the group does not close a part.
 *
 * <p>The handle is owned; {@link #close()} releases it and closing twice is a no-op.
 */
public final class LodGroup implements AutoCloseable {

    /** CNA's own sentinel for "no mesh part at this level", which every level here uses. */
    private static final long NO_PART = 0L;

    /** CNA's own result for a buffer that could not hold the answer. */
    private static final int RESULT_BUFFER_TOO_SMALL = 14;

    private final long handle;

    /**
     * The parts added, by handle, so a selection hands back the caller's own object.
     *
     * <p>CNA lends a level's part back as a bare handle. Wrapping that in a second Java object
     * would give a part two Java identities and invite closing the wrong one; looking it up here
     * gives back the one the caller already has.
     */
    private final Map<Long, CnaModelMeshPartHandle> parts = new HashMap<>();
    private boolean closed;

    private LodGroup(long handle) {
        this.handle = handle;
    }

    /**
     * Creates an empty group.
     *
     * @return the group, which the caller closes
     * @throws ExtensionNotSupportedException when this build has no engine layer
     */
    public static LodGroup create() {
        GraphicsExtension.requireBackend();
        long[] group = new long[1];
        GraphicsExtension.check("LodGroup.create",
                NativeEngineLayerRoutes.lodGroupExtCreate(group));
        return new LodGroup(group[0]);
    }

    /**
     * Adds one level and re-sorts the group.
     *
     * @param threshold the level's threshold, read as a distance or a projected radius depending
     *        on the selection mode; must be positive
     */
    public void addLevel(float threshold) {
        addLevel(threshold, null);
    }

    /**
     * Adds one level that draws a mesh part, and re-sorts the group.
     *
     * <p>The part is borrowed for as long as the level exists: it stays the caller's to close,
     * and it must outlive this group or be removed from it with {@link #clear()} first.
     *
     * @param threshold the level's threshold, read as a distance or a projected radius depending
     *        on the selection mode; must be positive
     * @param part what to draw at this level, or null for a level that deliberately draws
     *        nothing -- which is how a group fades an object out entirely at distance
     */
    public void addLevel(float threshold, CnaModelMeshPartHandle part) {
        if (!(threshold > 0.0f) || !Float.isFinite(threshold)) {
            throw new IllegalArgumentException(
                    "a level's threshold must be positive and finite, not " + threshold);
        }
        long value = part == null ? NO_PART : ExtensionHandles.meshPart(part);
        GraphicsExtension.check("LodGroup.addLevel",
                NativeEngineLayerRoutes.lodGroupExtAddLevel(open(), threshold, value));
        if (part != null) {
            parts.put(value, part);
        }
    }

    /**
     * Picks the level that applies at a distance and returns what it draws.
     *
     * <p>Answers null both when the group is empty and when the chosen level deliberately draws
     * nothing; {@link #selectIndex} separates those two. Hysteresis applies here exactly as it
     * does to {@link #selectIndex}, and the two share one remembered selection.
     *
     * @param distance the distance from the camera
     * @return the part the group chose -- the same object that was added -- or null
     */
    public CnaModelMeshPartHandle select(float distance) {
        long[] part = new long[1];
        GraphicsExtension.check("LodGroup.select",
                NativeEngineLayerRoutes.lodGroupExtSelect(open(), distance, part));
        return part[0] == NO_PART ? null : parts.get(part[0]);
    }

    /** Removes every level and forgets the last selection. */
    public void clear() {
        GraphicsExtension.check("LodGroup.clear",
                NativeEngineLayerRoutes.lodGroupExtClear(open()));
        parts.clear();
    }

    /**
     * How many parts this group is holding a reference to.
     *
     * <p>Package-private, and here because forgetting to drop them in {@link #clear()} is
     * invisible from behaviour: CNA answers "no part" for a cleared group either way, so a group
     * that kept the caller's parts alive would pass every selection test. This is what makes that
     * a fact a test can state.
     *
     * @return the number of retained parts
     */
    int retainedPartCount() {
        return parts.size();
    }

    /**
     * Returns the levels' thresholds, in the group's own order.
     *
     * @return the thresholds, sorted as the group sorted them
     */
    public List<Float> getThresholds() {
        long group = open();
        long[] count = new long[1];
        // A zero-capacity probe reports the count and writes nothing, so BUFFER_TOO_SMALL is the
        // expected answer to the first call rather than a failure.
        int probe = NativeEngineLayerRoutes
                .lodGroupExtCopyLevels(group, new long[0], new float[0], count);
        if (probe != RESULT_BUFFER_TOO_SMALL) {
            GraphicsExtension.check("LodGroup.getThresholds", probe);
        }
        int levels = Math.toIntExact(count[0]);
        // Two integral leaves to a level -- the borrowed part handle and CNA's reserved word --
        // and one float. The adapter derives the capacity from the integral array's length, so
        // sizing it per level rather than per leaf would ask for half as many as were wanted.
        long[] parts = new long[Math.multiplyExact(levels, 2)];
        float[] distances = new float[levels];
        GraphicsExtension.check("LodGroup.getThresholds", NativeEngineLayerRoutes
                .lodGroupExtCopyLevels(group, parts, distances, count));
        List<Float> thresholds = new ArrayList<>(levels);
        for (int level = 0; level < levels; level++) {
            thresholds.add(distances[level]);
        }
        return List.copyOf(thresholds);
    }

    /**
     * Picks the level that applies at a distance.
     *
     * <p>Hysteresis makes this stateful on purpose: the answer depends on the level the group
     * last returned, which is what stops an object sitting on a threshold from switching every
     * frame. {@link #resetHysteresis()} forgets it.
     *
     * @param distance the distance from the camera
     * @return the level's index, or -1 when the group has no levels
     */
    public int selectIndex(float distance) {
        int[] index = new int[1];
        GraphicsExtension.check("LodGroup.selectIndex",
                NativeEngineLayerRoutes.lodGroupExtSelectIndex(open(), distance, index));
        return index[0];
    }

    /** Returns the margin that damps switching at a threshold. */
    public float getHysteresis() {
        float[] margin = new float[1];
        GraphicsExtension.check("LodGroup.getHysteresis",
                NativeEngineLayerRoutes.lodGroupExtGetHysteresis(open(), margin));
        return margin[0];
    }

    /**
     * Sets the margin that damps switching at a threshold.
     *
     * @param margin how far past a threshold the object must move before the level changes back
     */
    public void setHysteresis(float margin) {
        GraphicsExtension.check("LodGroup.setHysteresis",
                NativeEngineLayerRoutes.lodGroupExtSetHysteresis(open(), margin));
    }

    /** Forgets the last selection, so the next one is unaffected by hysteresis. */
    public void resetHysteresis() {
        GraphicsExtension.check("LodGroup.resetHysteresis",
                NativeEngineLayerRoutes.lodGroupExtResetHysteresis(open()));
    }

    /** Returns how the group reads its thresholds. */
    public LodSelectionMode getSelectionMode() {
        int[] mode = new int[1];
        GraphicsExtension.check("LodGroup.getSelectionMode",
                NativeEngineLayerRoutes.lodGroupExtGetSelectionMode(open(), mode));
        LodSelectionMode[] values = LodSelectionMode.values();
        if (mode[0] < 0 || mode[0] >= values.length) {
            throw new IllegalStateException(
                    "CNA reported LOD selection mode " + mode[0]
                    + ", which this build has no constant for");
        }
        return values[mode[0]];
    }

    /**
     * Changes how the group reads its thresholds.
     *
     * @param mode the mode to select with
     */
    public void setSelectionMode(LodSelectionMode mode) {
        Objects.requireNonNull(mode, "mode");
        GraphicsExtension.check("LodGroup.setSelectionMode",
                NativeEngineLayerRoutes.lodGroupExtSetSelectionMode(open(), mode.ordinal()));
    }

    /**
     * Sets the three numbers screen-space selection needs.
     *
     * @param radius the object's bounding radius; must be positive
     * @param verticalFieldOfView the camera's vertical field of view in radians, in (0, pi)
     * @param viewportHeight the viewport height in pixels; must be positive
     */
    public void setScreenSpaceParameters(
            float radius, float verticalFieldOfView, float viewportHeight) {
        GraphicsExtension.check("LodGroup.setScreenSpaceParameters", NativeEngineLayerRoutes
                .lodGroupExtSetScreenSpaceParameters(
                        open(), radius, verticalFieldOfView, viewportHeight));
    }

    /**
     * Returns how many pixels the object's radius projects to at a distance.
     *
     * <p>The number screen-space selection compares against, exposed so a game can show it or
     * tune thresholds against it rather than guessing what the group is doing.
     *
     * @param distance the distance from the camera
     * @return the projected radius in pixels
     */
    public float getProjectedRadiusPixels(float distance) {
        float[] pixels = new float[1];
        GraphicsExtension.check("LodGroup.getProjectedRadiusPixels", NativeEngineLayerRoutes
                .lodGroupExtProjectedRadiusPixels(open(), distance, pixels));
        return pixels[0];
    }

    /** Releases the group. Closing twice is a no-op. */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        parts.clear();
        GraphicsExtension.check("LodGroup.close",
                NativeEngineLayerRoutes.lodGroupExtDestroy(handle));
    }

    private long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This LodGroup is closed");
            }
        }
        return handle;
    }
}
