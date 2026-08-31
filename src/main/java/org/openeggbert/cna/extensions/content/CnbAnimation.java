package org.openeggbert.cna.extensions.content;

/**
 * One animation clip a compiled model carries.
 *
 * <p>Read-only from Java, and that is a real boundary rather than an oversight: CNA's route for
 * adding one takes a descriptor whose fields are pointers to arrays of descriptors -- clip to
 * tracks to keyframes -- and the JNI generator refuses a shape whose lifetimes it cannot prove
 * rather than guessing at them. A clip therefore comes out of a {@code .cnj} compile or a
 * {@code .cnb} file, and Java reads it.
 *
 * @param Name the clip's name
 * @param DurationSeconds how long the clip runs
 * @param TrackCount how many bones the clip animates
 * @param TargetSpace which index space the tracks' bone indices live in
 */
public record CnbAnimation(
        String Name, double DurationSeconds, int TrackCount, CnbClipTargetSpace TargetSpace) {
}
