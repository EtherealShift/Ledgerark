package org.ledgerark.admin.web.controller.system;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ledgerark.common.entity.Result;
import org.ledgerark.common.entity.sys.SysRole;
import org.ledgerark.system.service.ISysRoleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sys/role")
public class SysRoleController {

    @Resource
    private ISysRoleService sysRoleService;


    /**
     * 查询全部角色
     * @return 角色列表
     */
    @GetMapping("/list")
    public Result<List<SysRole>> getAllRoleList() {
        return Result.success(sysRoleService.selectAllRoleList());
    }

    /**
     * 分页查询角色
     * @param pageNum 页码，从 1 开始，默认 1
     * @param pageSize 每页条数，默认 10，最大 100
     * @return 分页结果（含总条数、总页数、当前页数据）
     */
    @GetMapping("/page")
    public Result<Page<SysRole>> pageRoleList(@RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(sysRoleService.pageRoleList(pageNum, pageSize));
    }

}
