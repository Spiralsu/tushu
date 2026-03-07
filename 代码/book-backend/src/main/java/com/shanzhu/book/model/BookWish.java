package com.shanzhu.book.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

@Data
public class BookWish {
    private Integer wishId;
    private Integer userId;
    private String userName;
    private String bookName;
    private String wishDesc;
    private Integer state;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private Integer fulfillUserId;
}