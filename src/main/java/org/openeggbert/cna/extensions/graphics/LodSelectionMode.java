package org.openeggbert.cna.extensions.graphics;

/**
 * How a {@link LodGroup} reads its levels' thresholds.
 *
 * <p>The two answer different questions. Distance is what a level is a certain number of world
 * units away; screen-space error is how large it still looks, which is what actually decides
 * whether a simpler mesh is noticeable. A tall viewport or a narrow field of view makes the same
 * object cover more pixels at the same distance, and the second mode accounts for that where the
 * first cannot.
 */
public enum LodSelectionMode {

    /** Thresholds are distances from the camera, in world units. */
    Distance,

    /** Thresholds are projected radii in pixels; see
     * {@link LodGroup#setScreenSpaceParameters}. */
    ScreenSpaceError
}
