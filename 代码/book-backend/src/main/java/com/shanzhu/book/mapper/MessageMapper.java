package com.shanzhu.book.mapper;

import com.shanzhu.book.model.Message;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface MessageMapper {
    int insert(Message record);

    // 查询某用户的消息列表
    List<Message> selectByUserId(@Param("userid") Integer userid);

    // 标记消息为已读（可选功能）
    int updateReadStatus(@Param("messageid") Integer messageid);
}