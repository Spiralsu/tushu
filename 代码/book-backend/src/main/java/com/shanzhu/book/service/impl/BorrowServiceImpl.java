package com.shanzhu.book.service.impl;

import com.shanzhu.book.mapper.BookInfoMapper;
import com.shanzhu.book.mapper.BorrowMapper;
import com.shanzhu.book.mapper.MessageMapper;
import com.shanzhu.book.model.BookInfo;
import com.shanzhu.book.model.Borrow;
import com.shanzhu.book.model.Message;
import com.shanzhu.book.service.BorrowService;
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
    @Resource
    private MessageMapper messageMapper;

    @Override
    @Transactional
    public R addBorrow(Borrow borrow) {
        BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());
        if (book == null) return R.error("图书不存在");
        if (book.getInventory() <= 0) {
            return R.error("当前图书库存不足，正在漂流中");
        }

        borrow.setState(0); // 0-待审核
        borrow.setApplytime(new Date());
        if (borrow.getBorrowreason() == null) borrow.setBorrowreason("用户申请漂流");

        int row = borrowMapper.insert(borrow);
        return row > 0 ? R.ok("漂流申请已提交，请等待管理员审核") : R.error("申请失败");
    }

    @Override
    @Transactional
    public R auditBorrow(Integer borrowId, Integer state, String feedback) {
        Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);
        if (borrow == null) return R.error("记录不存在");
        BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());

        if (state == 1) { // 通过
            if (book.getInventory() <= 0) return R.error("审核失败：库存不足");

            // 扣减库存
            book.setInventory(book.getInventory() - 1);
            if (book.getInventory() == 0) book.setIsborrowed(1); // 标记为无货
            bookInfoMapper.updateByPrimaryKeySelective(book);

            borrow.setState(1); // 1-漂流中
            borrow.setBorrowtime(new Date());
            borrowMapper.updateByPrimaryKeySelective(borrow);

            Message msg = new Message(borrow.getUserid(), "您的漂流申请《" + book.getBookname() + "》已通过审核，请前往领取。");
            messageMapper.insert(msg);

            return R.ok("审核通过，库存已扣减");
        } else {
            borrow.setState(3); // 3-驳回
            borrowMapper.updateByPrimaryKeySelective(borrow);

            String reason = feedback != null ? feedback : "无";
            Message msg = new Message(borrow.getUserid(), "您的漂流申请《" + book.getBookname() + "》被驳回。原因：" + reason);
            messageMapper.insert(msg);

            return R.ok("已驳回申请");
        }
    }

    // 【核心修复】归还图书业务逻辑
    @Override
    @Transactional
    public Integer returnBook(Integer borrowId, Integer bookId) {
        // 1. 获取借阅记录
        Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);
        if (borrow == null || borrow.getState() != 1) {
            return 0; // 记录不存在或不是漂流中状态
        }

        // 2. 更新借阅状态 -> 2 (已归还)
        borrow.setState(2);
        borrow.setReturntime(new Date());
        borrowMapper.updateByPrimaryKeySelective(borrow);

        // 3. 更新图书库存
        BookInfo book = bookInfoMapper.selectByPrimaryKey(bookId);
        if (book != null) {
            book.setInventory(book.getInventory() + 1);
            // 只要有库存，就标记为可借 (isBorrowed=0)
            if (book.getInventory() > 0) {
                book.setIsborrowed(0);
            }
            bookInfoMapper.updateByPrimaryKeySelective(book);
        }

        // 4. 发送通知 (可选)
        // Message msg = new Message(borrow.getUserid(), "您已成功归还《" + book.getBookname() + "》，感谢您的爱心漂流！");
        // messageMapper.insert(msg);

        return 1;
    }

    @Override
    public R queryBorrowsByPage(Integer page, Integer size, Integer userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("begin", (page - 1) * size);
        params.put("size", size);
        if (userId != null) params.put("userId", userId);

        List<Borrow> list = borrowMapper.selectAllByLimit(params);
        int count = borrowMapper.selectCount(params);
        return R.getListResultMap(0, "success", count, list);
    }

    @Override
    public Integer getCount() { return borrowMapper.selectCount(new HashMap<>()); }

    @Override
    public Integer getSearchCount(Map<String, Object> params) { return borrowMapper.selectCount(params); }

    @Override
    public List<Borrow> searchBorrowsByPage(Map<String, Object> params) {
        if (!params.containsKey("begin")) {
            int page = Integer.parseInt(params.getOrDefault("page", "1").toString());
            int limit = Integer.parseInt(params.getOrDefault("limit", "10").toString());
            params.put("begin", (page - 1) * limit);
            params.put("size", limit);
        }
        return borrowMapper.selectAllByLimit(params);
    }

    // 接口兼容方法
    @Override public Integer deleteBorrow(Borrow borrow) { return borrowMapper.deleteByPrimaryKey(borrow.getBorrowid()); }
    @Override public Integer deleteBorrows(List<Borrow> borrows) { return 0; } // 简化
    @Override public Integer updateBorrow(Borrow borrow) { return borrowMapper.updateByPrimaryKeySelective(borrow); }
    @Override public Borrow queryBorrowsById(Integer borrowid) { return borrowMapper.selectByPrimaryKey(borrowid); }
    @Override public Integer updateBorrow2(Borrow borrow) { return borrowMapper.updateByPrimaryKeySelective(borrow); }
    @Override public Integer addBorrow2(Borrow borrow) { return borrowMapper.insert(borrow); }
}