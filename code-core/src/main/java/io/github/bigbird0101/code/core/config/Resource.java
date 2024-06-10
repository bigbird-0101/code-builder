package io.github.bigbird0101.code.core.config;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

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


    /**
     * 读取资源内容，读取完毕后会关闭流<br>
     * 关闭流并不影响下一次读取
     *
     * @param charset 编码
     * @return 读取资源内容
     * @throws IOException IOException
     */
    default String readStr(Charset charset) throws IOException {
        return IoUtil.read(getReader(charset));
    }

    /**
     * 读取资源内容，读取完毕后会关闭流<br>
     * 关闭流并不影响下一次读取
     *
     * @return 读取资源内容
     * @throws IOException IOException
     */
    default String readUtf8Str() throws IOException {
        return readStr(CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 读取资源内容，读取完毕后会关闭流<br>
     * 关闭流并不影响下一次读取
     *
     * @return 读取资源内容
     * @throws IOException 包装IOException
     */
    default byte[] readBytes() throws IOException {
        return IoUtil.readBytes(getInputStream());
    }

    /**
     * 获得Reader
     *
     * @param charset 编码
     * @return {@link BufferedReader}
     * @throws IOException 包装IOException
     */
    default BufferedReader getReader(Charset charset) throws IOException{
        return IoUtil.getReader(getInputStream(), charset);
    }
}
