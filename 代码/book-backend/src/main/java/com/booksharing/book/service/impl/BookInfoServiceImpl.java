package com.booksharing.book.service.impl;

import com.booksharing.book.mapper.BookInfoMapper;
import com.booksharing.book.mapper.BookWishMapper;
import com.booksharing.book.mapper.MessageMapper;
import com.booksharing.book.mapper.UserMapper;
import com.booksharing.book.model.BookInfo;
import com.booksharing.book.model.BookWish;
import com.booksharing.book.model.Message;
import com.booksharing.book.model.User;
import com.booksharing.book.service.BookInfoService;
import com.booksharing.book.utils.WechatPushUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookInfoServiceImpl implements BookInfoService {

    @Resource
    private BookInfoMapper bookInfoMapper;

    @Resource
    private BookWishMapper bookWishMapper;

    @Resource
    private MessageMapper messageMapper;

    // 【新增】：注入 UserMapper 用于查询许愿人的微信 OpenId
    @Resource
    private UserMapper userMapper;

    @Override
    public Integer getCount() {
        Map<String, Object> params = new HashMap<>();
        params.put("auditstatus", 1);
        return bookInfoMapper.selectCountBySearch(params);
    }

    @Override
    public List<BookInfo> queryBookInfos() {
        return bookInfoMapper.selectAll();
    }

    @Override
    public Integer getSearchCount(Map<String, Object> params) {
        return bookInfoMapper.selectCountBySearch(params);
    }

    @Override
    public List<BookInfo> searchBookInfosByPage(Map<String, Object> params) {
        return bookInfoMapper.selectBySearch(params);
    }

    @Override
    public Integer addBookInfo(BookInfo bookInfo) {
        if (bookInfo.getBookcount() != null && bookInfo.getInventory() == null) {
            bookInfo.setInventory(bookInfo.getBookcount());
        }
        if (bookInfo.getIsborrowed() == null) {
            bookInfo.setIsborrowed(0);
        }

        // 【新增】刚发布的图书默认为待审核状态
        if (bookInfo.getAuditstatus() == null) {
            bookInfo.setAuditstatus(0);
        }

        int result = bookInfoMapper.insert(bookInfo);

        // 发布成功通知与严格【做贡献回血策略】
        if (result > 0 && bookInfo.getUploaderid() != null) {
            messageMapper.insert(new Message(bookInfo.getUploaderid(), "【系统通知】您的书籍《" + bookInfo.getBookname() + "》已发布！"));

            User uploader = userMapper.selectByPrimaryKey(bookInfo.getUploaderid());
            if (uploader != null && (uploader.getCreditScore() == null || uploader.getCreditScore() < 100)) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                String todayStr = sdf.format(new java.util.Date());
                String updateStr = uploader.getScoreUpdateDate() == null ? "" : sdf.format(uploader.getScoreUpdateDate());

                int todayAdded = uploader.getTodayAddScore() == null ? 0 : uploader.getTodayAddScore();
                // 如果今天还没加过分，重置今日额度
                if (!todayStr.equals(updateStr)) {
                    todayAdded = 0;
                    uploader.setScoreUpdateDate(new java.util.Date());
                }

                int currentScore = uploader.getCreditScore() == null ? 100 : uploader.getCreditScore();
                // 只有今天加分还没满5分，且没到100分时才加
                if (todayAdded < 5 && currentScore < 100) {
                    // 发布书籍理论上加2分，但不能超过(5-已加分数)的剩余额度
                    int canAdd = Math.min(2, 5 - todayAdded);
                    int finalScore = Math.min(100, currentScore + canAdd);
                    int actualAdded = finalScore - currentScore;

                    if (actualAdded > 0) {
                        uploader.setCreditScore(finalScore);
                        uploader.setTodayAddScore(todayAdded + actualAdded);
                        userMapper.updateByPrimaryKeySelective(uploader);
                        messageMapper.insert(new Message(uploader.getUserid(), "【信用奖励】发布书籍奖励生效！本次恢复 " + actualAdded + " 分(今日已累计恢复" + (todayAdded + actualAdded) + "/5 分)，当前信用分：" + finalScore + " 分。"));
                    }
                }
            }
        }

        // 响应心愿墙逻辑...
        if (result > 0 && bookInfo.getBookname() != null) {
            List<BookWish> wishes = bookWishMapper.selectUnfulfilledWishesByBookName(bookInfo.getBookname());
            for (BookWish wish : wishes) {
                messageMapper.insert(new Message(wish.getUserId(), "好消息！您在心愿广场求购的书籍《" + bookInfo.getBookname() + "》被发布啦！"));
                bookWishMapper.fulfillWish(wish.getWishId(), 0);
                User wisher = userMapper.selectByPrimaryKey(wish.getUserId());
                if (wisher != null && wisher.getOpenId() != null) {
                    WechatPushUtils.pushMessage(wisher.getOpenId(), "🌟 心愿被点亮！", "您求购的《" + bookInfo.getBookname() + "》被发布啦！快去借阅！");
                }
            }
        }
        return result;
    }

    @Override
    public Integer deleteBookInfo(BookInfo bookInfo) {
        return bookInfoMapper.deleteByPrimaryKey(bookInfo.getBookid());
    }

    @Override
    public Integer deleteBookInfos(List<BookInfo> bookInfos) {
        int count = 0;
        for (BookInfo bookInfo : bookInfos) {
            count += bookInfoMapper.deleteByPrimaryKey(bookInfo.getBookid());
        }
        return count;
    }

    @Override
    public Integer updateBookInfo(BookInfo bookInfo) {
        return bookInfoMapper.updateByPrimaryKeySelective(bookInfo);
    }

    @Override
    public BookInfo queryBookInfoById(Integer id) {
        return bookInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public Integer auditBook(Integer bookid, Integer auditstatus, String reason) {
        BookInfo book = bookInfoMapper.selectByPrimaryKey(bookid);
        if (book == null) return 0;

        book.setAuditstatus(auditstatus);
        int result = bookInfoMapper.updateByPrimaryKeySelective(book);

        if (result > 0) {
            String msg = auditstatus == 1 ? "【系统通知】您的书籍《" + book.getBookname() + "》已通过审核并上架！" : "【系统通知】您的书籍《" + book.getBookname() + "》审核未通过。原因：" + (reason == null || reason.isEmpty() ? "无" : reason);
            messageMapper.insert(new Message(book.getUploaderid(), msg));

            User uploader = userMapper.selectByPrimaryKey(book.getUploaderid());
            if (uploader != null && uploader.getOpenId() != null) {
                if (auditstatus == 1) {
                    WechatPushUtils.pushMessage(uploader.getOpenId(), "书籍审核通过", "您的《" + book.getBookname() + "》现已成功上架！");
                } else if (auditstatus == 2) {
                    WechatPushUtils.pushMessage(uploader.getOpenId(), "书籍发布被驳回", "您的《" + book.getBookname() + "》审核未通过，原因：" + (reason == null || reason.isEmpty() ? "无" : reason));
                }
            }
        }
        return result;
    }
}
