package org.openeggbert.cna.extensions.sensors;

import java.time.Duration;
import java.util.Objects;

/**
 * A host motion sensor: what every sensor family in this package has in common.
 *
 * <p>A CNA extension. A sensor is created even on a host that has none, which is CNA's own
 * contract: creation succeeds and {@link #getState()} says whether the hardware is there. Asking
 * an unsupported sensor for a reading is refused rather than answered with zeros, so a caller
 * that wants a number without a state check should ask its {@code getIsSupported()} first.
 *
 * <p>The handle is <strong>owned</strong>. {@link #close()} releases it, and CNA disposes the
 * sensor as part of releasing it. Closing twice is a no-op here even though CNA refuses a second
 * disposal, because {@link AutoCloseable} requires it; the second call never reaches CNA.
 *
 * <p>A sensor belongs to the game that created it and is created, read and closed on that game's
 * thread, which is the thread ownership CNA's game handle already imposes.
 *
 * @param <TReading> the sample type this sensor produces
 */
public abstract class Sensor<TReading> implements AutoCloseable {

    /** The dispatch a concrete sensor supplies, so the shared members reach its own routes. */
    interface Routes {

        int state(long sensor, int[] outState);

        int start(long sensor);

        int stop(long sensor);

        int dataValid(long sensor, boolean[] outValid);

        int updateTicks(long sensor, long[] outTicks);

        int setUpdateTicks(long sensor, long ticks);

        int destroy(long sensor);
    }

    private final String owner;
    private final Routes routes;
    private final long handle;
    private boolean closed;

    Sensor(String owner, Routes routes, long handle) {
        this.owner = owner;
        this.routes = routes;
        this.handle = handle;
    }

    /** Returns the most recent sample. */
    public abstract TReading getCurrentValue();

    /** Returns what the sensor is currently doing, including whether the host has it at all. */
    public final SensorState getState() {
        int[] state = new int[1];
        check("getState", routes.state(open(), state));
        return SensorState.values()[state[0]];
    }

    /** Starts acquisition. Starting an already started sensor is refused, as CNA refuses it. */
    public final void Start() {
        check("Start", routes.start(open()));
    }

    /** Stops acquisition. */
    public final void Stop() {
        check("Stop", routes.stop(open()));
    }

    /**
     * Reports whether the last reading is a real measurement.
     *
     * <p>This is what tells a genuine zero from the zeroed default a supported sensor answers
     * before it has produced anything.
     */
    public final boolean getIsDataValid() {
        boolean[] valid = new boolean[1];
        check("getIsDataValid", routes.dataValid(open(), valid));
        return valid[0];
    }

    /** Returns the interval the host was asked to sample at. */
    public final Duration getTimeBetweenUpdates() {
        long[] ticks = new long[1];
        check("getTimeBetweenUpdates", routes.updateTicks(open(), ticks));
        return Duration.ofNanos(Math.multiplyExact(
                ticks[0], SensorExtension.NANOSECONDS_PER_TICK));
    }

    /**
     * Asks the host to sample at this interval.
     *
     * <p>The host is free to sample slower or faster; this is a request, and the interval read
     * back is what CNA recorded, not what the hardware achieved.
     *
     * @param interval the requested sampling interval, never negative
     */
    public final void setTimeBetweenUpdates(Duration interval) {
        Objects.requireNonNull(interval, "interval");
        if (interval.isNegative()) {
            throw new IllegalArgumentException("interval must not be negative: " + interval);
        }
        check("setTimeBetweenUpdates", routes.setUpdateTicks(open(),
                interval.toNanos() / SensorExtension.NANOSECONDS_PER_TICK));
    }

    /** Releases the sensor. CNA disposes it as part of releasing it; closing twice is a no-op. */
    @Override
    public final void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        check("close", routes.destroy(handle));
    }

    /** Returns the owned handle, refusing to use it after the sensor is closed. */
    final long open() {
        synchronized (this) {
            if (closed) {
                throw new IllegalStateException("This " + owner + " is closed");
            }
        }
        return handle;
    }

    final void check(String operation, int result) {
        SensorExtension.check(owner + "." + operation, result);
    }
}
