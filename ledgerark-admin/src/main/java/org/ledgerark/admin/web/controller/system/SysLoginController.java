package org.ledgerark.admin.web.controller.system;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ledgerark.common.entity.Result;
import org.ledgerark.framework.web.service.SysLoginService;
import org.ledgerark.system.entity.dto.SysUserLoginCommandDTO;
import org.ledgerark.system.entity.dto.SysUserRegisterCommandDTO;
import org.ledgerark.system.entity.vo.SysUserLoginResponseVO;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/sys/login")
public class SysLoginController {

    @Resource
    private SysLoginService loginService;

    /**
     * 登录方法
     * @param command 登录参数
     * @return 结果
     */
    @PostMapping("/doLogin")
    public Result<SysUserLoginResponseVO> login(@RequestBody SysUserLoginCommandDTO command) {
        // 调用服务层登录方法，返回含 token 的响应
        SysUserLoginResponseVO response = loginService.login(command);
        log.info("登录成功{}", command.getUsername());
        return Result.success(response);
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
    @GetMapping("/logout")
    public Result<String> logout() {
        loginService.logout();
        log.info("登出成功");
        return Result.success("登出成功");
    }

}
