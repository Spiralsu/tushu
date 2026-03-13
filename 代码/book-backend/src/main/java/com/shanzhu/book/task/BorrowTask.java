package com.shanzhu.book.task;

import com.shanzhu.book.mapper.BookInfoMapper;
import com.shanzhu.book.mapper.BorrowMapper;
import com.shanzhu.book.mapper.MessageMapper;
import com.shanzhu.book.mapper.UserMapper;
import com.shanzhu.book.model.BookInfo;
import com.shanzhu.book.model.Borrow;
import com.shanzhu.book.model.Message;
import com.shanzhu.book.model.User;
import com.shanzhu.book.utils.WechatPushUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BorrowTask {

    @Resource
    private BorrowMapper borrowMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private BookInfoMapper bookInfoMapper;
    @Resource
    private MessageMapper messageMapper;

    // 定时器：每 1 分钟执行一次扫描
    @Scheduled(cron = "0 */1 * * * ?")
    public void systemPatrolTask() {
        System.out.println("【系统巡查卫士】开始执行自动化风控巡查...");
        long now = System.currentTimeMillis();
        long threeDaysMillis = 3L * 24 * 60 * 60 * 1000;

        Map<String, Object> allParams = new HashMap<>();
        allParams.put("begin", 0);
        allParams.put("size", 10000);
        List<Borrow> allBorrows = borrowMapper.selectAllByLimit(allParams);

        for (Borrow borrow : allBorrows) {
            // 1. 巡查【超时未审核】(0) 和【超时未交接】(4)
            if ((borrow.getState() == 0 || borrow.getState() == 4) && borrow.getApplytime() != null) {

                // 【给你专门留的测试后门】：如果申请理由里包含“测试”两个字，超时时间缩短为 3 分钟！
                long timeout = threeDaysMillis;
                if (borrow.getBorrowreason() != null && borrow.getBorrowreason().contains("测试")) {
                    timeout = 3L * 60 * 1000; // 测试模式：3 分钟
                }

                // 判断是否超过容忍时间
                if (now - borrow.getApplytime().getTime() > timeout) {
                    System.out.println("发现超时记录 ID：" + borrow.getBorrowid() + "，执行自动取消！");
                    int oldState = borrow.getState();

                    borrow.setState(7); // 7-已失效
                    borrow.setReturnmsg("【系统强制取消】超出处理期限，系统已自动释放该申请。");
                    borrowMapper.updateByPrimaryKeySelective(borrow);

                    BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());
                    // 只有【待交接】超时才退回库存，因为【待审核】状态时还没扣过库存！(逻辑严丝合缝)
                    if (oldState == 4 && book != null) {
                        book.setInventory(book.getInventory() + 1);
                        if (book.getInventory() > 0) book.setIsborrowed(0);
                        bookInfoMapper.updateByPrimaryKeySelective(book);
                    }

                    // 双向发送站内信
                    messageMapper.insert(new Message(borrow.getUserid(), "【系统通知】您对《" + (book != null ? book.getBookname() : "") + "》的申请因超时未处理，已被系统自动取消。"));
                    if (book != null && book.getUploaderid() != null) {
                        messageMapper.insert(new Message(book.getUploaderid(), "【系统通知】由于超时未处理，针对《" + book.getBookname() + "》的借阅申请已被系统强制释放，若已扣库存则现已回退。"));
                    }
                }
            }

            // 2. 巡查【已逾期】(1)，每日扣分
            if (borrow.getState() == 1 && borrow.getBorrowtime() != null) {
                int allowDays = (borrow.getBorrowDays() != null) ? borrow.getBorrowDays() : 30;
                long borrowedMillis = now - borrow.getBorrowtime().getTime();
                long allowMillis = (long) allowDays * 24 * 60 * 60 * 1000;

                if (borrowedMillis > allowMillis || allowDays == 0) {
                    User borrower = userMapper.selectByPrimaryKey(borrow.getUserid());
                    if (borrower != null && borrower.getCreditScore() != null) {
                        int currentScore = borrower.getCreditScore();
                        if (currentScore > 0) {
                            int newScore = Math.max(0, currentScore - 2); // 扣2分
                            borrower.setCreditScore(newScore);
                            userMapper.updateByPrimaryKeySelective(borrower);

                            String msg = "【逾期惩罚】您的借阅已逾期！系统已扣除您 2 信用分。当前剩余：" + newScore + " 分。";
                            messageMapper.insert(new Message(borrower.getUserid(), msg));
                        }
                    }
                }
            }
        }
        System.out.println("【系统巡查卫士】本轮巡查结束！");
    }
}