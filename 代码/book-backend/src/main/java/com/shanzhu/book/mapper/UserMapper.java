package com.shanzhu.book.mapper;

import com.shanzhu.book.model.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface UserMapper {
    int deleteByPrimaryKey(Integer userid);
    int insert(User record);
    int insertSelective(User record);
    User selectByPrimaryKey(Integer userid);
    int updateByPrimaryKeySelective(User record);
    int updateByPrimaryKey(User record);

    // 修改：根据学号查询
    User selectByUserName(@Param("username") String username);

    // 修改：统计学号数量
    int selectCountByUserName(@Param("username") String username);

    List<User> selectAllByLimit(@Param("begin") Integer begin, @Param("size") Integer size, @Param("user") User user);
    Integer selectCount(@Param("user") User user);
}