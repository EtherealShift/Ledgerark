package org.ledgerark.admin.web.controller.system;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ledgerark.common.entity.Result;
import org.ledgerark.common.entity.base.PageQuery;
import org.ledgerark.common.entity.sys.SysUser;
import org.ledgerark.system.service.ISysUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private ISysUserService sysUserService;


    /**
     * 查询全部用户
     * @return 用户列表
     */
    @GetMapping("/list")
    public Result<List<SysUser>> selectUserList() {
        return Result.success(sysUserService.selectAllUserList());
    }

    /**
     * 分页查询用户
     * @param pageQuery 分页查询参数（pageNum 默认 1，pageSize 默认 10，最大 100）
     * @return 分页结果（含总条数、总页数、当前页数据）
     */
    @GetMapping("/page")
    public Result<Page<SysUser>> pageUserList(PageQuery pageQuery) {
        return Result.success(sysUserService.pageUserList(pageQuery));
    }

}
