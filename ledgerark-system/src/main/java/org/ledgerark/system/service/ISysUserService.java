package org.ledgerark.system.service;

import org.ledgerark.common.entity.SysUser;
import org.ledgerark.system.entity.dto.SysUserRegisterCommandDTO;


/**
 * 用户服务类
 */
public interface ISysUserService {

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    public SysUser selectUserByUserName(String userName);

    /**
     * 注册用户信息
     */
    public void insertUserInfo(SysUserRegisterCommandDTO userInfo);


}
