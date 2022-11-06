package io.github.bigbird0101.code.core.config;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-11-05 20:36:43
 */
public class PathResource extends AbstractResource implements WritableResource{
    private Path path;

    public PathResource(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!Files.exists(this.path)) {
            throw new FileNotFoundException(getPath() + " (no such file or directory)");
        }
        if (Files.isDirectory(this.path)) {
            throw new FileNotFoundException(getPath() + " (is a directory)");
        }
        return Files.newInputStream(this.path);
    }

    @Override
    public URL getURL() throws IOException {
        return this.path.toUri().toURL();
    }

    @Override
    public URI getURI() throws IOException {
        return this.path.toUri();
    }

    @Override
    public File getFile() throws IOException {
        try {
            return this.path.toFile();
        }
        catch (UnsupportedOperationException ex) {
            // Only paths on the default file system can be converted to a File:
            // Do exception translation for cases where conversion is not possible.
            throw new FileNotFoundException(this.path + " cannot be resolved to absolute file path");
        }
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return new PathResource(this.path.resolve(relativePath));
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return Files.newOutputStream(this.path);
    }
}
