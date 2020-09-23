package dev.joeoc.quotifier;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamReading {
    public static byte[] readBytes(InputStream in) throws IOException {
        byte[] buffer = new byte[2048];

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int len;
            while ((len = in.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }

            return baos.toByteArray();
        }
    }
}
