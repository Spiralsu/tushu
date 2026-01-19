package com.shanzhu.book.service;

import com.shanzhu.book.model.Borrow;
import com.shanzhu.book.utils.R;

import java.util.List;
import java.util.Map;

public interface BorrowService {
    // 新增业务方法
    R addBorrow(Borrow borrow);
    R queryBorrowsByPage(Integer page, Integer size, Integer userId);
    R auditBorrow(Integer borrowId, Integer state, String feedback);

    // 基础/旧业务兼容方法
    Integer getCount();
    Integer getSearchCount(Map<String, Object> params);
    List<Borrow> searchBorrowsByPage(Map<String, Object> params);

    Integer deleteBorrow(Borrow borrow);
    Integer deleteBorrows(List<Borrow> borrows);
    Integer updateBorrow(Borrow borrow);
    Borrow queryBorrowsById(Integer borrowid);

    // 解决 Controller 和 Impl 报错的方法
    Integer addBorrow2(Borrow borrow);
    Integer updateBorrow2(Borrow borrow);
}