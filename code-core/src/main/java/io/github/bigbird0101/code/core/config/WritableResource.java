package io.github.bigbird0101.code.core.config;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 可以写的资源
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-11-05 20:57:36
 */
public interface WritableResource extends Resource{
    /**
     * 获取一个输出流
     * @return 输出流
     * @throws IOException IOException
     * @see #getInputStream()
     */
    OutputStream getOutputStream() throws IOException;
}
