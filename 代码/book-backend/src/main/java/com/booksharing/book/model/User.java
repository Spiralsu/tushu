package com.booksharing.book.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class User implements Serializable {
    private Integer userid;
    private String username;
    private String studentid;
    private String userpassword;
    private Integer isadmin;
    private Integer status;       // 0待审核 1正常 2禁用

    private Integer gender;

    private String openId;

    private String department;

    private Integer creditScore;

    private Integer todayAddScore;
    private java.util.Date scoreUpdateDate;
}
