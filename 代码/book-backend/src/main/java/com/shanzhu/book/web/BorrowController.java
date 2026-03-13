package com.shanzhu.book.web;

import com.shanzhu.book.model.Borrow;
import com.shanzhu.book.service.BorrowService;
import com.shanzhu.book.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/borrow")
public class BorrowController {

    @Resource
    private BorrowService borrowService;

    // 申请漂流
    @PostMapping("/add")
    public R addBorrow(@RequestBody Borrow borrow) {
        if(borrow.getBorrowreason() == null || borrow.getBorrowreason().isEmpty()){
            return R.error("请填写申请理由");
        }
        return borrowService.addBorrow(borrow);
    }

    // 查询我的漂流记录
    @GetMapping("/myList")
    public R queryMyBorrows(Integer page, Integer size, Integer userId) {
        return borrowService.queryBorrowsByPage(page, size, userId);
    }

    // 管理员审核与流程管理 (包含下发暗号)
    @PostMapping("/audit")
    public R auditBorrow(@RequestParam Integer borrowId,
                         @RequestParam Integer state,
                         @RequestParam(required = false) String feedback) {
        return borrowService.auditBorrow(borrowId, state, feedback);
    }

    // 【新增】真正的验证暗号接口
    @PostMapping("/verifyCode")
    public R verifyCode(@RequestParam Integer borrowId, @RequestParam String secretCode) {
        return borrowService.verifyCode(borrowId, secretCode);
    }

    @GetMapping(value = "/getCount")
    public Integer getCount() {
        return borrowService.getCount();
    }

    @RequestMapping(value = "/queryBorrowsByPage")
    public Map<String, Object> queryBorrowsByPage(@RequestParam Map<String, Object> params) {
        List<Borrow> list = borrowService.searchBorrowsByPage(params);
        Integer count = borrowService.getSearchCount(params);
        return R.getListResultMap(0, "success", count, list);
    }

    @RequestMapping(value = "/addBorrow")
    public R addBorrowOld(@RequestBody Borrow borrow) {
        return borrowService.addBorrow(borrow);
    }

    @RequestMapping("/borrowBook")
    public R borrowBook(@RequestParam(required = false) Integer userid,
                        @RequestParam(required = false) Integer bookid,
                        @RequestBody(required = false) Map<String, Object> body) {
        if (userid == null && body != null) {
            if (body.get("userid") != null) userid = Integer.parseInt(body.get("userid").toString());
            if (body.get("bookid") != null) bookid = Integer.parseInt(body.get("bookid").toString());
        }
        if (userid == null || bookid == null) return R.error("缺少用户或书籍ID");

        Borrow borrow = new Borrow();
        borrow.setUserid(userid);
        borrow.setBookid(bookid);

        if (body != null) {
            // 准确接收用户的申请理由
            if (body.get("borrowreason") != null) {
                borrow.setBorrowreason(body.get("borrowreason").toString());
            }
            // 【核心修复】：准确接收前端传来的借阅天数！
            if (body.get("borrowDays") != null) {
                borrow.setBorrowDays(Integer.parseInt(body.get("borrowDays").toString()));
            }
        }

        // 【核心修复】：直接返回 R 对象。这样前端才能拿到具体的报错文本（如：信用分不足等提示）
        return borrowService.addBorrow(borrow);
    }

    @PostMapping("/returnBook")
    public R returnBook(@RequestParam Integer borrowId, @RequestParam Integer bookId, @RequestParam String returnMsg, @RequestParam(defaultValue = "") String contactInfo) {
        int result = borrowService.returnBook(borrowId, bookId, returnMsg, contactInfo);
        if (result > 0) {
            return R.ok();
        }
        return R.error("操作失败");
    }

    @RequestMapping(value = "/deleteBorrow")
    public Integer deleteBorrow(@RequestBody Borrow borrow) {
        return borrowService.deleteBorrow(borrow);
    }

    @RequestMapping(value = "/deleteBorrows")
    public Integer deleteBorrows(@RequestBody List<Borrow> borrows) {
        return borrowService.deleteBorrows(borrows);
    }


    // 【新增】撤销交接接口
    @PostMapping("/cancel")
    public R cancelBorrow(@RequestParam Integer borrowId, @RequestParam String reason) {
        return borrowService.cancelBorrow(borrowId, reason);
    }


    // 【新增】跨区协商交接接口
    @PostMapping("/nudge")
    public R nudgeUploader(@RequestParam Integer borrowId, @RequestParam String message) {
        return borrowService.nudgeUploader(borrowId, message);
    }


    // 毒点4：报损登记接口
    @PostMapping("/reportLoss")
    public R reportLoss(@RequestParam Integer borrowId, @RequestParam String reason) {
        return borrowService.reportLoss(borrowId, reason);
    }

    // 毒点2：一键催还接口
    @PostMapping("/urge")
    public R urgeReturn(@RequestParam Integer borrowId) {
        return borrowService.urgeReturn(borrowId);
    }

    // 毒点5：绑定微信测试号 OpenID 接口
    @PostMapping("/bindWx")
    public R bindWx(@RequestParam Integer userId, @RequestParam String openId) {
        return borrowService.bindWx(userId, openId);
    }


    // 毒点：解除微信绑定
    @PostMapping("/unbindWx")
    public R unbindWx(@RequestParam Integer userId) {
        return borrowService.unbindWx(userId);
    }

    @PostMapping("/forcePenalize")
    public R forcePenalize(@RequestParam Integer borrowId) {
        return borrowService.forcePenalize(borrowId);
    }


}