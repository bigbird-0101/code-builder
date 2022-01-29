package com.fpp.code.core.exception;

/**
 * 创建模板时异常
 */
public class CreateTemplateException extends CodeBuilderException {
    public CreateTemplateException(String message) {
        super(message);
    }

    public CreateTemplateException(Throwable cause) {
        super(cause);
    }

    public CreateTemplateException(String message, Throwable cause) {
        super(message, cause);
    }
}
