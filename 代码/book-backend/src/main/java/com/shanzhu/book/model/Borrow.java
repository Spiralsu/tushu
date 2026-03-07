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
}