package com.shanzhu.book.service;

import com.shanzhu.book.model.Borrow;
import com.shanzhu.book.utils.R;
import java.util.List;
import java.util.Map;

public interface BorrowService {

    R addBorrow(Borrow borrow);

    R auditBorrow(Integer borrowId, Integer state, String feedback);

    R queryBorrowsByPage(Integer page, Integer size, Integer userId);

    Integer getCount();

    // 搜索相关
    Integer getSearchCount(Map<String, Object> params);
    List<Borrow> searchBorrowsByPage(Map<String, Object> params);

    // 旧接口兼容
    Integer deleteBorrow(Borrow borrow);
    Integer deleteBorrows(List<Borrow> borrows);
    Integer updateBorrow(Borrow borrow);
    Borrow queryBorrowsById(Integer borrowid);

    Integer updateBorrow2(Borrow borrow);
    Integer addBorrow2(Borrow borrow);

    // 【新增】归还图书接口
    Integer returnBook(Integer borrowId, Integer bookId);
}