package com.shanzhu.book.web;

import com.shanzhu.book.model.User;
import com.shanzhu.book.service.UserService;
import com.shanzhu.book.utils.PageUtils;
import com.shanzhu.book.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Resource
    private UserService userService;

    // 新登录接口
    @PostMapping(value = "/login")
    public R login(@RequestBody User user) {
        // 兼容处理：前端若传username，赋值给studentid
        if(user.getStudentid() == null) user.setStudentid(user.getUsername());
        return userService.userLogin(user);
    }

    // 新注册接口
    @PostMapping(value = "/registerUser")
    public R registerUser(@RequestBody User user) {
        return userService.userRegister(user);
    }

    // 旧注册接口 (保留以兼容前端)
    @RequestMapping(value = "/register")
    public Integer register(String username, String password) {
        // 调用 Service 修复后的逻辑
        return userService.register(username, password);
    }

    @RequestMapping(value = "/info")
    public R info(String token) {
        User user = userService.getUser(token);
        return user != null ? R.ok().put("data", user) : R.error("获取失败，请重新登录");
    }

    @RequestMapping(value = "/logout")
    public R logout(String token) {
        userService.removeUser(token);
        return R.ok("退出成功");
    }

    // 管理员获取用户列表
    @GetMapping(value = "/queryUsersByPage")
    public Map<String, Object> queryUsersByPage(@RequestParam Map<String, Object> params) {
        PageUtils.parsePageParams(params);
        // 这里只是为了让管理员页面能跑通，实际可以优化
        return R.getListResultMap(0, "success", 0, userService.queryUsers());
    }

    @RequestMapping(value = "/updateUser")
    public Integer updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping(value = "/deleteUser")
    public Integer deleteUser(@RequestBody User user) {
        return userService.deleteUser(user);
    }
}