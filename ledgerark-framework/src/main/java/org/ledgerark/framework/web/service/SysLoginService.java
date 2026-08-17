package org.ledgerark.framework.web.service;

import org.ledgarerk.system.entity.vo.SysUserLoginResponseVO;

public interface SysLoginService {


    /**
     *  登陆验证
     * @param username
     * @param password
     * @return
     */
    SysUserLoginResponseVO login(String username, String password);

}
