package com.shanzhu.book.web;

import com.shanzhu.book.model.Borrow;
import com.shanzhu.book.service.BorrowService;
import com.shanzhu.book.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping(value = "/borrow")
public class BorrowController {

    @Resource
    private BorrowService borrowService;

    // 新增：申请漂流
    @PostMapping("/add")
    public R addBorrow(@RequestBody Borrow borrow) {
        if(borrow.getBorrowreason() == null || borrow.getBorrowreason().isEmpty()){
            return R.error("请填写申请理由");
        }
        return borrowService.addBorrow(borrow);
    }

    // 新增：查询我的漂流记录
    @GetMapping("/myList")
    public R queryMyBorrows(Integer page, Integer size, Integer userId) {
        return borrowService.queryBorrowsByPage(page, size, userId);
    }

    // 新增：管理员审核与流程管理
    @PostMapping("/audit")
    public R auditBorrow(@RequestParam Integer borrowId,
                         @RequestParam Integer state,
                         @RequestParam(required = false) String feedback) {
        return borrowService.auditBorrow(borrowId, state, feedback);
    }

    // --- 保留原有接口映射，但内部尽量转调新逻辑 ---

    @RequestMapping(value = "/queryBorrowsByPage")
    public Map<String, Object> queryBorrowsByPage(@RequestParam Map<String, Object> params) {
        // 简单转调，实际建议前端改用 /myList
        return borrowService.searchBorrowsByPage(params) != null ? R.ok() : R.error();
    }

    @RequestMapping(value = "/addBorrow")
    public R addBorrowOld(@RequestBody Borrow borrow) {
        return borrowService.addBorrow(borrow);
    }
}