package com.shanzhu.book.service.impl;

import com.shanzhu.book.mapper.BookInfoMapper;
import com.shanzhu.book.mapper.BookWishMapper;
import com.shanzhu.book.mapper.MessageMapper;
import com.shanzhu.book.mapper.UserMapper;
import com.shanzhu.book.model.BookInfo;
import com.shanzhu.book.model.BookWish;
import com.shanzhu.book.model.Message;
import com.shanzhu.book.model.User;
import com.shanzhu.book.service.BookInfoService;
import com.shanzhu.book.utils.WechatPushUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
        return bookInfoMapper.selectCount();
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
                // 只有今天加分还没满5分，且没满100分时才加
                if (todayAdded < 5 && currentScore < 100) {
                    // 发布书籍理论加2分，但不能超过(5-已加分数)的剩余额度
                    int canAdd = Math.min(2, 5 - todayAdded);
                    int finalScore = Math.min(100, currentScore + canAdd);
                    int actualAdded = finalScore - currentScore;

                    if (actualAdded > 0) {
                        uploader.setCreditScore(finalScore);
                        uploader.setTodayAddScore(todayAdded + actualAdded);
                        userMapper.updateByPrimaryKeySelective(uploader);
                        messageMapper.insert(new Message(uploader.getUserid(), "【信用奖励】发布书籍奖励生效！本次恢复 " + actualAdded + " 分 (今日已累计恢复 " + (todayAdded + actualAdded) + "/5 分)，当前信用分：" + finalScore + " 分。"));
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
}