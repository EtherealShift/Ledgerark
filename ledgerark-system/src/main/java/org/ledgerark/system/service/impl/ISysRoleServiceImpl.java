package org.ledgerark.system.service.impl;

import jakarta.annotation.Resource;
import org.ledgerark.common.entity.sys.SysRole;
import org.ledgerark.system.mapper.SysRoleMapper;
import org.ledgerark.system.service.ISysRoleService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ISysRoleServiceImpl implements ISysRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;


    @Override
    public List<SysRole> selectRoleList() {
        return sysRoleMapper.selectList(null);
    }
}
