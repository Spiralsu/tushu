package com.booksharing.book.service;

import com.booksharing.book.model.BookType;

import java.util.List;
import java.util.Map;

/**
 * 图书类型 服务层 *
 *
 */
public interface BookTypeService {

    Integer getCount();

    Integer getSearchCount(Map<String, Object> params);

    List<BookType> searchBookTypesByPage(Map<String, Object> params);

    Integer addBookType(BookType bookType);

    Integer deleteBookType(BookType bookType);

    Integer deleteBookTypes(List<BookType> bookTypes);

    Integer updateBookType(BookType bookType);

    List<BookType> queryBookTypes();

}
