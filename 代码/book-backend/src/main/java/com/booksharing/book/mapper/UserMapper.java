package com.booksharing.book.mapper;

import com.booksharing.book.model.User;
import java.util.List;
import java.util.Map;

public interface UserMapper {
    int deleteByPrimaryKey(Integer userid);

    int insert(User record);

    User selectByPrimaryKey(Integer userid);

    User selectByUserName(String username);

    int selectCountByUserName(String username);

    int updateByPrimaryKeySelective(User record);

    List<User> selectAllByLimit(Map<String, Object> params);

    int selectCount(Map<String, Object> params);
}
