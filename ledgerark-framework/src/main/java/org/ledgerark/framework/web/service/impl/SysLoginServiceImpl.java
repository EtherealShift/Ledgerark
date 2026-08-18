package org.ledgerark.framework.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.ledgerark.common.enums.ResultCode;
import org.ledgerark.common.exception.user.UserException;
import org.ledgerark.system.entity.dto.SysUserLoginCommandDTO;
import org.ledgerark.system.entity.dto.SysUserRegisterCommandDTO;
import org.ledgerark.system.entity.vo.SysUserLoginResponseVO;
import org.ledgerark.system.service.ISysUserService;
import org.ledgerark.common.constant.UserConstant;
import org.ledgerark.common.entity.SysUser;
import org.ledgerark.framework.web.service.SysLoginService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class SysLoginServiceImpl implements SysLoginService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private  ISysUserService sysUserService;


    @Override
    public void login(SysUserLoginCommandDTO command) {

        // 登陆前校验
        loginPreCheck(command.getUsername(), command.getPassword());

        SysUser user = sysUserService.selectUserByUserName(command.getUsername());

        // 校验密码
        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new UserException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 构建用户信息
        SysUserLoginResponseVO userInfo = SysUserLoginResponseVO.builder()
                .username(user.getUserName()).email(user.getEmail())
                .nickname(user.getNickName()).genderDisplayName(user.getSexName())
                .userTypeDisplayName(user.convertRoleType())
                .statusDisplayName(user.getStatusName()).build();

        // 登录并生成token
        StpUtil.login(user.getId());

        // 将用户信息存储在Session中
        StpUtil.getSession().set("userInfo", userInfo);
    }

    @Override
    public void register(SysUserRegisterCommandDTO command) {



    }



    /**
     * 登陆前校验
     * @param username
     * @param password
     */
    public void loginPreCheck(String username, String password) {
        // 用户名或密码为空
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new UserException(ResultCode.USERNAME_OR_PASSWORD_EMPTY);
        }

        // 用户名长度限制
        if (username.length() < UserConstant.USERNAME_MIN_LENGTH || username.length() > UserConstant.USERNAME_MAX_LENGTH) {
            throw new UserException(ResultCode.USERNAME_LENGTH_ERROR);
        }

        // 密码长度限制
        if (password.length() < UserConstant.PASSWORD_MIN_LENGTH || password.length() > UserConstant.PASSWORD_MAX_LENGTH) {
            throw new UserException(ResultCode.PASSWORD_LENGTH_ERROR);
        }

    }
}
