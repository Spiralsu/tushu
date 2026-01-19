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
}