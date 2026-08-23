package org.ledgerark.framework.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ledgerark.common.entity.LoginUser;
import org.ledgerark.common.enums.ResultCode;
import org.ledgerark.framework.util.UserUtils;
import org.ledgerark.common.enums.UserType;
import org.ledgerark.system.exception.UserException;
import org.ledgerark.system.entity.dto.SysUserLoginCommandDTO;
import org.ledgerark.system.entity.dto.SysUserRegisterCommandDTO;
import org.ledgerark.system.entity.vo.SysUserLoginResponseVO;
import org.ledgerark.system.service.ISysUserService;
import org.ledgerark.system.constant.UserConstant;
import org.ledgerark.system.entity.sys.SysUser;
import org.ledgerark.framework.web.service.SysLoginService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class SysLoginServiceImpl implements SysLoginService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private  ISysUserService sysUserService;


    @Override
    public SysUserLoginResponseVO login(SysUserLoginCommandDTO command) {

        try {
            // 登陆前校验
            loginPreCheck(command.getUsername(), command.getPassword());

            SysUser user = sysUserService.selectUserByUserName(command.getUsername());

            // 校验密码
            if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
                throw new UserException(ResultCode.USER_PASSWORD_ERROR);
            }

            // 登录并生成 token（先登录，token 才可获取）
            StpUtil.login(user.getId());
            String tokenValue = StpUtil.getTokenInfo().getTokenValue();

            // 构建用户详情
            SysUserLoginResponseVO.UserInfo userInfo = SysUserLoginResponseVO.UserInfo.builder()
                    .username(user.getUserName()).email(user.getEmail())
                    .nickname(user.getNickName()).genderDisplayName(user.getSexName())
                    .userTypeDisplayName(user.convertUserType())
                    .statusDisplayName(user.getStatusName()).build();

            // 构建 Session
            LoginUser loginUser = LoginUser.builder()
                    .username(user.getUserName())
                    .email(user.getEmail())
                    .nickname(user.getNickName())
                    .userType(user.getUserType())
                    .employeeId(user.getEmployeeId()).build();
            StpUtil.getSession().set(UserConstant.SESSION_USER_KEY, loginUser);

            // 组装登录响应：token + 用户名 + 用户详情
            return SysUserLoginResponseVO.builder()
                    .token(tokenValue)
                    .username(user.getUserName())
                    .userInfo(userInfo)
                    .build();
        } catch (Exception e) {
            log.error("用户登录异常：{}", e.getMessage());
            throw new UserException(ResultCode.USER_LOGIN_FAIL);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(SysUserRegisterCommandDTO command) {
        try {
            // 新增前校验（checkXxxUnique 返回 true 表示"未被占用"，故取反后抛"已存在"）
            if (!sysUserService.checkEmailUnique(command.getEmail())) {
                throw new UserException(ResultCode.USER_EMAIL_EXIST);
            }
            if (!sysUserService.checkUsernameUnique(command.getUsername())) {
                throw new UserException(ResultCode.USER_USERNAME_EXIST);
            }

            // 密码加密
            String encode = passwordEncoder.encode(command.getPassword());
            command.setPassword(encode);

            // 新增用户
            sysUserService.insertUserInfo(command);
            log.info("用户注册成功，用户名：{}", command.getUsername());
        } catch (Exception e) {
            log.error("用户注册异常：{}", e.getMessage());
            throw new UserException(ResultCode.USER_REGISTER_FAIL);
        }

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

    @Override
    public void resetPassword(Long userId) {
        try {
            // 校验当前用户是否管理员
            String currentUserType = UserUtils.getCurrentUserType();
            if (!UserType.ADMIN.getCode().equals(currentUserType)) {
                log.info("非管理员用户，无权限重置密码");
                throw new UserException(ResultCode.USER_NOT_ADMIN);
            }

            String newPassword = passwordEncoder.encode("123456");

            // 重置密码
            sysUserService.updateUserPasswordById(userId, newPassword);

            log.info("重置用户密码成功");
        } catch (Exception e) {
            log.error("重置用户密码异常：{}", e.getMessage());
            throw new UserException(ResultCode.USER_RESET_PASSWORD_FAIL);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        try {
            // 校验当前用户
            Long currentUserId = UserUtils.getCurrentUserId();
            if (!currentUserId.equals(userId)) {
                log.info("非当前登录用户，无权限修改密码");
                throw new UserException(ResultCode.NOT_CURRENT_USER);
            }


            // 校验参数
            if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword)) {
                log.info("密码为空");
                throw new UserException(ResultCode.PARAM_MISSING);
            }
            if (oldPassword.equals(newPassword)) {
                log.info("新密码与旧密码相同");
                throw new UserException(ResultCode.PARAM_INVALID);
            }
            // 修改密码
            String newPasswordEncode = passwordEncoder.encode(newPassword);
            sysUserService.updateUserPasswordById(userId, newPasswordEncode);
        } catch (Exception e) {
            log.error("修改密码异常：{}", e.getMessage());
            throw new UserException(ResultCode.USER_CHANGE_PASSWORD_FAIL);
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
