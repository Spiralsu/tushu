package com.booksharing.book.mapper;

import com.booksharing.book.model.BookWish;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BookWishMapper {
    @Insert("INSERT INTO book_wish(user_id, user_name, book_name, wish_desc, state, create_time) " +
            "VALUES(#{userId}, #{userName}, #{bookName}, #{wishDesc}, 0, NOW())")
    int insertWish(BookWish wish);

    @Update("UPDATE book_wish SET state = 1, fulfill_user_id = #{fulfillUserId} WHERE wish_id = #{wishId}")
    int fulfillWish(@Param("wishId") Integer wishId, @Param("fulfillUserId") Integer fulfillUserId);

    @Select("SELECT wish_id as wishId, user_id as userId, user_name as userName, " +
            "book_name as bookName, wish_desc as wishDesc, state, " +
            "create_time as createTime, fulfill_user_id as fulfillUserId " +
            "FROM book_wish ORDER BY state ASC, create_time DESC")
    List<BookWish> selectAllWishes();

    @Delete("DELETE FROM book_wish WHERE wish_id = #{wishId}")
    int deleteWish(@Param("wishId") Integer wishId);

    @Select("SELECT wish_id as wishId, user_id as userId, user_name as userName, " +
            "book_name as bookName, wish_desc as wishDesc, state, " +
            "create_time as createTime, fulfill_user_id as fulfillUserId " +
            "FROM book_wish WHERE state = 0 AND book_name = #{bookName}")
    List<BookWish> selectUnfulfilledWishesByBookName(@Param("bookName") String bookName);

}
