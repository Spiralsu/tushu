package com.booksharing.book.exception;

/**
 * 图书库存不足异常
 */
public class BookNotEnoughException extends RuntimeException {
    public BookNotEnoughException() {
        super();
    }

    public BookNotEnoughException(String message) {
        super(message);
    }
}
