package org.ledgerark.system.service;

import org.ledgerark.common.entity.sys.SysRole;

import java.util.List;

/**
 * 角色业务层
 */
public interface ISysRoleService {

    /**
     * 获取角色信息
     */
    public List<SysRole> selectRoleList();

}
