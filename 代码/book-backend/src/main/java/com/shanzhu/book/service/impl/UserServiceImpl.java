package com.shanzhu.book.service.impl;

import com.shanzhu.book.mapper.UserMapper;
import com.shanzhu.book.model.User;
import com.shanzhu.book.service.UserService;
import com.shanzhu.book.utils.R;
import com.shanzhu.book.utils.TokenProcessor;
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
        // 使用学号(studentId)登录
        User dbUser = userMapper.selectByUserName(user.getStudentid());
        if (dbUser == null) return R.error("学号未注册");

        // 1. 校验密码
        if (!dbUser.getUserpassword().equals(user.getUserpassword())) {
            return R.error("密码错误");
        }

        // 2. 校验身份 (0:读者, 1:管理员)
        if (user.getIsadmin() != null && !user.getIsadmin().equals(dbUser.getIsadmin())) {
            String roleName = user.getIsadmin() == 1 ? "管理员" : "读者";
            return R.error("登录失败：该账号不是" + roleName + "身份");
        }

        // 3. 校验状态
        if (dbUser.getStatus() == 0) return R.error("账号审核中，请联系管理员");
        if (dbUser.getStatus() == 2) return R.error("账号被禁用");

        String token = TokenProcessor.getInstance().generateToken();
        saveUser(token, dbUser);

        return R.ok("登录成功").put("token", token).put("user", dbUser);
    }

    @Override
    public R userRegister(User user) {
        // 查重：检查学号
        if (userMapper.selectCountByUserName(user.getStudentid()) > 0) {
            return R.error("该学号已注册");
        }
        user.setStatus(0); // 默认待审核
        user.setIsadmin(0);
        int i = userMapper.insert(user);
        return i > 0 ? R.ok("注册成功，请等待审核") : R.error("注册失败");
    }

    // --- 兼容旧方法 ---
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
        user.setStatus(0);
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

    // 【核心修复】构造 Map 参数调用 userMapper
    @Override
    public List<User> queryUsers() {
        Map<String, Object> map = new HashMap<>();
        map.put("begin", 0);
        map.put("size", 1000);
        return userMapper.selectAllByLimit(map);
    }

    // 【核心修复】直接传递 Map 参数
    @Override
    public int getSearchCount(Map<String, Object> searchParam) {
        return userMapper.selectCount(searchParam);
    }

    // 【核心修复】直接传递 Map 参数
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
}