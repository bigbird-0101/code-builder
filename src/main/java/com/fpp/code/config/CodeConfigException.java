package com.fpp.code.config;
import com.fpp.code.common.CodeBuilderException;

import java.security.PrivilegedActionException;

/**
 * 配置文件异常
 * @author fpp
 * @version 1.0
 * @date 2020/7/10 11:27
 */
public class CodeConfigException extends CodeBuilderException {
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public CodeConfigException(String message) {
        super(message);
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
    public CodeConfigException(Throwable cause) {
        super(cause);
    }


    public CodeConfigException(String message,Throwable cause) {
        super(message,cause);
    }

}
