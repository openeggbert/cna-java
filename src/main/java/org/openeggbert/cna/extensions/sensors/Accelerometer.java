package org.openeggbert.cna.extensions.sensors;

import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeSensorExtensionRoutes;

/**
 * The host's accelerometer, reporting acceleration per axis in g.
 *
 * <p>A CNA extension. A device at rest reads about one g along whichever axis points down, so a
 * reading of exactly zero on every axis is almost always the default rather than a measurement;
 * {@link #getIsDataValid()} is what tells them apart.
 */
public final class Accelerometer extends Sensor<AccelerometerReading> {

    private static final Routes ROUTES = new Routes() {

        @Override
        public int state(long sensor, int[] outState) {
            return NativeSensorExtensionRoutes.accelerometerGetState(sensor, outState);
        }

        @Override
        public int start(long sensor) {
            return NativeSensorExtensionRoutes.accelerometerStart(sensor);
        }

        @Override
        public int stop(long sensor) {
            return NativeSensorExtensionRoutes.accelerometerStop(sensor);
        }

        @Override
        public int dataValid(long sensor, boolean[] outValid) {
            return NativeSensorExtensionRoutes.accelerometerGetIsDataValid(sensor, outValid);
        }

        @Override
        public int updateTicks(long sensor, long[] outTicks) {
            return NativeSensorExtensionRoutes
                    .accelerometerGetTimeBetweenUpdatesTicks(sensor, outTicks);
        }

        @Override
        public int setUpdateTicks(long sensor, long ticks) {
            return NativeSensorExtensionRoutes
                    .accelerometerSetTimeBetweenUpdatesTicks(sensor, ticks);
        }

        @Override
        public int destroy(long sensor) {
            return NativeSensorExtensionRoutes.accelerometerDestroy(sensor);
        }
    };

    private Accelerometer(long handle) {
        super("Accelerometer", ROUTES, handle);
    }

    /**
     * Reports whether this host has an accelerometer.
     *
     * <p>{@code false} on a machine without one is an ordinary answer, and the usual one on a
     * desktop.
     */
    public static boolean getIsSupported() {
        boolean[] supported = new boolean[1];
        SensorExtension.check("Accelerometer.getIsSupported",
                NativeSensorExtensionRoutes.accelerometerGetIsSupported(
                        SensorExtension.game("Accelerometer"), supported));
        return supported[0];
    }

    /** Creates an accelerometer. Creation succeeds even where the host has no such sensor. */
    public static Accelerometer Create() {
        long[] sensor = new long[1];
        SensorExtension.check("Accelerometer.Create",
                NativeSensorExtensionRoutes.accelerometerCreate(
                        SensorExtension.game("Accelerometer"), sensor));
        return new Accelerometer(sensor[0]);
    }

    /**
     * Returns the most recent sample.
     *
     * @throws IllegalStateException when the host has no accelerometer, which CNA refuses rather
     *     than answering with a default, or when this sensor is closed
     */
    @Override
    public AccelerometerReading getCurrentValue() {
        long[] integral = new long[2];
        float[] floating = new float[3];
        check("getCurrentValue", NativeSensorExtensionRoutes
                .accelerometerGetCurrentValue(open(), integral, floating));
        return new AccelerometerReading(
                SensorExtension.timestamp(integral[0], integral[1]),
                new Vector3(floating[0], floating[1], floating[2]));
    }
}
