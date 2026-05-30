package com.booksharing.book.model;

import lombok.Data;

/**
 * 图书类型
 */
@Data
public class BookType {

    /**
     * 图书类型id
     */
    private Integer booktypeid;

    /**
     * 图书类型名
     */
    private String booktypename;

    /**
     * 图书类型描述
     */
    private String booktypedesc;

}
