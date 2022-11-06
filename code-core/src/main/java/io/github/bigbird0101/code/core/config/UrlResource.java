package io.github.bigbird0101.code.core.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-11-05 20:27:47
 */
public class UrlResource extends AbstractResource{
    private URL url;

    private URI uri;
    public UrlResource(URL url) {
         this.url=url;
         this.uri=null;
    }

    public UrlResource(String protocol, String location) throws MalformedURLException  {
        this(protocol, location, null);
    }

    public UrlResource(String protocol, String location,String fragment) throws MalformedURLException  {
        try {
            this.uri = new URI(protocol, location, fragment);
            this.url = this.uri.toURL();
        }
        catch (URISyntaxException ex) {
            MalformedURLException exToThrow = new MalformedURLException(ex.getMessage());
            exToThrow.initCause(ex);
            throw exToThrow;
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection con = this.url.openConnection();
        try {
            return con.getInputStream();
        }
        catch (IOException ex) {
            if (con instanceof HttpURLConnection) {
                ((HttpURLConnection) con).disconnect();
            }
            throw ex;
        }
    }

    @Override
    public URL getURL() throws IOException {
        return url;
    }

    @Override
    public URI getURI() throws IOException {
        return uri;
    }

    @Override
    public File getFile() throws IOException {
        return new File(URLUtil.decode(getURL().getFile()));
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return new UrlResource(createRelativeURL(relativePath));
    }

    /**
     * This delegate creates a {@code java.net.URL}, applying the given path
     * relative to the path of the underlying URL of this resource descriptor.
     * A leading slash will get dropped; a "#" symbol will get encoded.
     * @since 5.2
     * @see #createRelative(String)
     * @see java.net.URL#URL(java.net.URL, String)
     */
    protected URL createRelativeURL(String relativePath) throws MalformedURLException {
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        // # can appear in filenames, java.net.URL should not treat it as a fragment
        relativePath = StrUtil.replace(relativePath, "#", "%23");
        // Use the URL constructor for applying the relative path as a URL spec
        return new URL(this.url, relativePath);
    }
}
