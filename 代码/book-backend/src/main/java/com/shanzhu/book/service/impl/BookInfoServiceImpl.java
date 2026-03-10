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
        // 1. 自动设置库存
        if (bookInfo.getBookcount() != null && bookInfo.getInventory() == null) {
            bookInfo.setInventory(bookInfo.getBookcount());
        }

        if (bookInfo.getIsborrowed() == null) {
            bookInfo.setIsborrowed(0);
        }

        // 2. 将书籍插入数据库
        int result = bookInfoMapper.insert(bookInfo);

        // 3. 给发布者自己发一条“发布成功”的站内信
        if (result > 0 && bookInfo.getUploaderid() != null) {
            String successMsg = "【系统通知】恭喜！您的书籍《" + bookInfo.getBookname() + "》已成功发布到漂流大厅！\n" +
                    "感谢您为校园旧书漂流做出的贡献。当有同学申请借阅时，系统会第一时间通知您。";
            messageMapper.insert(new Message(bookInfo.getUploaderid(), successMsg));
        }

        // 4. 【核心升级】：检查心愿广场并发送微信推送！
        if (result > 0 && bookInfo.getBookname() != null) {
            List<BookWish> wishes = bookWishMapper.selectUnfulfilledWishesByBookName(bookInfo.getBookname());
            for (BookWish wish : wishes) {
                String content = "好消息！您在心愿广场求购的书籍《" + bookInfo.getBookname() + "》刚刚被发布啦，快去漂流大厅看看吧！";
                Message message = new Message(wish.getUserId(), content);
                messageMapper.insert(message);
                bookWishMapper.fulfillWish(wish.getWishId(), 0);

                // 【WxPusher 场景7：响应心愿通知】
                User wisher = userMapper.selectByPrimaryKey(wish.getUserId());
                if (wisher != null && wisher.getOpenId() != null) {
                    WechatPushUtils.pushMessage(
                            wisher.getOpenId(),
                            "🌟 您的求书心愿已被点亮！",
                            "激动人心的好消息！您在心愿墙求购的书籍《" + bookInfo.getBookname() + "》刚刚被热心书友发布啦！<br/><br/>快登录系统去【探索好书】里申请借阅吧，手慢无哦！"
                    );
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