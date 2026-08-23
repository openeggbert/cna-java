package Microsoft.Xna.Framework.Media;

import org.openeggbert.cna.internal.NativeMedia;
import java.util.Iterator;

/** Ordered read-only genre view owned by a media library. */
public final class GenreCollection implements Iterable<Genre>, AutoCloseable {
    private final MediaCollectionCore<Genre> core;
    GenreCollection(long handle) { core = new MediaCollectionCore<>(handle, NativeMedia.GENRE,
            new MediaCollectionCore.ItemFactory<>() {
                public Genre create(long value) { return new Genre(value); }
                public void release(Object value) { ((Genre)value).releaseHandleOnly(); }
            }); }
    public int getCount() { return core.count(); }
    public Genre get(int index) { return core.get(index); }
    public boolean getIsDisposed() { return core.isDisposed(); }
    public Iterator<Genre> GetEnumerator() { return core.iterator(); }
    @Override public Iterator<Genre> iterator() { return GetEnumerator(); }
    @Override public void close() { core.close(); }
    void releaseHandleOnly() { core.releaseHandleOnly(); }
}
