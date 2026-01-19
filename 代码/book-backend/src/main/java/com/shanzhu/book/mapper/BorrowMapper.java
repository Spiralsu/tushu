package com.shanzhu.book.mapper;

import com.shanzhu.book.model.Borrow;
import java.util.List;
import java.util.Map;

public interface BorrowMapper {
    int deleteByPrimaryKey(Integer borrowid);
    int insert(Borrow record);
    int updateByPrimaryKeySelective(Borrow record);
    Borrow selectByPrimaryKey(Integer borrowid);

    // 统一查询接口
    List<Borrow> selectAllByLimit(Map<String, Object> map);

    // 统一统计接口（替代 selectCountBySearch）
    Integer selectCount(Map<String, Object> map);
}