package org.ledgerark.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    public Page<SysRole> pageRoleList(Integer pageNum, Integer pageSize) {
        // 参数兑底：页码/页大小为空或不合法时使用默认值，页大小限制最大 100，防止恶意大分页拖垮数据库
        int current = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        int size = (pageSize == null || pageSize < 1) ? 10 : Math.min(pageSize, 100);

        // 分页查询，按 ID 正序
        Page<SysRole> page = new Page<>(current, size);
        return sysRoleMapper.selectPage(page, null);
    }

    @Override
    public List<SysRole> selectAllRoleList() {
        return sysRoleMapper.selectList(null);
    }
}
