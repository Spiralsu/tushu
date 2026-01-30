package com.shanzhu.book.web;

import com.shanzhu.book.model.User;
import com.shanzhu.book.service.UserService;
import com.shanzhu.book.utils.PageUtils;
import com.shanzhu.book.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Resource
    private UserService userService;

    // 登录接口
    @PostMapping(value = "/login")
    public R login(@RequestBody User user) {
        // 兼容处理：前端若传username，赋值给studentid
        if(user.getStudentid() == null) user.setStudentid(user.getUsername());
        return userService.userLogin(user);
    }

    // 注册接口 (新)
    @PostMapping(value = "/registerUser")
    public R registerUser(@RequestBody User user) {
        return userService.userRegister(user);
    }

    // 注册接口 (旧 - 兼容)
    @RequestMapping(value = "/register")
    public Integer register(String username, String password) {
        return userService.register(username, password);
    }

    // 获取用户信息
    @RequestMapping(value = "/info")
    public R info(String token) {
        User user = userService.getUser(token);
        return user != null ? R.ok().put("data", user) : R.error("获取失败，请重新登录");
    }

    // 退出登录
    @RequestMapping(value = "/logout")
    public R logout(String token) {
        userService.removeUser(token);
        return R.ok("退出成功");
    }

    // 【核心修复】获取用户总数，解决首页 404 错误
    @GetMapping(value = "/getCount")
    public Integer getCount() {
        return userService.getCount();
    }

    // 【优化】分页查询用户 (对接 searchUsersByPage)
    @GetMapping(value = "/queryUsersByPage")
    public Map<String, Object> queryUsersByPage(@RequestParam Map<String, Object> params) {
        PageUtils.parsePageParams(params);
        // 获取搜索总数
        int count = userService.getSearchCount(params);
        // 获取分页列表
        List<User> list = userService.searchUsersByPage(params);
        return R.getListResultMap(0, "success", count, list);
    }

    // 【新增】管理员添加用户
    @PostMapping(value = "/addUser")
    public Integer addUser(@RequestBody User user) {
        // 默认设置
        if(user.getStatus() == null) user.setStatus(1); // 正常状态
        if(user.getIsadmin() == null) user.setIsadmin(0); // 默认读者
        return userService.addUser(user);
    }

    // 更新用户
    @RequestMapping(value = "/updateUser")
    public Integer updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    // 删除单个用户
    @DeleteMapping(value = "/deleteUser")
    public Integer deleteUser(@RequestBody User user) {
        return userService.deleteUser(user);
    }

    // 【新增】批量删除用户
    @DeleteMapping(value = "/deleteUsers")
    public Integer deleteUsers(@RequestBody List<User> users) {
        return userService.deleteUsers(users);
    }
}