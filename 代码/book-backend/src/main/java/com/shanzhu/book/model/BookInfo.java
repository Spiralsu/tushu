package com.shanzhu.book.model;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BookInfo implements Serializable {
    private Integer bookid;
    private String bookname;
    private String bookauthor;
    private BigDecimal bookprice;
    private Integer booktypeid;
    private String booktypename; // 冗余字段用于展示
    private String bookdesc;
    private Integer isborrowed; // 0:有库存 1:无库存/已借出
    private String bookimg;

    // 【新增】库存字段
    private Integer bookcount; // 总数
    private Integer inventory; // 剩余库存
}