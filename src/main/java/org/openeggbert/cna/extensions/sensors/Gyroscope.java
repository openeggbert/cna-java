package org.openeggbert.cna.extensions.sensors;

import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeSensorExtensionRoutes;

/**
 * The host's gyroscope, reporting angular velocity per axis in radians per second.
 *
 * <p>A CNA extension. A still device reads about zero on every axis, so {@link #getIsDataValid()}
 * is what separates that from a sensor that has produced nothing.
 */
public final class Gyroscope extends Sensor<GyroscopeReading> {

    private static final Routes ROUTES = new Routes() {

        @Override
        public int state(long sensor, int[] outState) {
            return NativeSensorExtensionRoutes.gyroscopeGetState(sensor, outState);
        }

        @Override
        public int start(long sensor) {
            return NativeSensorExtensionRoutes.gyroscopeStart(sensor);
        }

        @Override
        public int stop(long sensor) {
            return NativeSensorExtensionRoutes.gyroscopeStop(sensor);
        }

        @Override
        public int dataValid(long sensor, boolean[] outValid) {
            return NativeSensorExtensionRoutes.gyroscopeGetIsDataValid(sensor, outValid);
        }

        @Override
        public int updateTicks(long sensor, long[] outTicks) {
            return NativeSensorExtensionRoutes
                    .gyroscopeGetTimeBetweenUpdatesTicks(sensor, outTicks);
        }

        @Override
        public int setUpdateTicks(long sensor, long ticks) {
            return NativeSensorExtensionRoutes
                    .gyroscopeSetTimeBetweenUpdatesTicks(sensor, ticks);
        }

        @Override
        public int destroy(long sensor) {
            return NativeSensorExtensionRoutes.gyroscopeDestroy(sensor);
        }
    };

    private Gyroscope(long handle) {
        super("Gyroscope", ROUTES, handle);
    }

    /** Reports whether this host has a gyroscope. */
    public static boolean getIsSupported() {
        boolean[] supported = new boolean[1];
        SensorExtension.check("Gyroscope.getIsSupported",
                NativeSensorExtensionRoutes.gyroscopeGetIsSupported(
                        SensorExtension.game("Gyroscope"), supported));
        return supported[0];
    }

    /** Creates a gyroscope. Creation succeeds even where the host has no such sensor. */
    public static Gyroscope Create() {
        long[] sensor = new long[1];
        SensorExtension.check("Gyroscope.Create",
                NativeSensorExtensionRoutes.gyroscopeCreate(
                        SensorExtension.game("Gyroscope"), sensor));
        return new Gyroscope(sensor[0]);
    }

    /**
     * Returns the most recent sample.
     *
     * @throws IllegalStateException when the host has no gyroscope, which CNA refuses rather than
     *     answering with a default, or when this sensor is closed
     */
    @Override
    public GyroscopeReading getCurrentValue() {
        long[] integral = new long[2];
        float[] floating = new float[3];
        check("getCurrentValue", NativeSensorExtensionRoutes
                .gyroscopeGetCurrentValue(open(), integral, floating));
        return new GyroscopeReading(
                SensorExtension.timestamp(integral[0], integral[1]),
                new Vector3(floating[0], floating[1], floating[2]));
    }
}
