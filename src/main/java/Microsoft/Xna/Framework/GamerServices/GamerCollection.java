package Microsoft.Xna.Framework.GamerServices;

import org.openeggbert.cna.internal.NativeGamerServices;
import org.openeggbert.cna.internal.generated.NativeGamerServicesRoutes;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.LongFunction;

/**
 * A read-only collection of gamers.
 *
 * <p>XNA derives this from {@code ReadOnlyCollection<T>} and additionally declares
 * {@code IEnumerable<Gamer>} so a caller can walk any gamer collection as {@code Gamer}. Java
 * forbids one class from implementing two parameterizations of one interface, so the
 * projection bounds {@code T} by {@link Gamer} instead: a {@code GamerCollection<T>} is an
 * {@code Iterable<? extends Gamer>}, which is what those call sites wanted.
 *
 * @param <T> the kind of gamer this collection holds
 */
public class GamerCollection<T extends Gamer> extends AbstractList<T> {

    private final long handle;
    private final LongFunction<T> factory;
    private final Source source;

    GamerCollection(long handle, LongFunction<T> factory) {
        this(handle, factory, null);
    }

    GamerCollection(long handle, LongFunction<T> factory, Source source) {
        this.handle = handle;
        this.factory = factory;
        this.source = source;
    }

    long handle() {
        return handle;
    }

    /**
     * Where a collection's elements come from.
     *
     * <p>Most gamer collections are one native collection handle. The signed-in gamers are not:
     * they are the process-wide slot list, read afresh on every access. Keeping that behind a
     * source rather than behind an override is what lets {@code SignedInGamerCollection} add
     * only the member XNA declares on it.
     */
    interface Source {
        int count();

        long at(int index);
    }

    /** Returns XNA's enumerator over this collection. */
    public final GamerCollectionEnumerator<T> GetEnumerator() {
        long[] enumerator = new long[1];
        NativeGamerServices.check("GamerCollection.GetEnumerator",
                NativeGamerServicesRoutes.gamerCollectionCreateEnumerator(handle, enumerator));
        return new GamerCollectionEnumerator<>(enumerator[0], factory);
    }

    @Override
    public T get(int index) {
        if (source != null) {
            return factory.apply(source.at(index));
        }
        long[] gamer = new long[1];
        NativeGamerServices.check("GamerCollection.get",
                NativeGamerServicesRoutes.gamerCollectionGetAt(handle, index, gamer));
        return factory.apply(gamer[0]);
    }

    @Override
    public int size() {
        if (source != null) {
            return source.count();
        }
        int[] count = new int[1];
        NativeGamerServices.check("GamerCollection.size",
                NativeGamerServicesRoutes.gamerCollectionGetCount(handle, count));
        return count[0];
    }

    /**
     * XNA's enumerator over a gamer collection.
     *
     * <p>It is a struct in XNA, so it is a value carrier here, and it is also a Java
     * {@link Iterator} so the collection can be used in a for-each loop. {@code MoveNext} and
     * {@code getCurrent} are the XNA members; {@code hasNext} and {@code next} are the Java
     * bridge over the same cursor.
     *
     * @param <T> the kind of gamer this enumerator yields
     */
    public static final class GamerCollectionEnumerator<T extends Gamer> implements Iterator<T> {

        private final long handle;
        private final LongFunction<T> factory;
        private boolean hasCurrent;
        private boolean peeked;

        GamerCollectionEnumerator(long handle, LongFunction<T> factory) {
            this.handle = handle;
            this.factory = factory;
        }

        public GamerCollectionEnumerator() {
            this(0L, value -> null);
        }

        public GamerCollectionEnumerator(GamerCollectionEnumerator<T> value) {
            this(value == null ? 0L : value.handle, value == null ? item -> null : value.factory);
        }

        public void Dispose() {
            if (handle != 0L) {
                NativeGamerServices.check("GamerCollectionEnumerator.Dispose",
                        NativeGamerServicesRoutes.gamerEnumeratorDestroy(handle));
            }
        }

        public void close() {
            Dispose();
        }

        public boolean MoveNext() {
            if (handle == 0L) {
                return false;
            }
            boolean[] present = new boolean[1];
            NativeGamerServices.check("GamerCollectionEnumerator.MoveNext",
                    NativeGamerServicesRoutes.gamerEnumeratorMoveNext(handle, present));
            hasCurrent = present[0];
            peeked = false;
            return hasCurrent;
        }

        public T getCurrent() {
            if (!hasCurrent) {
                throw new IllegalStateException(
                        "Current is undefined before the first MoveNext and after the last");
            }
            long[] gamer = new long[1];
            NativeGamerServices.check("GamerCollectionEnumerator.Current",
                    NativeGamerServicesRoutes.gamerEnumeratorGetCurrent(handle, gamer));
            return factory.apply(gamer[0]);
        }

        @Override
        public boolean hasNext() {
            if (!peeked) {
                peeked = MoveNext();
                hasCurrent = peeked;
            }
            return hasCurrent;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            peeked = false;
            return getCurrent();
        }
    }
}
