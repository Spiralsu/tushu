package com.shanzhu.book.service;

import com.shanzhu.book.model.Borrow;
import com.shanzhu.book.utils.R;
import java.util.List;
import java.util.Map;

public interface BorrowService {

    R addBorrow(Borrow borrow);

    // 审核并自动下发暗号
    R auditBorrow(Integer borrowId, Integer state, String feedback);

    // 【新增】异常撤销交接
    R cancelBorrow(Integer borrowId, String reason);


    // 【新增】验证交接暗号接口
    R verifyCode(Integer borrowId, String secretCode);

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

    // 【修改】：加入 contactInfo 参数
    Integer returnBook(Integer borrowId, Integer bookId, String returnMsg, String contactInfo);

    // 【新增】发送跨区协商交接通知
    R nudgeUploader(Integer borrowId, String message);

    // 毒点4：报损登记
    R reportLoss(Integer borrowId, String reason);

    // 毒点2：一键催还
    R urgeReturn(Integer borrowId);

    // 毒点5：绑定微信OpenID
    R bindWx(Integer userId, String openId);

    // 解除微信绑定
    R unbindWx(Integer userId);

    R forcePenalize(Integer borrowId);
}