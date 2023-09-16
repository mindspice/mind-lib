package io.mindspice.mindlib.http;

import java.io.IOException;
import java.io.InputStream;


public class LimitedInputStream extends InputStream {
    private final InputStream original;
    private final long maxSize;
    private long bytesRead;

    public LimitedInputStream(InputStream original, long maxSize) {
        this.original = original;
        this.maxSize = maxSize;
        this.bytesRead = 0;
    }

    @Override
    public int read() throws IOException {
        if (bytesRead >= maxSize) {
            throw new IOException("Maximum response size exceeded; Limit: " + maxSize + " bytes");
        }
        int value = original.read();
        if (value != -1) {
            bytesRead++;
        }
        return value;
    }
}
