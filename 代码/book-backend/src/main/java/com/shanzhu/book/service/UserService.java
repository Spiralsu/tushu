package com.shanzhu.book.service;

import com.shanzhu.book.model.User;
import com.shanzhu.book.utils.R;

import java.util.List;
import java.util.Map;

public interface UserService {
    // 修改：返回 R 类型
    R userLogin(User user);

    // 修改：返回 R 类型
    R userRegister(User user);

    // --- 以下为兼容旧逻辑的方法 ---
    User login(User user); // 旧登录方法
    Integer register(String username, String password); // 旧注册方法

    void saveUser(String token, User user);
    User getUser(String token);
    void removeUser(String token);
    void setPassword(Integer id, String password);
    Integer getCount();
    List<User> queryUsers();
    int getSearchCount(Map<String, Object> searchParam);
    List<User> searchUsersByPage(Map<String, Object> params);
    Integer addUser(User user);
    Integer deleteUser(User user);
    Integer deleteUsers(List<User> users);
    Integer updateUser(User user);
}