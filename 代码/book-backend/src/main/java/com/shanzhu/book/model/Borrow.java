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
    private Date borrowtime;      // 实际借出时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date returntime;      // 归还时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date applytime;       // 新增：申请时间

    private String borrowreason;  // 新增：申请理由
    private Integer state;        // 新增：0审核中 1待交接 2漂流中 3已归还 4已驳回
}