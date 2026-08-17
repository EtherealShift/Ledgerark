package org.ledgerark.framework.web.service.impl;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.ledgarerk.system.entity.vo.SysUserLoginResponseVO;
import org.ledgarerk.system.service.ISysUserService;
import org.ledgerark.common.constant.UserConstant;
import org.ledgerark.common.entity.SysUser;
import org.ledgerark.framework.util.JwtTokenUtils;
import org.ledgerark.framework.web.service.SysLoginService;

import org.springframework.stereotype.Service;


@Service
public class SysLoginServiceImpl implements SysLoginService {

    @Resource
    private ISysUserService sysUserService;

    @Override
    public SysUserLoginResponseVO login(String username, String password) {

        // 登陆前校验
        loginPreCheck(username, password);

        SysUser user = sysUserService.selectUserByUserName(username);

        // 生成JWT token
        String token = JwtTokenUtils.generateToken(user.getId(), user.getUserName(), user.getRoleType());

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
            throw new RuntimeException("用户名或密码为空");
        }

        // 用户名长度限制
        if (username.length() < UserConstant.USERNAME_MIN_LENGTH || username.length() > UserConstant.USERNAME_MAX_LENGTH) {
            throw new RuntimeException("用户名长度限制");
        }

        // 密码长度限制
        if (password.length() < UserConstant.PASSWORD_MIN_LENGTH || password.length() > UserConstant.PASSWORD_MAX_LENGTH) {
            throw new RuntimeException("密码长度限制");
        }

    }
}
