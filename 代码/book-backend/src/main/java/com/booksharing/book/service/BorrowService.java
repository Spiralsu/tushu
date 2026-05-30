package com.booksharing.book.service;

import com.booksharing.book.model.Borrow;
import com.booksharing.book.utils.R;
import java.util.List;
import java.util.Map;

public interface BorrowService {

    R addBorrow(Borrow borrow);

    R auditBorrow(Integer borrowId, Integer state, String feedback);

    R cancelBorrow(Integer borrowId, String reason);

    R verifyCode(Integer borrowId, String secretCode);

    R queryBorrowsByPage(Integer page, Integer size, Integer userId);

    Integer getCount();

    Integer getSearchCount(Map<String, Object> params);
    List<Borrow> searchBorrowsByPage(Map<String, Object> params);

    Integer deleteBorrow(Borrow borrow);
    Integer deleteBorrows(List<Borrow> borrows);
    Integer updateBorrow(Borrow borrow);
    Borrow queryBorrowsById(Integer borrowid);

    Integer updateBorrow2(Borrow borrow);
    Integer addBorrow2(Borrow borrow);

    Integer returnBook(Integer borrowId, Integer bookId, String returnMsg, String contactInfo);

    R nudgeUploader(Integer borrowId, String message);

    R reportLoss(Integer borrowId, String reason);

    R urgeReturn(Integer borrowId);

    R bindWx(Integer userId, String openId);

    R unbindWx(Integer userId);

    R forcePenalize(Integer borrowId);
}
