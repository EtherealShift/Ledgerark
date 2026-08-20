package org.ledgerark.admin.web.controller.system;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ledgerark.common.entity.Result;
import org.ledgerark.framework.web.service.SysLoginService;
import org.ledgerark.system.entity.dto.SysUserLoginCommandDTO;
import org.ledgerark.system.entity.dto.SysUserRegisterCommandDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/sys/user")
public class SysLoginController {

    @Resource
    private SysLoginService loginService;

    /**
     * 登录方法
     * @param command 登录参数
     * @return 结果
     */
    @PostMapping("/doLogin")
    public Result<String> login(@RequestBody SysUserLoginCommandDTO command) {
        // 调用服务层登录方法
        loginService.login(command);
        log.info("登录成功{}", command.getUsername());
        return Result.success("登录成功");
    }

    /**
     * 注册方法
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody SysUserRegisterCommandDTO command) {

        loginService.register(command);

        log.info("注册成功{}", command.getUsername());
        return Result.success("注册成功");
    }

    /**
     * 登出方法
     * @return 结果
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        loginService.logout();
        log.info("登出成功");
        return Result.success("登出成功");
    }

}
