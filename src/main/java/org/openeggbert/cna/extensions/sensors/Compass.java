package org.openeggbert.cna.extensions.sensors;

import Microsoft.Xna.Framework.Vector3;
import org.openeggbert.cna.internal.generated.NativeSensorExtensionRoutes;

/**
 * The host's compass: magnetic and true heading in degrees, plus the raw magnetometer vector.
 *
 * <p>A CNA extension. The headings are doubles across the whole path, because that is what CNA
 * reports and narrowing them at the boundary would discard precision the host measured.
 */
public final class Compass extends Sensor<CompassReading> {

    private static final Routes ROUTES = new Routes() {

        @Override
        public int state(long sensor, int[] outState) {
            return NativeSensorExtensionRoutes.compassGetState(sensor, outState);
        }

        @Override
        public int start(long sensor) {
            return NativeSensorExtensionRoutes.compassStart(sensor);
        }

        @Override
        public int stop(long sensor) {
            return NativeSensorExtensionRoutes.compassStop(sensor);
        }

        @Override
        public int dataValid(long sensor, boolean[] outValid) {
            return NativeSensorExtensionRoutes.compassGetIsDataValid(sensor, outValid);
        }

        @Override
        public int updateTicks(long sensor, long[] outTicks) {
            return NativeSensorExtensionRoutes
                    .compassGetTimeBetweenUpdatesTicks(sensor, outTicks);
        }

        @Override
        public int setUpdateTicks(long sensor, long ticks) {
            return NativeSensorExtensionRoutes
                    .compassSetTimeBetweenUpdatesTicks(sensor, ticks);
        }

        @Override
        public int destroy(long sensor) {
            return NativeSensorExtensionRoutes.compassDestroy(sensor);
        }
    };

    private Compass(long handle) {
        super("Compass", ROUTES, handle);
    }

    /** Reports whether this host has a compass. */
    public static boolean getIsSupported() {
        boolean[] supported = new boolean[1];
        SensorExtension.check("Compass.getIsSupported",
                NativeSensorExtensionRoutes.compassGetIsSupported(
                        SensorExtension.game("Compass"), supported));
        return supported[0];
    }

    /** Creates a compass. Creation succeeds even where the host has no such sensor. */
    public static Compass Create() {
        long[] sensor = new long[1];
        SensorExtension.check("Compass.Create",
                NativeSensorExtensionRoutes.compassCreate(
                        SensorExtension.game("Compass"), sensor));
        return new Compass(sensor[0]);
    }

    /**
     * Returns the most recent sample.
     *
     * @throws IllegalStateException when the host has no compass, which CNA refuses rather than
     *     answering with a default, or when this sensor is closed
     */
    @Override
    public CompassReading getCurrentValue() {
        long[] integral = new long[2];
        float[] floating = new float[3];
        double[] doubles = new double[3];
        check("getCurrentValue", NativeSensorExtensionRoutes
                .compassGetCurrentValue(open(), integral, floating, doubles));
        return new CompassReading(
                SensorExtension.timestamp(integral[0], integral[1]),
                doubles[0], doubles[1], doubles[2],
                new Vector3(floating[0], floating[1], floating[2]));
    }
}
