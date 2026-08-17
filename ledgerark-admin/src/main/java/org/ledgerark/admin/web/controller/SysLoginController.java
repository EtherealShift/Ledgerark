package org.ledgerark.admin.web.controller;

import jakarta.annotation.Resource;
import org.ledgarerk.system.entity.vo.SysUserLoginResponseVO;
import org.ledgerark.common.entity.Result;
import org.ledgerark.framework.web.service.SysLoginService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/login")
public class SysLoginController {


    @Resource
    private SysLoginService loginService;

    /**
     * 登录方法
     * @param username 用户名
     * @param password 密码
     * @return 结果
     */
    @RequestMapping("/login")
    public Result<SysUserLoginResponseVO> login(String username, String password) {

        SysUserLoginResponseVO token = loginService.login(username, password);

        return Result.success(token);
    }


}
