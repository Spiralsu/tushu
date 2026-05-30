package com.booksharing.book.exception;

/**
 * 系统操作失败异常
 */
public class OperationFailureException extends RuntimeException {
    public OperationFailureException() {
    }

    public OperationFailureException(String message) {
        super(message);
    }
}
