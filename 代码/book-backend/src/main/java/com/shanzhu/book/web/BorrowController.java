package com.shanzhu.book.web;

import com.shanzhu.book.exception.BookNotEnoughException;
import com.shanzhu.book.exception.OperationFailureException;
import com.shanzhu.book.model.BookInfo;
import com.shanzhu.book.model.Borrow;
import com.shanzhu.book.service.BookInfoService;
import com.shanzhu.book.service.BorrowService;
import com.shanzhu.book.utils.PageUtils;
import com.shanzhu.book.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 图书借阅 控制层
 *
 * @author: ShanZhu
 * @date: 2023-12-31
 */
@Slf4j
@RestController
@RequestMapping(value = "/borrow")
public class BorrowController {

    @Resource
    private BorrowService borrowService;

    @Resource
    private BookInfoService bookInfoService;

    /**
     * 分页查询借阅
     *
     * @param params {page, limit, userid, bookid}
     * @return 借阅数据
     */
    @RequestMapping(value = "/queryBorrowsByPage")
    public Map<String, Object> queryBorrowsByPage(@RequestParam Map<String, Object> params) {
        PageUtils.parsePageParams(params);
        int count = borrowService.getSearchCount(params);
        List<Borrow> borrows = borrowService.searchBorrowsByPage(params);
        return R.getListResultMap(0, "success", count, borrows);
    }

    /**
     * 获得数量
     *
     * @return 数量
     */
    @RequestMapping(value = "/getCount")
    public Integer getCount() {
        return borrowService.getCount();
    }

    /**
     * 添加借阅
     *
     * @param borrow 借阅信息
     * @return 结果
     */
    @RequestMapping(value = "/addBorrow")
    public Integer addBorrow(@RequestBody Borrow borrow) {
        return borrowService.addBorrow(borrow);
    }

    /**
     * 删除借阅
     *
     * @param borrow 借阅信息
     * @return 结果
     */
    @RequestMapping(value = "/deleteBorrow")
    public Integer deleteBorrow(@RequestBody Borrow borrow) {
        return borrowService.deleteBorrow(borrow);
    }

    /**
     * 批量删除借阅
     *
     * @param borrows 借阅信息
     * @return 结果
     */
    @RequestMapping(value = "/deleteBorrows")
    public Integer deleteBorrows(@RequestBody List<Borrow> borrows) {
        return borrowService.deleteBorrows(borrows);
    }

    /**
     * 更新借阅
     *
     * @param borrow 借阅信息
     * @return 结果
     */
    @RequestMapping(value = "/updateBorrow")
    public Integer updateBorrow(@RequestBody Borrow borrow) {
        return borrowService.updateBorrow(borrow);
    }

    /**
     * 借书
     *
     * @param userid 用户id
     * @param bookid 书籍id
     * @return 结果
     */
    @RequestMapping(value = {"/borrowBook", "/reader/borrowBook"})
    @Transactional
    public Integer borrowBook(Integer userid, Integer bookid) {
        try {
            // 查询该书的情况
            BookInfo theBook = bookInfoService.queryBookInfoById(bookid);

            if (theBook == null) {  // 图书不存在
                throw new NullPointerException("图书" + bookid + "不存在");
            } else if (theBook.getIsborrowed() == 1) {  // 已经被借
                throw new BookNotEnoughException("图书" + bookid + "库存不足（已经被借走）");
            }

            // 更新图书表的isBorrowed
            BookInfo bookInfo = new BookInfo();
            bookInfo.setBookid(bookid);
            bookInfo.setIsborrowed((byte) 1);
            Integer res2 = bookInfoService.updateBookInfo(bookInfo);
            if (res2 == 0) throw new OperationFailureException("图书" + bookid + "更新被借信息失败");

            // 添加一条记录到borrow表
            Borrow borrow = new Borrow();
            borrow.setUserid(userid);
            borrow.setBookid(bookid);
            borrow.setBorrowtime(new Date(System.currentTimeMillis()));
            Integer res1 = borrowService.addBorrow2(borrow);
            if (res1 == 0) throw new OperationFailureException("图书" + bookid + "添加借阅记录失败");

        } catch (Exception e) {
            log.error("发生异常，进行手动回滚", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
        return 1;
    }

    /**
     * 还书
     *
     * @param borrowid 借阅id
     * @param bookid   书籍id
     * @return 结果
     */
    @RequestMapping(value = {"/returnBook", "/reader/returnBook"})
    @Transactional
    public Integer returnBook(Integer borrowid, Integer bookid) {
        try {
            // 查询该书的情况
            BookInfo theBook = bookInfoService.queryBookInfoById(bookid);
            // 查询借书的情况
            Borrow theBorrow = borrowService.queryBorrowsById(borrowid);

            // 图书不存在
            if (theBook == null) {
                throw new NullPointerException("图书" + bookid + "不存在");
            } else if (theBorrow == null) {
                //结束记录不存在
                throw new NullPointerException("借书记录" + bookid + "不存在");
            } else if (theBorrow.getReturntime() != null) {
                // 已经还过书
                throw new BookNotEnoughException("图书" + bookid + "已经还过了");
            }

            // 更新图书表的isBorrowed
            BookInfo bookInfo = new BookInfo();
            bookInfo.setBookid(bookid);
            bookInfo.setIsborrowed((byte) 0);
            Integer res2 = bookInfoService.updateBookInfo(bookInfo);
            if (res2 == 0) throw new OperationFailureException("图书" + bookid + "更新被借信息失败");

            // 更新Borrow表，更新结束时间
            Borrow borrow = new Borrow();
            borrow.setBorrowid(borrowid);
            borrow.setReturntime(new Date(System.currentTimeMillis()));
            Integer res1 = borrowService.updateBorrow2(borrow);
            if (res1 == 0) throw new OperationFailureException("图书" + bookid + "更新借阅记录失败");

        } catch (Exception e) {
            log.error("发生异常，进行手动回滚", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
        return 1;
    }

}
