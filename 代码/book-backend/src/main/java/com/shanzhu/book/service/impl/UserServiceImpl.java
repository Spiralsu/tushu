package com.shanzhu.book.service.impl;

import com.shanzhu.book.mapper.UserMapper;
import com.shanzhu.book.model.User;
import com.shanzhu.book.service.UserService;
import com.shanzhu.book.utils.R;
import com.shanzhu.book.utils.TokenProcessor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

        if (!dbUser.getUserpassword().equals(user.getUserpassword())) {
            return R.error("密码错误");
        }

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
        // 兼容旧Controller调用，但尽量改用userLogin
        return userMapper.selectByUserName(user.getUsername());
    }

    @Override
    public Integer register(String username, String password) {
        // 核心修复：旧接口调用时，将 username 赋值给 studentId
        User user = new User();
        user.setUsername("新用户"); // 默认昵称
        user.setStudentid(username); // 将前端输入的账号作为学号
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
    public Integer getCount() { return userMapper.selectCount(null); }
    @Override
    public List<User> queryUsers() { return userMapper.selectAllByLimit(0, 1000, null); }
    @Override
    public int getSearchCount(Map<String, Object> searchParam) { return userMapper.selectCount(null); }
    @Override
    public List<User> searchUsersByPage(Map<String, Object> params) { return userMapper.selectAllByLimit(0, 10, null); }
    @Override
    public Integer addUser(User user) { return userMapper.insert(user); }
    @Override
    public Integer deleteUser(User user) { return userMapper.deleteByPrimaryKey(user.getUserid()); }
    @Override
    public Integer deleteUsers(List<User> users) { return 0; }
    @Override
    public Integer updateUser(User user) { return userMapper.updateByPrimaryKeySelective(user); }
}