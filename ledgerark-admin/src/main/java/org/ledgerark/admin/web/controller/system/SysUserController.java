package org.ledgerark.admin.web.controller.system;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ledgerark.common.entity.Result;
import org.ledgerark.common.entity.base.PageQuery;
import org.ledgerark.system.entity.sys.SysUser;
import org.ledgerark.system.entity.vo.SysPageResponseVO;
import org.ledgerark.system.entity.vo.SysUserResponseVO;
import org.ledgerark.system.service.ISysUserService;
import org.springframework.web.bind.annotation.*;

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
    public Result<List<SysUserResponseVO>> selectUserList() {
        return Result.success(sysUserService.selectAllUserList());
    }

    /**
     * 分页查询用户
     * @param pageQuery 分页查询参数（pageNum 默认 1，pageSize 默认 10，最大 100）
     * @return 分页结果（含总条数、总页数、当前页数据）
     */
    @GetMapping("/page")
    public Result<SysPageResponseVO> pageUserList(PageQuery pageQuery) {
        return Result.success(sysUserService.pageUserList(pageQuery));
    }

    /**
     * 根据用户 ID 删除用户
     * @param userId 用户 ID
     * @return 删除结果
     */
    @PostMapping("/delete")
    public Result<?> deleteUserByUserId(Long userId) {
        sysUserService.deleteUserByUserId(userId);
        return Result.success("用户删除成功");
    }

}
