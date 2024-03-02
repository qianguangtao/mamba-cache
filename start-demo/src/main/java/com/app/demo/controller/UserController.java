package com.app.demo.controller;

import com.app.core.mvc.argument.StrListPathVariable;
import com.app.core.mvc.result.Result;
import com.app.demo.entity.User;
import com.app.demo.service.UserService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author qiangt
 * @date 2023/9/13
 * @apiNote
 */
@Api(tags = "用户管理")
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @ApiOperation(value = "用户列表")
    @ApiOperationSupport(order = 1)
    @PostMapping("/list")
    public Result<List<User>> list(@RequestBody User user) {
        return Result.success(service.list(user));
    }

    @ApiOperation(value = "新增用户")
    @ApiOperationSupport(order = 2)
    @PostMapping()
    public Result<User> save(@RequestBody User user) {
        return Result.success(service.insert(user));
    }

    @ApiOperation(value = "编辑用户")
    @ApiOperationSupport(order = 3)
    @PutMapping()
    public Result<User> edit(@RequestBody User user) {
        return Result.success(service.edit(user));
    }

    @ApiOperation(value = "删除用户")
    @ApiOperationSupport(order = 4)
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        service.delete(id);
        return Result.success();
    }

    @ApiOperation(value = "查询单个用户")
    @ApiOperationSupport(order = 5)
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable String id) {
        User user = service.getById(id);
        log.info("查询用户成功：{}", user);
        return Result.success(user);
    }
    @ApiOperation(value = "批量删除用户")
    @ApiOperationSupport(order = 6)
    @DeleteMapping("/batch/{ids}")
    public Result<Void> deleteBatch(@StrListPathVariable List<String> ids) {
        service.delete(ids);
        return Result.success();
    }


}
