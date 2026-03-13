package com.shanzhu.book.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class User implements Serializable {
    private Integer userid;
    private String username;
    private String studentid;     // 新增：学号
    private String userpassword;
    private Integer isadmin;
    private Integer status;       // 新增：0待审核 1正常 2禁用
    // 【新增】性别字段
    private Integer gender;

    // 【新增】微信测试号的 OpenID
    private String openId;

    // 【新增】所属系部
    private String department;

    // 【新增】：信用分风控字段
    private Integer creditScore;

    // 【新增】：每日回血上限风控字段
    private Integer todayAddScore;
    private java.util.Date scoreUpdateDate;


}