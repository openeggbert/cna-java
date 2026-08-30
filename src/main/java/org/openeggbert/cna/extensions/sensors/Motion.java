package org.openeggbert.cna.extensions.sensors;

import Microsoft.Xna.Framework.Matrix;
import Microsoft.Xna.Framework.Quaternion;
import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeSensorExtensionRoutes;

/**
 * The host's fused motion sensor: orientation, motion and gravity separated from one another.
 *
 * <p>A CNA extension. This is the sensor a game wants for tilt control, because the fusion has
 * already removed gravity from the acceleration; the raw {@link Accelerometer} has not.
 */
public final class Motion extends Sensor<MotionReading> {

    private static final Routes ROUTES = new Routes() {

        @Override
        public int state(long sensor, int[] outState) {
            return NativeSensorExtensionRoutes.motionGetState(sensor, outState);
        }

        @Override
        public int start(long sensor) {
            return NativeSensorExtensionRoutes.motionStart(sensor);
        }

        @Override
        public int stop(long sensor) {
            return NativeSensorExtensionRoutes.motionStop(sensor);
        }

        @Override
        public int dataValid(long sensor, boolean[] outValid) {
            return NativeSensorExtensionRoutes.motionGetIsDataValid(sensor, outValid);
        }

        @Override
        public int updateTicks(long sensor, long[] outTicks) {
            return NativeSensorExtensionRoutes.motionGetTimeBetweenUpdatesTicks(sensor, outTicks);
        }

        @Override
        public int setUpdateTicks(long sensor, long ticks) {
            return NativeSensorExtensionRoutes.motionSetTimeBetweenUpdatesTicks(sensor, ticks);
        }

        @Override
        public int destroy(long sensor) {
            return NativeSensorExtensionRoutes.motionDestroy(sensor);
        }
    };

    private Motion(long handle) {
        super("Motion", ROUTES, handle);
    }

    /** Reports whether this host fuses motion. */
    public static boolean getIsSupported() {
        boolean[] supported = new boolean[1];
        SensorExtension.check("Motion.getIsSupported",
                NativeSensorExtensionRoutes.motionGetIsSupported(
                        SensorExtension.game("Motion"), supported));
        return supported[0];
    }

    /** Creates a motion sensor. Creation succeeds even where the host does not fuse motion. */
    public static Motion Create() {
        long[] sensor = new long[1];
        SensorExtension.check("Motion.Create",
                NativeSensorExtensionRoutes.motionCreate(
                        SensorExtension.game("Motion"), sensor));
        return new Motion(sensor[0]);
    }

    /**
     * Reports whether the attitude the sensor produces has an absolute yaw reference.
     *
     * <p>A CNA extension to the sensor itself, and one whose default is deliberately vacuous:
     * before a backend starts, the answer is {@code true} meaning "nothing is drifting yet", not
     * "north is known". It is informative only once a started backend answers for itself, which
     * {@link #getState()} reports.
     */
    public boolean getIsAttitudeNorthReferenced() {
        boolean[] referenced = new boolean[1];
        check("getIsAttitudeNorthReferenced", NativeSensorExtensionRoutes
                .motionGetIsAttitudeNorthReferencedExt(open(), referenced));
        return referenced[0];
    }

    /**
     * Returns the most recent sample.
     *
     * @throws IllegalStateException when the host does not fuse motion, which CNA refuses rather
     *     than answering with a default, or when this sensor is closed
     */
    @Override
    public MotionReading getCurrentValue() {
        long[] integral = new long[4];
        float[] floating = new float[32];
        check("getCurrentValue", NativeSensorExtensionRoutes
                .motionGetCurrentValue(open(), integral, floating));
        AttitudeReading attitude = new AttitudeReading(
                SensorExtension.timestamp(integral[2], integral[3]),
                floating[0], floating[1], floating[2],
                new Quaternion(floating[3], floating[4], floating[5], floating[6]),
                new Matrix(
                        floating[7], floating[8], floating[9], floating[10],
                        floating[11], floating[12], floating[13], floating[14],
                        floating[15], floating[16], floating[17], floating[18],
                        floating[19], floating[20], floating[21], floating[22]));
        return new MotionReading(
                SensorExtension.timestamp(integral[0], integral[1]),
                attitude,
                new Vector3(floating[23], floating[24], floating[25]),
                new Vector3(floating[26], floating[27], floating[28]),
                new Vector3(floating[29], floating[30], floating[31]));
    }
}
