package com.shanzhu.book.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

@Data
public class Borrow {
    private Integer borrowid;
    private Integer userid;
    private String username;
    private String studentid;
    private Integer bookid;
    private String bookname;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date borrowtime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date returntime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date applytime;

    private String borrowreason;
    private Integer state;

    private String returnmsg;

    // 【本次新增】交接暗号字段
    private String secretCode;

    // 【新增】：借阅天数
    private Integer borrowDays;

    // 【新增】：用于前端展示借阅者的信用分（联查字段）
    private Integer creditScore;
}