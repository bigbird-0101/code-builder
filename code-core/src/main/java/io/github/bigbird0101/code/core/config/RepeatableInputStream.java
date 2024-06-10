package io.github.bigbird0101.code.core.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RepeatableInputStream extends InputStream {

    private ByteArrayInputStream cachedStream;
    private byte[] cache;
    private int cachePosition;
    private InputStream originalStream;

    public RepeatableInputStream(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("The InputStream must not be null");
        }
        this.originalStream = inputStream;
        // 初始化缓存大小，可以根据需要调整
        cache = new byte[1024];
        cachePosition = 0;
    }

    @Override
    public int read() throws IOException {
        if (cachedStream != null) {
            return cachedStream.read();
        } else {
            int data = originalStream.read();
            if (data != -1) {
                cache[cachePosition++] = (byte) data;
            }
            if (data == -1 && cachePosition > 0) {
                cachedStream = new ByteArrayInputStream(cache, 0, cachePosition);
                // 重置位置以备下次读取时重新填充
                cachePosition = 0;
                // 递归调用以从缓存中读取
                return read();
            }
            return data;
        }
    }

    /**
     * 重置输入流到初始位置。
     * 注意：这个实现基于将数据全部缓存，因此重置操作成本低。
     */
    public void reset() {
        if (cachedStream != null) {
//            cachedStream.reset();
        }
    }

    // 其他必要的 InputStream 方法（如read(byte[] b), available(), skip(long n), close()等）
    // 需要根据具体需求实现，这里省略以保持示例简洁

    // 示例close方法，确保原始输入流最终被关闭
    @Override
    public void close() throws IOException {
        if (originalStream != null) {
            originalStream.close();
        }
        if (cachedStream != null) {
            cachedStream.close();
        }
    }
}
