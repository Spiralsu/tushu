package com.shanzhu.book.model;

import lombok.Data;

import java.util.Date;

/**
 * 借阅信息
 *
 * @author: ShanZhu
 * @date: 2023-12-31
 */
@Data
public class Borrow {

    /**
     * id
     */
    private Integer borrowid;

    /**
     * 借阅者用户id
     */
    private Integer userid;

    /**
     * 借阅者名称
     */
    private String username;

    /**
     * 借阅图书id
     */
    private Integer bookid;

    /**
     * 借阅图书名
     */
    private String bookname;

    /**
     * 借阅时间
     */
    private Date borrowtime;

    /**
     * 借阅时间str
     */
    private String borrowtimestr;

    /**
     * 规还时间
     */
    private Date returntime;

    /**
     * 规还时间str
     */
    private String returntimestr;

}
