package org.ledgarerk.system.service;

import org.ledgerark.common.entity.SysUser;

public interface ISysUserService {


//    public SysUserLoginResponseVO login(SysUserLoginCommandDTO commandDTO);

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    public SysUser selectUserByUserName(String userName);


}
