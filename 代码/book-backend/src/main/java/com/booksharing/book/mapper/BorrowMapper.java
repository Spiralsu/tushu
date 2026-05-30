package com.booksharing.book.mapper;

import com.booksharing.book.model.Borrow;
import java.util.List;
import java.util.Map;

public interface BorrowMapper {
    int deleteByPrimaryKey(Integer borrowid);
    int insert(Borrow record);
    int updateByPrimaryKeySelective(Borrow record);
    Borrow selectByPrimaryKey(Integer borrowid);

    List<Borrow> selectAllByLimit(Map<String, Object> map);

    Integer selectCount(Map<String, Object> map);
}
