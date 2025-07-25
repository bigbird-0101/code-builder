package io.github.bigbird0101.code.core.share;

/**
 * @author bigbird-0101
 * @date 2024-06-12 21:03
 */
public abstract class AbstractShareServerProvider {
    private static ShareServer shareServer;

    public static void setShareServer(ShareServer shareServer) {
        AbstractShareServerProvider.shareServer = shareServer;
    }

    public static ShareServer getShareServer() {
        if (null == shareServer) {
            throw new IllegalStateException("共享服务未初始化或者初始化异常");
        }
        return shareServer;
    }
}
