package org.ledgerark.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.ledgerark.common.entity.sys.SysRole;

import java.util.List;

/**
 * 角色业务层
 */
public interface ISysRoleService {

    /**
     * 分页查询角色信息
     * @param pageNum 页码，从 1 开始
     * @param pageSize 每页条数
     * @return 分页结果（含总条数、总页数）
     */
    Page<SysRole> pageRoleList(Integer pageNum, Integer pageSize);

    /**
     * 获取全部角色信息
     */
    List<SysRole> selectAllRoleList();

}
