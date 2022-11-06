package io.github.bigbird0101.code.core.config;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-11-05 21:06:16
 */
public class FileUrlResource extends UrlResource implements WritableResource{
    public FileUrlResource(URL url) {
        super(url);
    }
    public FileUrlResource(String location) throws MalformedURLException {
        super("file", location);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return Files.newOutputStream(getFile().toPath());
    }
}
