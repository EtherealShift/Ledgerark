package org.ledgerark.framework.web.service.impl;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.ledgerark.common.enums.ResultCode;
import org.ledgerark.common.exception.user.UserException;
import org.ledgerark.system.entity.vo.SysUserLoginResponseVO;
import org.ledgerark.system.service.ISysUserService;
import org.ledgerark.common.constant.UserConstant;
import org.ledgerark.common.entity.SysUser;
import org.ledgerark.framework.util.JwtTokenUtils;
import org.ledgerark.framework.web.service.SysLoginService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class SysLoginServiceImpl implements SysLoginService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private JwtTokenUtils jwtTokenUtils;

    @Resource
    private  ISysUserService sysUserService;


    @Override
    public SysUserLoginResponseVO login(String username, String password) {

        // 登陆前校验
        loginPreCheck(username, password);

        SysUser user = sysUserService.selectUserByUserName(username);

        // 校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 生成JWT token
        String token = jwtTokenUtils.generateToken(user.getId(), user.getUserName(), user.getRoleType());

        return SysUserLoginResponseVO.builder()
                .token(token)
                .roleType(user.getRoleType())
                .detail(SysUserLoginResponseVO.entityToDetailResponse(user))
                .build();
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
