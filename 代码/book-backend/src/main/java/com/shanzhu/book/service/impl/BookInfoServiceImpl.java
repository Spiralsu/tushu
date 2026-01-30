package com.shanzhu.book.service.impl;

import com.shanzhu.book.mapper.BookInfoMapper;
import com.shanzhu.book.model.BookInfo;
import com.shanzhu.book.service.BookInfoService;
import com.shanzhu.book.utils.PageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class BookInfoServiceImpl implements BookInfoService {

    @Resource
    private BookInfoMapper bookInfoMapper;

    @Override
    public Integer getCount() {
        return bookInfoMapper.selectCount();
    }

    // Dashboard 图表依赖此方法
    @Override
    public List<BookInfo> queryBookInfos() {
        return bookInfoMapper.selectAll();
    }

    @Override
    public Integer getSearchCount(Map<String, Object> params) {
        return bookInfoMapper.selectCountBySearch(params);
    }

    @Override
    public List<BookInfo> searchBookInfosByPage(Map<String, Object> params) {
        return bookInfoMapper.selectBySearch(params);
    }

    @Override
    public Integer addBookInfo(BookInfo bookInfo) {
        // 自动设置库存
        if (bookInfo.getBookcount() != null && bookInfo.getInventory() == null) {
            bookInfo.setInventory(bookInfo.getBookcount());
        }
        return bookInfoMapper.insert(bookInfo);
    }

    @Override
    public Integer deleteBookInfo(BookInfo bookInfo) {
        return bookInfoMapper.deleteByPrimaryKey(bookInfo.getBookid());
    }

    @Override
    public Integer deleteBookInfos(List<BookInfo> bookInfos) {
        int count = 0;
        for (BookInfo bookInfo : bookInfos) {
            count += bookInfoMapper.deleteByPrimaryKey(bookInfo.getBookid());
        }
        return count;
    }

    @Override
    public Integer updateBookInfo(BookInfo bookInfo) {
        return bookInfoMapper.updateByPrimaryKeySelective(bookInfo);
    }

    // 【核心修复】添加缺失的方法实现
    @Override
    public BookInfo queryBookInfoById(Integer id) {
        return bookInfoMapper.selectByPrimaryKey(id);
    }
}