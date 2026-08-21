package org.ledgerark.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.ledgerark.common.constant.CommonConstant;
import org.ledgerark.common.entity.PageParam;
import org.ledgerark.common.entity.base.PageQuery;
import org.ledgerark.system.entity.sys.SysRole;
import org.ledgerark.system.entity.sys.SysUser;
import org.ledgerark.system.entity.vo.SysPageResponseVO;
import org.ledgerark.system.mapper.SysRoleMapper;
import org.ledgerark.system.service.ISysRoleService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ISysRoleServiceImpl implements ISysRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;


    @Override
    public SysPageResponseVO pageRoleList(PageQuery pageQuery) {

        // 分页查询，按 ID 正序
        Page<SysRole> page = PageParam.of(pageQuery).toPage();

        Page<SysRole> sysRolePage = sysRoleMapper.selectPage(page, null);

        return SysPageResponseVO.builder()
                .records(sysRolePage.getRecords())
                .total(sysRolePage.getTotal())
                .size(sysRolePage.getSize())
                .current(sysRolePage.getCurrent()).build();
    }

    @Override
    public List<SysRole> selectAllRoleList() {
        return sysRoleMapper.selectList(null);
    }
}
