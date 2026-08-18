package org.ledgerark.framework.web.service;

import org.ledgerark.system.entity.dto.SysUserLoginCommandDTO;
import org.ledgerark.system.entity.dto.SysUserRegisterCommandDTO;

public interface SysLoginService {


    /**
     *  登陆验证
     * @param username
     * @param password
     * @return
     */
    void login(SysUserLoginCommandDTO command);

    void register(SysUserRegisterCommandDTO command);

}
