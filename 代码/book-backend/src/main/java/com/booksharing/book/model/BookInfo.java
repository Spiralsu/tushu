package com.booksharing.book.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class BookInfo implements Serializable {
    private Integer bookid;
    private String bookname;
    private String bookauthor;
    private Integer booktypeid;
    private String booktypename;
    private String bookdesc;
    private Integer isborrowed; // 0:有库存 1:无库存/已借出
    private String bookimg;

    private Integer bookcount; // 总数
    private Integer inventory; // 剩余库存

    private Integer uploaderid; // 发布者ID
    private String contactinfo; // 隐藏的交接联系方式
    private Integer auditstatus; // 0:待审核 1:审核通过/已上架 2:拒绝驳回

    private Integer isSameSex;
}
