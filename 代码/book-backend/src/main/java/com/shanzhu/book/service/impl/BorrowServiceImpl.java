package com.shanzhu.book.service.impl;

import com.shanzhu.book.mapper.BookInfoMapper;
import com.shanzhu.book.mapper.BorrowMapper;
import com.shanzhu.book.model.BookInfo;
import com.shanzhu.book.model.Borrow;
import com.shanzhu.book.service.BorrowService;
import com.shanzhu.book.utils.PageUtils;
import com.shanzhu.book.utils.R;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BorrowServiceImpl implements BorrowService {

    @Resource
    private BorrowMapper borrowMapper;
    @Resource
    private BookInfoMapper bookInfoMapper;

    @Override
    @Transactional
    public R addBorrow(Borrow borrow) {
        // 1. 检查书籍
        BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());
        if (book == null) return R.error("书籍不存在");
        if (book.getIsborrowed() != 0) {
            return R.error("该书当前不可申请（已被借出或正在申请中）");
        }

        // 2. 锁定书籍状态
        book.setIsborrowed((byte) 2); // 2: 申请中
        bookInfoMapper.updateByPrimaryKeySelective(book);

        // 3. 插入申请记录
        borrow.setState(0); // 0: 审核中
        borrow.setApplytime(new Date());
        int i = borrowMapper.insert(borrow);

        return i > 0 ? R.ok("申请已提交，等待审核") : R.error("申请提交失败");
    }

    @Override
    public R queryBorrowsByPage(Integer page, Integer size, Integer userId) {
        Map<String, Object> params = new HashMap<>();
        int begin = (page - 1) * size;
        params.put("begin", begin);
        params.put("size", size);
        if(userId != null) params.put("userId", userId);

        List<Borrow> list = borrowMapper.selectAllByLimit(params);
        int total = borrowMapper.selectCount(params);

        return R.ok().put("data", list).put("count", total);
    }

    @Override
    @Transactional
    public R auditBorrow(Integer borrowId, Integer state, String feedback) {
        Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);
        if(borrow == null) return R.error("记录不存在");

        borrow.setState(state);
        BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());

        // 状态流转逻辑
        if (state == 1) {
            // 审核通过，等待线下交接，书籍状态保持"申请中/锁定"
        } else if (state == 4) {
            // 驳回，释放书籍
            if(book != null) {
                book.setIsborrowed((byte) 0);
                bookInfoMapper.updateByPrimaryKeySelective(book);
            }
        } else if (state == 2) {
            // 确认交接，正式借出
            borrow.setBorrowtime(new Date());
            if(book != null) {
                book.setIsborrowed((byte) 1); // 1: 借出中
                bookInfoMapper.updateByPrimaryKeySelective(book);
            }
        } else if (state == 3) {
            // 归还
            borrow.setReturntime(new Date());
            if(book != null) {
                book.setIsborrowed((byte) 0); // 0: 在库
                bookInfoMapper.updateByPrimaryKeySelective(book);
            }
        }

        borrowMapper.updateByPrimaryKeySelective(borrow);
        return R.ok("操作成功");
    }

    // --- 兼容旧方法 ---
    @Override
    public Integer getCount() { return borrowMapper.selectCount(null); }
    @Override
    public Integer getSearchCount(Map<String, Object> params) { return borrowMapper.selectCount(params); }
    @Override
    public List<Borrow> searchBorrowsByPage(Map<String, Object> params) { return borrowMapper.selectAllByLimit(params); }
    @Override
    public Integer deleteBorrow(Borrow borrow) { return borrowMapper.deleteByPrimaryKey(borrow.getBorrowid()); }
    @Override
    public Integer deleteBorrows(List<Borrow> borrows) { return 0; }
    @Override
    public Integer updateBorrow(Borrow borrow) { return borrowMapper.updateByPrimaryKeySelective(borrow); }
    @Override
    public Integer updateBorrow2(Borrow borrow) { return borrowMapper.updateByPrimaryKeySelective(borrow); } // 修正为调用同一个更新
    @Override
    public Borrow queryBorrowsById(Integer borrowid) { return borrowMapper.selectByPrimaryKey(borrowid); }
    @Override
    public Integer addBorrow2(Borrow borrow) { return borrowMapper.insert(borrow); } // 简单插入，无逻辑检查
}