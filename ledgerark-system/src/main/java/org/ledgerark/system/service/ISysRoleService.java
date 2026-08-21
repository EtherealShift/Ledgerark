package org.ledgerark.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.ledgerark.common.entity.base.PageQuery;
import org.ledgerark.system.entity.sys.SysRole;

import java.util.List;

/**
 * 角色业务层
 */
public interface ISysRoleService {

    /**
     * 分页查询角色信息
     * @param pageQuery 分页查询参数
     * @return 分页结果（含总条数、总页数）
     */
    Page<SysRole> pageRoleList(PageQuery pageQuery);

    /**
     * 获取全部角色信息
     */
    List<SysRole> selectAllRoleList();

}
