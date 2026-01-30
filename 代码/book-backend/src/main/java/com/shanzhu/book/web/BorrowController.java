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

    // 管理员审核与流程管理
    @PostMapping("/audit")
    public R auditBorrow(@RequestParam Integer borrowId,
                         @RequestParam Integer state,
                         @RequestParam(required = false) String feedback) {
        return borrowService.auditBorrow(borrowId, state, feedback);
    }

    // 【核心修复】首页获取借阅总数，解决 404 错误
    @GetMapping(value = "/getCount")
    public Integer getCount() {
        return borrowService.getCount();
    }

    // 【优化】分页查询
    @RequestMapping(value = "/queryBorrowsByPage")
    public Map<String, Object> queryBorrowsByPage(@RequestParam Map<String, Object> params) {
        List<Borrow> list = borrowService.searchBorrowsByPage(params);
        Integer count = borrowService.getSearchCount(params);
        return R.getListResultMap(0, "success", count, list);
    }

    // 兼容旧接口
    @RequestMapping(value = "/addBorrow")
    public R addBorrowOld(@RequestBody Borrow borrow) {
        return borrowService.addBorrow(borrow);
    }

    // 【核心修复】借书/申请接口 (解决申请报404)
    @RequestMapping("/borrowBook")
    public Integer borrowBook(@RequestParam(required = false) Integer userid,
                              @RequestParam(required = false) Integer bookid,
                              @RequestBody(required = false) Map<String, Object> body) {
        if (userid == null && body != null) {
            if (body.get("userid") != null) userid = Integer.parseInt(body.get("userid").toString());
            if (body.get("bookid") != null) bookid = Integer.parseInt(body.get("bookid").toString());
        }
        if (userid == null || bookid == null) return 0;

        Borrow borrow = new Borrow();
        borrow.setUserid(userid);
        borrow.setBookid(bookid);

        R result = borrowService.addBorrow(borrow);
        return result.getCode() == 0 ? 1 : 0;
    }

    // 【核心修复】归还接口 (解决归还报404或无反应)
    @RequestMapping("/returnBook")
    public Integer returnBook(@RequestParam Integer borrowid, @RequestParam Integer bookid) {
        return borrowService.returnBook(borrowid, bookid);
    }

    // 【核心修复】删除接口 (解决删除报404)
    @RequestMapping(value = "/deleteBorrow")
    public Integer deleteBorrow(@RequestBody Borrow borrow) {
        return borrowService.deleteBorrow(borrow);
    }

    // 【核心修复】批量删除接口
    @RequestMapping(value = "/deleteBorrows")
    public Integer deleteBorrows(@RequestBody List<Borrow> borrows) {
        return borrowService.deleteBorrows(borrows);
    }
}