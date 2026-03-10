package com.shanzhu.book.service.impl;

import com.shanzhu.book.mapper.BookInfoMapper;
import com.shanzhu.book.mapper.BorrowMapper;
import com.shanzhu.book.mapper.MessageMapper;
import com.shanzhu.book.mapper.UserMapper;
import com.shanzhu.book.model.BookInfo;
import com.shanzhu.book.model.Borrow;
import com.shanzhu.book.model.Message;
import com.shanzhu.book.model.User;
import com.shanzhu.book.service.BorrowService;
import com.shanzhu.book.utils.R;
import com.shanzhu.book.utils.WechatPushUtils;
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
    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional
    public R addBorrow(Borrow borrow) {
        // 【风控拦截 1】：查出当前借书人的信用分，低于 60 分直接熔断！
        User currentUser = userMapper.selectByPrimaryKey(borrow.getUserid());
        if (currentUser != null && currentUser.getCreditScore() != null && currentUser.getCreditScore() < 60) {
            return R.error("您的信用分低于 60 分，已被系统限制借阅权限！如有特殊情况请线下联系管理员。");
        }

        BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());
        if (book == null) return R.error("图书不存在");

        // 【风控拦截 2】：禁止自己借阅自己的书籍！
        if (book.getUploaderid() != null && book.getUploaderid().equals(borrow.getUserid())) {
            return R.error("不能申请借阅自己发布（或当前持有）的书籍哦！");
        }

        if (book.getInventory() <= 0) {
            return R.error("当前图书库存不足，正在漂流中");
        }

        borrow.setState(0); // 0-待审核
        borrow.setApplytime(new Date());
        if (borrow.getBorrowreason() == null) borrow.setBorrowreason("用户申请漂流");

        int row = borrowMapper.insert(borrow);

        // 有人申请时，立刻通知发布者！
        if (row > 0 && book.getUploaderid() != null) {
            String msgContent = "【系统通知】有同学申请借阅了您发布的书籍《" + book.getBookname() + "》！\n" +
                    "TA 的申请理由是：[" + borrow.getBorrowreason() + "]\n" +
                    "目前正在等待管理员审核，请留意后续通知。";
            messageMapper.insert(new Message(book.getUploaderid(), msgContent));

            // 【WxPusher 场景1：有人申请借书】
            User uploader = userMapper.selectByPrimaryKey(book.getUploaderid());
            if (uploader != null && uploader.getOpenId() != null) {
                WechatPushUtils.pushMessage(
                        uploader.getOpenId(),
                        "📚 新的漂流申请",
                        "您好！您发布的《" + book.getBookname() + "》有人申请啦！<br/>留言：" + borrow.getBorrowreason() + "<br/>请尽快登录系统进行审核！"
                );
            }
        }

        return row > 0 ? R.ok("漂流申请已提交，请等待发布者审核") : R.error("申请失败");
    }

    @Override
    @Transactional
    public R auditBorrow(Integer borrowId, Integer state, String feedback) {
        Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);
        if (borrow == null) return R.error("记录不存在");
        BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());

        if (state == 1) { // 审核通过
            if (book.getInventory() <= 0) return R.error("审核失败：库存不足");

            book.setInventory(book.getInventory() - 1);
            if (book.getInventory() == 0) book.setIsborrowed(1); // 标记为无货
            bookInfoMapper.updateByPrimaryKeySelective(book);

            String secretCode = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
            borrow.setState(4); // 4-待交接
            borrow.setSecretCode(secretCode);
            borrowMapper.updateByPrimaryKeySelective(borrow);

            String contact = (book.getContactinfo() != null && !book.getContactinfo().isEmpty()) ? book.getContactinfo() : "无特殊说明，请当面沟通。";
            String msgToApplicant = "【系统通知】你的漂流申请《" + book.getBookname() + "》已通过！\n" +
                    "对方交接说明：【" + contact + "】\n" +
                    "你的专属提货暗号为：【" + secretCode + "】。碰面拿到书后，请将此暗号出示给发布者，由发布者在系统中进行核销。";
            messageMapper.insert(new Message(borrow.getUserid(), msgToApplicant));

            // 【WxPusher 场景2：审核通过通知】
            User applyUser = userMapper.selectByPrimaryKey(borrow.getUserid());
            if (applyUser != null && applyUser.getOpenId() != null) {
                WechatPushUtils.pushMessage(
                        applyUser.getOpenId(),
                        "✅ 审核通过通知",
                        "恭喜！您对《" + book.getBookname() + "》的借阅申请已通过！<br/>请登录系统查看交接暗号和对方地址，准备线下交接。"
                );
            }

            if (book.getUploaderid() != null) {
                String msgToUploader = "【系统通知】您发布的书籍《" + book.getBookname() + "》借阅申请已通过审核！\n" +
                        "请准备好书籍等待交接。对方在拿到书后会向您出示 6 位数字暗号，请您在“借阅信息管理”的【我借出的】列表中点击核销暗号，完成最终交接！";
                messageMapper.insert(new Message(book.getUploaderid(), msgToUploader));
            }

            return R.ok("审核通过，已双向下发交接通知");
        } else {
            borrow.setState(3); // 3-驳回
            borrowMapper.updateByPrimaryKeySelective(borrow);

            String reason = feedback != null ? feedback : "无";
            Message msg = new Message(borrow.getUserid(), "【系统通知】您的漂流申请《" + book.getBookname() + "》被驳回。原因：" + reason);
            messageMapper.insert(msg);

            // 【WxPusher 场景3：审核驳回通知】
            User applyUser = userMapper.selectByPrimaryKey(borrow.getUserid());
            if (applyUser != null && applyUser.getOpenId() != null) {
                WechatPushUtils.pushMessage(
                        applyUser.getOpenId(),
                        "❌ 审核驳回通知",
                        "很遗憾，您对《" + book.getBookname() + "》的申请已被拒绝。<br/>原因：" + reason
                );
            }

            return R.ok("已驳回申请");
        }
    }

    @Override
    @Transactional
    public R verifyCode(Integer borrowId, String secretCode) {
        Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);
        if (borrow == null) return R.error("记录不存在");
        if (borrow.getState() != 4) return R.error("当前状态不支持验证暗号");

        if (borrow.getSecretCode() == null || !borrow.getSecretCode().equals(secretCode)) {
            return R.error("暗号错误，请核对后再试！");
        }

        borrow.setState(1);
        borrow.setBorrowtime(new Date());
        borrowMapper.updateByPrimaryKeySelective(borrow);
        return R.ok("暗号正确！交接成功，书籍正式进入漂流中。");
    }

    @Override
    @Transactional
    public Integer returnBook(Integer borrowId, Integer bookId, String returnMsg, String contactInfo) {
        Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);
        if (borrow == null || borrow.getState() != 1) return 0;

        borrow.setState(2);
        borrow.setReturntime(new Date());
        borrow.setReturnmsg(returnMsg);
        borrowMapper.updateByPrimaryKeySelective(borrow);

        BookInfo book = bookInfoMapper.selectByPrimaryKey(bookId);
        if (book != null) {
            Integer oldUploaderId = book.getUploaderid();
            book.setInventory(book.getInventory() + 1);
            if (book.getInventory() > 0) book.setIsborrowed(0);

            book.setUploaderid(borrow.getUserid());

            if (contactInfo != null && !contactInfo.trim().isEmpty()) {
                book.setContactinfo(contactInfo);
            } else {
                book.setContactinfo("新主人暂未留下具体地址，请申请后通过系统留言沟通。");
            }
            bookInfoMapper.updateByPrimaryKeySelective(book);

            String newOwnerMsg = "【漂流接力】恭喜！您已读完《" + book.getBookname() + "》。\n" +
                    "由于这是漂流书籍，书本物理上现在由您保管。系统已自动将该书转移到了您的【我借出的】列表中。\n" +
                    "等待下一位有缘人申请时，您将作为新的传递者与TA当面交接暗号！让知识继续流动吧！";
            messageMapper.insert(new Message(borrow.getUserid(), newOwnerMsg));

            if (oldUploaderId != null && !oldUploaderId.equals(borrow.getUserid())) {
                messageMapper.insert(new Message(oldUploaderId, "【系统通知】您最初发布的《" + book.getBookname() + "》已经被前一位读者读完，并成功接力成为了下一站的火种。感谢您的分享！"));
            }
        }
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

    @Override
    @Transactional
    public R cancelBorrow(Integer borrowId, String reason) {
        Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);
        // 【新增】：允许 state=4(待交接) 和 state=0(待审核) 两个状态下进行撤销
        if (borrow == null || (borrow.getState() != 4 && borrow.getState() != 0)) {
            return R.error("当前状态无法撤销");
        }

        BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());

        // 【关键逻辑】：如果是待交接(4)撤销，需要把库存还回去；如果是待审核(0)撤销，因为之前还没扣库存，所以不需要恢复！
        if (borrow.getState() == 4 && book != null) {
            book.setInventory(book.getInventory() + 1);
            if (book.getInventory() > 0) book.setIsborrowed(0);
            bookInfoMapper.updateByPrimaryKeySelective(book);
        }

        // 修改状态为 5-已撤销
        borrow.setState(5);
        borrowMapper.updateByPrimaryKeySelective(borrow);

        String stageStr = (borrow.getState() == 0) ? "【申请阶段】" : "【交接阶段】";

        // 给自己发站内信
        messageMapper.insert(new Message(borrow.getUserid(), "【系统通知】您已成功撤销对《" + book.getBookname() + "》的借阅。"));

        // 给发布者发站内信和微信推送
        if (book != null && book.getUploaderid() != null) {
            messageMapper.insert(new Message(book.getUploaderid(), "【系统通知】对方在" + stageStr + "撤销了对《" + book.getBookname() + "》的借阅。原因：[" + reason + "]。"));

            // 【WxPusher：借阅撤销通知】
            User uploader = userMapper.selectByPrimaryKey(book.getUploaderid());
            if (uploader != null && uploader.getOpenId() != null) {
                WechatPushUtils.pushMessage(
                        uploader.getOpenId(),
                        "⚠️ 借阅撤销通知",
                        "借书人取消了对《" + book.getBookname() + "》的借阅申请。<br/>撤销阶段：" + stageStr + "<br/>对方留言：" + reason + "<br/>请悉知，无需再进行处理。"
                );
            }
        }

        return R.ok("撤销成功！");
    }

    @Override
    @Transactional
    public R nudgeUploader(Integer borrowId, String message) {
        Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);
        if(borrow == null || borrow.getState() != 4) return R.error("当前状态无法发送协商");

        BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());
        if(book == null || book.getUploaderid() == null) return R.error("找不到发布者信息，无法传话");

        String content = "【紧急交接协商】您发布的《" + book.getBookname() + "》的借阅者遇到了交接困难。\n" +
                "TA 的紧急留言是：【" + message + "】\n" +
                "请您通过上述方式联系 TA，或约定一个新的公共地点完成交接（对方的提书暗号依然有效，记得在此系统中核销哦）。";

        messageMapper.insert(new Message(book.getUploaderid(), content));

        // 【WxPusher 场景4：交接协商通知】
        User uploader = userMapper.selectByPrimaryKey(book.getUploaderid());
        if (uploader != null && uploader.getOpenId() != null) {
            WechatPushUtils.pushMessage(
                    uploader.getOpenId(),
                    "🆘 紧急交接协商",
                    "借书人遇到了交接困难！<br/>关于书籍《" + book.getBookname() + "》，对方留言内容：" + message + "<br/>请尽快登录系统查看详情并联系对方！"
            );
        }

        return R.ok("协商消息已成功发送给发布者！请留意对方的联系。");
    }

    @Override
    @Transactional
    public R reportLoss(Integer borrowId, String reason) {
        Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);
        if (borrow == null) return R.error("记录不存在");

        borrow.setState(6);
        borrow.setReturntime(new Date());
        borrow.setReturnmsg("【遗失登记】" + reason);
        borrowMapper.updateByPrimaryKeySelective(borrow);

        BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());
        book.setInventory(0);
        book.setIsborrowed(1);
        book.setContactinfo("此书已遗憾退出漂流");
        bookInfoMapper.updateByPrimaryKeySelective(book);

        messageMapper.insert(new Message(book.getUploaderid(), "【遗失通知】很遗憾，您发布的《" + book.getBookname() + "》被读者登记为遗失/损毁。原因：" + reason));

        // 【WxPusher 场景5：书籍遗憾报损】
        User uploader = userMapper.selectByPrimaryKey(book.getUploaderid());
        if (uploader != null && uploader.getOpenId() != null) {
            WechatPushUtils.pushMessage(
                    uploader.getOpenId(),
                    "💔 书籍遗憾报损",
                    "十分抱歉，您发布的《" + book.getBookname() + "》被当前借阅人登记为遗失/损坏。<br/>原因：" + reason + "<br/>此书已退出漂流系统。"
            );
        }

        return R.ok("登记成功，该书已终止漂流");
    }

    @Override
    public R urgeReturn(Integer borrowId) {
        Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);
        BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());


        // 【核心拦截器】：获取当前借阅者自己申请的借阅天数（没填默认30）
        int allowDays = (borrow.getBorrowDays() != null) ? borrow.getBorrowDays() : 30;

        // 计算时间差：如果还没到期，无情拦截！
        if (borrow.getBorrowtime() != null) {
            long borrowedMillis = System.currentTimeMillis() - borrow.getBorrowtime().getTime();
            long allowMillis = (long) allowDays * 24 * 60 * 60 * 1000;

            // 如果不是测试的 0 天，且借阅时间还没超过允许的时间
            if (allowDays != 0 && borrowedMillis < allowMillis) {
                // 计算还差几天到期
                long remainDays = (allowMillis - borrowedMillis) / (1000 * 3600 * 24) + 1;
                return R.error("对方还在合理借阅期内（剩余 " + remainDays + " 天），请耐心等待哦~");
            }
        }

        // 校验通过，开始发站内信
        messageMapper.insert(new Message(borrow.getUserid(), "【催还通知】⏰ 您借阅的《" + book.getBookname() + "》已经到期啦，请尽快阅读并传递哦！"));

        // 【WxPusher 场景6：一键催还提醒】
        User targetUser = userMapper.selectByPrimaryKey(borrow.getUserid());
        if (targetUser != null && targetUser.getOpenId() != null) {
            WechatPushUtils.pushMessage(
                    targetUser.getOpenId(),
                    "⏰ 一键催还提醒",
                    "上一任主人在催您啦！<br/>您借阅的《" + book.getBookname() + "》已达期望借阅期限，请尽快阅读完毕并在系统中点击【归还/传递】交接给下一位书友哦！"
            );
        }
        return R.ok("催还通知已成功发送给该读者！");
    }

    // 绑定微信 UID
    @Override
    public R bindWx(Integer userId, String openId) {
        User user = new User();
        user.setUserid(userId);
        user.setOpenId(openId);
        userMapper.updateByPrimaryKeySelective(user);
        return R.ok("微信绑定成功");
    }

    // 解除微信绑定
    @Override
    public R unbindWx(Integer userId) {
        User user = new User();
        user.setUserid(userId);
        user.setOpenId(""); // 清空数据库中的绑定记录
        userMapper.updateByPrimaryKeySelective(user);
        return R.ok("微信解绑成功");
    }

    // 【全新风控毒点】：逾期强制收回并扣除信用分 (温和版)
    @Override
    @Transactional
    public R forcePenalize(Integer borrowId) {
        Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);
        if (borrow == null || borrow.getState() != 1) return R.error("当前状态无法执行强制收回");

        BookInfo book = bookInfoMapper.selectByPrimaryKey(borrow.getBookid());
        User badUser = userMapper.selectByPrimaryKey(borrow.getUserid());

        // 1. 温和扣分：只扣除 10 信用分
        if (badUser != null) {
            int currentScore = (badUser.getCreditScore() != null) ? badUser.getCreditScore() : 100;
            badUser.setCreditScore(Math.max(0, currentScore - 10)); // 最低扣到 0 分
            userMapper.updateByPrimaryKeySelective(badUser);
        }

        // 2. 强制结束本次借阅 (设为状态7-逾期强裁)
        borrow.setState(7);
        borrow.setReturntime(new Date());
        borrow.setReturnmsg("【系统强制收回】因逾期未还且无视催还，已扣除 10 信用分");
        borrowMapper.updateByPrimaryKeySelective(borrow);

        // 3. 恢复图书库存，重新上架
        if (book != null) {
            book.setInventory(book.getInventory() + 1);
            if (book.getInventory() > 0) book.setIsborrowed(0);
            bookInfoMapper.updateByPrimaryKeySelective(book);
        }

        // 4. 微信通知这个“小迷糊”
        if (badUser != null && badUser.getOpenId() != null) {
            WechatPushUtils.pushMessage(
                    badUser.getOpenId(),
                    "⛔ 信用降级与强制收回通知",
                    "警告！因您逾期未归还《" + book.getBookname() + "》，系统已强制结束借阅，并扣除您 10 信用分！<br/>当前剩余信用分：" + badUser.getCreditScore() + "分。<br/>⚠️ 若低于 60 分将永久封禁借阅权限！如有特殊情况，请线下联系管理员说明并申请恢复信用分。"
            );
        }

        return R.ok("已成功强制收回，并扣除对方 10 信用分！");
    }

    @Override public Integer deleteBorrow(Borrow borrow) { return borrowMapper.deleteByPrimaryKey(borrow.getBorrowid()); }
    @Override public Integer deleteBorrows(List<Borrow> borrows) { return 0; }
    @Override public Integer updateBorrow(Borrow borrow) { return borrowMapper.updateByPrimaryKeySelective(borrow); }
    @Override public Borrow queryBorrowsById(Integer borrowid) { return borrowMapper.selectByPrimaryKey(borrowid); }
    @Override public Integer updateBorrow2(Borrow borrow) { return borrowMapper.updateByPrimaryKeySelective(borrow); }
    @Override public Integer addBorrow2(Borrow borrow) { return borrowMapper.insert(borrow); }
}