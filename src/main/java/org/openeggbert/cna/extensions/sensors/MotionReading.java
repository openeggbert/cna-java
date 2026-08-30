package org.openeggbert.cna.extensions.sensors;

import Microsoft.Xna.Framework.Vector3;

import java.time.OffsetDateTime;

/**
 * One fused motion sample: orientation, motion and gravity separated from each other.
 *
 * @param Timestamp when the host took the reading, with the offset the host reported
 * @param Attitude the fused device orientation
 * @param DeviceAcceleration acceleration per axis in g, with gravity already removed
 * @param DeviceRotationRate angular velocity per axis in radians per second
 * @param Gravity the gravity vector per axis, in g
 */
public record MotionReading(
        OffsetDateTime Timestamp,
        AttitudeReading Attitude,
        Vector3 DeviceAcceleration,
        Vector3 DeviceRotationRate,
        Vector3 Gravity) {
}
