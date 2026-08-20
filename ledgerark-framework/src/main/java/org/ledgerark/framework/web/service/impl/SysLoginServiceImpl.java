package org.ledgerark.framework.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ledgerark.common.entity.LoginUser;
import org.ledgerark.common.enums.ResultCode;
import org.ledgerark.common.exception.user.UserException;
import org.ledgerark.system.entity.dto.SysUserLoginCommandDTO;
import org.ledgerark.system.entity.dto.SysUserRegisterCommandDTO;
import org.ledgerark.system.entity.vo.SysUserLoginResponseVO;
import org.ledgerark.system.service.ISysUserService;
import org.ledgerark.common.constant.UserConstant;
import org.ledgerark.common.entity.sys.SysUser;
import org.ledgerark.framework.web.service.SysLoginService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
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
                .userTypeDisplayName(user.convertUserType())
                .statusDisplayName(user.getStatusName()).build();

        // 构建Session
        LoginUser loginUser = LoginUser.builder()
                .username(user.getUserName())
                .email(user.getEmail())
                .nickname(user.getNickName())
                .userType(user.getUserType())
                .employeeId(user.getEmployeeId()).build();

        // 登录并生成token
        StpUtil.login(user.getId());

        // 将用户信息存储在Session中
        StpUtil.getSession().set(UserConstant.SESSION_USER_KEY, loginUser);

    }

    @Override
    public void register(SysUserRegisterCommandDTO command) {

        // 新增前校验
        if (!sysUserService.checkEmailUnique(command.getEmail())) {
            throw new UserException(ResultCode.USER_EMAIL_EXIST);
        }
        if  (!sysUserService.checkUsernameUnique(command.getUsername())) {
            throw new UserException(ResultCode.USER_USERNAME_EXIST);
        }

        // 密码加密
        String encode = passwordEncoder.encode(command.getPassword());
        command.setPassword(encode);

        // 新增用户
        sysUserService.insertUserInfo(command);
        log.info("用户注册成功，用户名：{}", command.getUsername());

    }

    @Override
    public void logout() {
        try {
            // 获取token
            String token = StpUtil.getTokenValue();
            log.info("退出登录");
            StpUtil.logout(token);
        } catch (Exception e) {
            log.error("退出登录异常：{}", e.getMessage());
        }
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
