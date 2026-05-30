package com.booksharing.book.mapper;

import com.booksharing.book.model.BookInfo;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface BookInfoMapper {
    int deleteByPrimaryKey(Integer bookid);

    int insert(BookInfo record);

    BookInfo selectByPrimaryKey(Integer bookid);

    List<BookInfo> selectAll();

    int updateByPrimaryKeySelective(BookInfo record);

    List<BookInfo> selectBySearch(Map<String, Object> params);

    List<BookInfo> selectAllByLimit(@Param("begin") int begin, @Param("size") int size);

    int selectCount();

    int selectCountBySearch(Map<String, Object> params);

    int selectCountByType(Map<String, Object> map);
}
