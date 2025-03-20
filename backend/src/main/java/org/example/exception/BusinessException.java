package org.example.exception;

/**
 * service层的异常信息处理
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
