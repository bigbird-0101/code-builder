package io.github.bigbird0101.code.core.config;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-11-05 20:03:23
 */
public interface Resource extends InputStreamSource, Serializable {
    /**
     * 获取资源的url
     * @throws IOException IOException
     * @return 资源的url
     */
    URL getURL() throws IOException;

    /**
     * 获取资源的uri
     * @throws IOException IOException
     * @return 资源的uri
     */
    URI getURI() throws IOException;

    /**
     * 获取资源的文件
     * @throws IOException IOException
     * @return 资源的文件
     */
    File getFile() throws IOException;

    /**
     * 根据相对路径创建一个资源
     * @param relativePath 相对路径
     * @return 相对路径创建一个资源
     * @throws IOException if the relative resource cannot be determined
     */
    Resource createRelative(String relativePath) throws IOException;
}
