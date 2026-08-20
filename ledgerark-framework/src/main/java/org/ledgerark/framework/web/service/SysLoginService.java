package org.ledgerark.framework.web.service;

import org.ledgerark.system.entity.dto.SysUserLoginCommandDTO;
import org.ledgerark.system.entity.dto.SysUserRegisterCommandDTO;

public interface SysLoginService {

    /**
     * 登陆
     * @param command
     */
    void login(SysUserLoginCommandDTO command);


    /**
     * 注册
     * @param command
     */
    void register(SysUserRegisterCommandDTO command);


    /**
     * 注销
     */
    void logout();


}
