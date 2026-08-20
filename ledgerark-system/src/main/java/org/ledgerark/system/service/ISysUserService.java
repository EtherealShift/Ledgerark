package org.ledgerark.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.ledgerark.common.entity.base.PageQuery;
import org.ledgerark.common.entity.sys.SysUser;
import org.ledgerark.system.entity.dto.SysUserRegisterCommandDTO;

import java.util.List;


/**
 * 用户业务层
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


    /**
     * 校验邮箱信息
     */
    public boolean checkEmailUnique(String email);


    /**
     * 校验用户名信息
     */
    public boolean checkUsernameUnique(String username);

    /**
     * 获取用户信息列表
     */
    List<SysUser> selectAllUserList();

    /**
     * 分页查询用户信息
     * @param pageQuery 分页查询参数
     * @return 分页结果（含总条数、总页数）
     */
    Page<SysUser> pageUserList(PageQuery pageQuery);

}
