package com.shanzhu.book.mapper;

import com.shanzhu.book.model.User;
import java.util.List;
import java.util.Map;

public interface UserMapper {
    int deleteByPrimaryKey(Integer userid);

    int insert(User record);

    User selectByPrimaryKey(Integer userid);

    // 按用户名/学号查询
    User selectByUserName(String username);

    int selectCountByUserName(String username);

    int updateByPrimaryKeySelective(User record);

    // 统一改为 Map 传参，支持搜索
    List<User> selectAllByLimit(Map<String, Object> params);

    // 统一改为 Map 传参，支持搜索计数
    int selectCount(Map<String, Object> params);
}