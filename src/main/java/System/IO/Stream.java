package System.IO;

import java.io.FilterInputStream;
import java.io.InputStream;

/** Readable stream carrier used where the strict XNA signature names System.IO.Stream. */
public class Stream extends FilterInputStream {
    public Stream(InputStream input) {
        super(input);
    }
}
