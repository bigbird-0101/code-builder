package io.github.bigbird0101.code.exception;

import cn.hutool.core.util.StrUtil;

import java.security.PrivilegedActionException;

/**
 * 模板解析异常
 * @author fpp
 * @version 1.0
 */
public class TemplateResolveException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public TemplateResolveException(String message) {
        super(message);
    }

    /**
     *
     * @param message 异常信息
     * @param param 具体的参数
     */
    public TemplateResolveException(CharSequence message, Object... param) {
        super(StrUtil.format(message, param));
    }
    /**
     * Constructs a new exception with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwables (for example, {@link
     * PrivilegedActionException}).
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A <tt>null</tt> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public TemplateResolveException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public TemplateResolveException() {
    }

}
