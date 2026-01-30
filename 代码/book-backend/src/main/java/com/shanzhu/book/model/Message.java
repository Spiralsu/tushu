package com.shanzhu.book.model;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class Message implements Serializable {
    private Integer messageid;
    private Integer userid;
    private String content;
    private Integer isread;
    private Date createtime;

    // 构造函数方便使用
    public Message() {}

    public Message(Integer userid, String content) {
        this.userid = userid;
        this.content = content;
        this.isread = 0;
        this.createtime = new Date();
    }
}