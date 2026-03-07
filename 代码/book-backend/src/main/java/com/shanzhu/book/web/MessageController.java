package com.shanzhu.book.web;

import com.shanzhu.book.mapper.MessageMapper;
import com.shanzhu.book.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageMapper messageMapper;

    /**
     * 根据前端传来的用户ID，拉取该用户的所有站内消息
     * 对应前端请求：/message/getByUserId?userid=xxx
     */
    @GetMapping("/getByUserId")
    public List<Message> getByUserId(@RequestParam("userid") Integer userid) {
        // 调用你现有的 Mapper 获取数据
        return messageMapper.selectByUserId(userid);
    }

    /**
     * (保留备用) 后续如果管理员审核通过后需要下发消息，可以调用此接口
     */
    @PostMapping("/send")
    public int sendMessage(@RequestBody Message message) {
        return messageMapper.insert(message);
    }
}