package com.booksharing.book.service.impl;

import com.booksharing.book.mapper.UserMapper;
import com.booksharing.book.model.User;
import com.booksharing.book.service.UserService;
import com.booksharing.book.utils.R;
import com.booksharing.book.utils.TokenProcessor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public R userLogin(User user) {
        User dbUser = userMapper.selectByUserName(user.getStudentid());
        if (dbUser == null) return R.error("学号不存在，请联系管理员录入账号");

        if (!dbUser.getUserpassword().equals(user.getUserpassword())) {
            return R.error("密码错误");
        }

        if (user.getIsadmin() != null && !user.getIsadmin().equals(dbUser.getIsadmin())) {
            String roleName = user.getIsadmin() == 1 ? "管理员" : "读者";
            return R.error("登录失败：该账号不是" + roleName + "身份");
        }

        if (dbUser.getStatus() == 0) return R.error("账号未启用，请联系管理员");
        if (dbUser.getStatus() == 2) return R.error("账号已被禁用");

        String token = TokenProcessor.getInstance().generateToken();
        saveUser(token, dbUser);

        return R.ok("登录成功").put("token", token).put("user", dbUser);
    }

    @Override
    public R userRegister(User user) {
        if (userMapper.selectCountByUserName(user.getStudentid()) > 0) {
            return R.error("该学号已录入");
        }
        user.setStatus(1);
        if (user.getUserpassword() == null || user.getUserpassword().isEmpty()) {
            user.setUserpassword("123456");
        }
        user.setIsadmin(0);
        int i = userMapper.insert(user);
        return i > 0 ? R.ok("录入成功") : R.error("录入失败");
    }

    // --- 兼容旧方法---
    @Override
    public User login(User user) {
        return userMapper.selectByUserName(user.getUsername());
    }

    @Override
    public Integer register(String username, String password) {
        User user = new User();
        user.setUsername("新用户");
        user.setStudentid(username);
        user.setUserpassword(password);
        user.setStatus(1);
        user.setIsadmin(0);
        return userMapper.insert(user);
    }

    @Override
    public void saveUser(String token, User user) { TokenProcessor.getInstance().saveUser(token, user); }
    @Override
    public User getUser(String token) { return TokenProcessor.getInstance().getUser(token); }
    @Override
    public void removeUser(String token) { TokenProcessor.getInstance().removeUser(token); }
    @Override
    public void setPassword(Integer id, String password) {
        User user = new User();
        user.setUserid(id);
        user.setUserpassword(password);
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public Integer getCount() {
        return userMapper.selectCount(new HashMap<>());
    }

    // 【核心修复】构造Map 参数调用 userMapper
    @Override
    public List<User> queryUsers() {
        Map<String, Object> map = new HashMap<>();
        map.put("begin", 0);
        map.put("size", 1000);
        return userMapper.selectAllByLimit(map);
    }

    // 【核心修复】直接传递Map 参数
    @Override
    public int getSearchCount(Map<String, Object> searchParam) {
        return userMapper.selectCount(searchParam);
    }

    // 【核心修复】直接传递Map 参数
    @Override
    public List<User> searchUsersByPage(Map<String, Object> params) {
        return userMapper.selectAllByLimit(params);
    }

    @Override
    public Integer addUser(User user) { return userMapper.insert(user); }
    @Override
    public Integer deleteUser(User user) { return userMapper.deleteByPrimaryKey(user.getUserid()); }
    @Override
    public Integer deleteUsers(List<User> users) {
        int count = 0;
        for(User u : users) {
            count += userMapper.deleteByPrimaryKey(u.getUserid());
        }
        return count;
    }
    @Override
    public Integer updateUser(User user) { return userMapper.updateByPrimaryKeySelective(user); }

    @Override
    public R updatePassword(Integer userid, String oldPassword, String newPassword) {
        User user = userMapper.selectByPrimaryKey(userid);
        if (user == null) return R.error("用户不存在");
        if (!user.getUserpassword().equals(oldPassword)) return R.error("原密码错误");
        setPassword(userid, newPassword);
        return R.ok("密码修改成功");
    }
}
