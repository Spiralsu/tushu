package com.shanzhu.book.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BookInfo {
    private Integer bookid;
    private String bookname;
    private String bookauthor;
    private BigDecimal bookprice;
    private Integer booktypeid;
    private String booktypename;
    private String bookdesc;
    private Byte isborrowed;      // 0:在库 1:漂流中 2:申请中
    private String bookimg;
    private Integer uploaderid;   // 新增：上传者ID
    private Integer auditstatus;  // 新增：0待审核 1已通过
}