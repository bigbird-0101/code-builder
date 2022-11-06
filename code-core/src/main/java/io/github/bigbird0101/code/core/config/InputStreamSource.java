package io.github.bigbird0101.code.core.config;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-11-05 19:59:52
 */
public interface InputStreamSource {
    /**
     * 获取输入流资源
     * @return 输入流资源
     * @throws IOException IOException
     */
    InputStream getInputStream() throws IOException;
}
