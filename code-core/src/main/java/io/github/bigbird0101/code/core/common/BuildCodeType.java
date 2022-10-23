package io.github.bigbird0101.code.core.common;

/**
 * @author fpp
 * @version 1.0
 * @since 2020/5/15 16:39
 */
public enum BuildCodeType {
    /**
     * 在文件的末尾处拼接代码
     */
    AFTER_FILE,
    /**
     * 在java类最开始拼接代码
     */
    BEFORE_FILE,
    /**
     * 创建新的文件，然后把代码放进去
     */
    BUILD_NEW_FILE;
}
