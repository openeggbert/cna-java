package Microsoft.Xna.Framework.Net;

import org.openeggbert.cna.internal.NativeGamerServices;
import org.openeggbert.cna.internal.generated.NativeNetworkRoutes;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * The optional integers a title uses to describe or to search for a session.
 *
 * <p>XNA declares {@code IList<int?>}: an unset slot is null, and a search matches a session on
 * the slots the search actually set. Java's {@link Integer} carries the same distinction, so
 * nothing is adapted for it.
 *
 * <p>CLR's base is {@code Object} with {@code IList<T>} as an interface, so this implements
 * {@link List} rather than extending {@link AbstractList}. The derived list operations are
 * delegated to a private {@code AbstractList} view over the same native list, which is what
 * keeps them consistent with {@code get}, {@code set} and {@code size} without restating them.
 */
public class NetworkSessionProperties implements List<Integer> {

    private final long handle;
    private final List<Integer> view = new AbstractList<>() {

        @Override
        public Integer get(int index) {
            return NetworkSessionProperties.this.item(index);
        }

        @Override
        public Integer set(int index, Integer element) {
            return NetworkSessionProperties.this.item(index, element);
        }

        @Override
        public int size() {
            return NetworkSessionProperties.this.count();
        }
    };

    public NetworkSessionProperties() {
        long[] properties = new long[1];
        NativeGamerServices.check("NetworkSessionProperties",
                NativeNetworkRoutes.networkSessionPropertiesCreate(properties));
        handle = properties[0];
    }

    NetworkSessionProperties(long handle) {
        this.handle = handle;
    }

    long handle() {
        return handle;
    }

    /** Returns XNA's enumerator over the property slots. */
    public final Iterator<Integer> GetEnumerator() {
        return view.iterator();
    }

    public final int getCount() {
        return count();
    }

    @Override
    public final Integer get(int index) {
        return item(index);
    }

    @Override
    public final Integer set(int index, Integer item) {
        return item(index, item);
    }

    @Override
    public final int size() {
        return count();
    }

    @Override
    public final boolean isEmpty() {
        return view.isEmpty();
    }

    @Override
    public final boolean contains(Object item) {
        return view.contains(item);
    }

    @Override
    public final Iterator<Integer> iterator() {
        return view.iterator();
    }

    /**
     * Copies every slot out in one native call.
     *
     * <p>This is the Java spelling of XNA's {@code CopyTo}, so it takes CNA's own bulk copy
     * rather than reading the slots back one at a time.
     */
    @Override
    public final Object[] toArray() {
        return snapshot();
    }

    @Override
    public final <T> T[] toArray(T[] array) {
        return view.toArray(array);
    }

    @Override
    public final boolean add(Integer item) {
        return view.add(item);
    }

    @Override
    public final boolean remove(Object item) {
        return view.remove(item);
    }

    @Override
    public final boolean containsAll(Collection<?> collection) {
        return view.containsAll(collection);
    }

    @Override
    public final boolean addAll(Collection<? extends Integer> collection) {
        return view.addAll(collection);
    }

    @Override
    public final boolean addAll(int index, Collection<? extends Integer> collection) {
        return view.addAll(index, collection);
    }

    @Override
    public final boolean removeAll(Collection<?> collection) {
        return view.removeAll(collection);
    }

    @Override
    public final boolean retainAll(Collection<?> collection) {
        return view.retainAll(collection);
    }

    @Override
    public final void clear() {
        NativeGamerServices.check("NetworkSessionProperties.clear",
                NativeNetworkRoutes.networkSessionPropertiesClear(handle));
    }

    @Override
    public final void add(int index, Integer item) {
        NativeGamerServices.check("NetworkSessionProperties.add",
                NativeNetworkRoutes.networkSessionPropertiesInsert(
                        handle, index, new byte[3], optional(item)));
    }

    @Override
    public final Integer remove(int index) {
        Integer previous = item(index);
        NativeGamerServices.check("NetworkSessionProperties.remove",
                NativeNetworkRoutes.networkSessionPropertiesRemoveAt(handle, index));
        return previous;
    }

    @Override
    public final int indexOf(Object item) {
        if (item != null && !(item instanceof Integer)) {
            return -1;
        }
        int[] index = new int[1];
        NativeGamerServices.check("NetworkSessionProperties.indexOf",
                NativeNetworkRoutes.networkSessionPropertiesIndexOf(
                        handle, new byte[3], optional((Integer) item), index));
        return index[0];
    }

    @Override
    public final int lastIndexOf(Object item) {
        return view.lastIndexOf(item);
    }

    @Override
    public final ListIterator<Integer> listIterator() {
        return view.listIterator();
    }

    @Override
    public final ListIterator<Integer> listIterator(int index) {
        return view.listIterator(index);
    }

    @Override
    public final List<Integer> subList(int fromIndex, int toIndex) {
        return view.subList(fromIndex, toIndex);
    }

    private static long[] optional(Integer value) {
        return new long[] {value == null ? 0L : 1L, value == null ? 0L : value};
    }

    private Integer item(int index) {
        long[] values = new long[2];
        NativeGamerServices.check("NetworkSessionProperties.get",
                NativeNetworkRoutes.networkSessionPropertiesGetItem(
                        handle, index, new byte[3], values));
        return values[0] != 0L ? (int) values[1] : null;
    }

    private Integer item(int index, Integer value) {
        Integer previous = item(index);
        NativeGamerServices.check("NetworkSessionProperties.set",
                NativeNetworkRoutes.networkSessionPropertiesSetItem(
                        handle, index, new byte[3], optional(value)));
        return previous;
    }

    private int count() {
        int[] count = new int[1];
        NativeGamerServices.check("NetworkSessionProperties.size",
                NativeNetworkRoutes.networkSessionPropertiesGetCount(handle, count));
        return count[0];
    }

    /**
     * Reads every slot through CNA's bulk copy.
     *
     * <p>CNA refuses a destination that cannot hold the whole list and writes no partial copy,
     * so the buffer is sized from the current count first. An unset slot stays null, which is
     * the distinction {@code int?} carries and {@link Integer} keeps.
     */
    private Integer[] snapshot() {
        int size = count();
        long[] values = new long[size * 2];
        long[] copied = new long[1];
        NativeGamerServices.check("NetworkSessionProperties.toArray",
                NativeNetworkRoutes.networkSessionPropertiesCopyTo(
                        handle, new byte[size * 3], values, 0, copied));
        Integer[] result = new Integer[(int) copied[0]];
        for (int index = 0; index < result.length; index++) {
            result[index] = values[index * 2] != 0L ? (int) values[index * 2 + 1] : null;
        }
        return result;
    }
}
