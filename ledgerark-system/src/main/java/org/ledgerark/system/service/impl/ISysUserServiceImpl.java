package org.ledgerark.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.ledgerark.common.entity.PageParam;
import org.ledgerark.common.entity.base.PageQuery;
import org.ledgerark.system.entity.dto.SysUserRegisterCommandDTO;
import org.ledgerark.system.entity.vo.SysPageResponseVO;
import org.ledgerark.system.entity.vo.SysUserResponseVO;
import org.ledgerark.system.mapper.SysUserMapper;
import org.ledgerark.system.service.ISysUserService;
import org.ledgerark.system.entity.sys.SysUser;
import org.ledgerark.system.enums.CommonStatus;
import org.ledgerark.common.enums.ResultCode;
import org.ledgerark.system.enums.UserType;
import org.ledgerark.system.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ISysUserServiceImpl implements ISysUserService {

    @Resource
    private SysUserMapper userMapper;


    @Override
    public SysUser selectUserByUserName(String userName) {
        // 构建查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>();
        queryWrapper.eq(SysUser::getUserName, userName).or()
                .eq(SysUser::getNickName, userName).or()
                .eq(SysUser::getEmail, userName);
        SysUser user = userMapper.selectOne(queryWrapper);

        if (user == null){
            throw new UserException(ResultCode.USER_NOT_FOUND);
        }

        // 检查用户的状态
        if (!user.isActive()) {
            throw new UserException(ResultCode.USER_INVALID);
        }

        return user;
    }

    @Override
    public void insertUserInfo(SysUserRegisterCommandDTO userInfo) {

        // 创建用户对象
        SysUser user = new SysUser();
        user.setUserName(userInfo.getUsername());
        user.setNickName(userInfo.getNickname());
        user.setEmail(userInfo.getEmail());
        user.setPhoneNumber(userInfo.getPhoneNumber());
        user.setSex(userInfo.getSex());
        user.setPassword(userInfo.getPassword());

        // 设置默认状态与用户类型（工号保持为空，由管理员分配）
        user.setStatus(CommonStatus.NORMAL.getCode());
        user.setUserType(UserType.COMMON.getCode());

        // 插入用户数据
        userMapper.insert(user);

    }

    @Override
    public boolean checkEmailUnique(String email) {

        if (StringUtils.isBlank(email)) {
            throw  new UserException(ResultCode.PARAM_MISSING);
        }

        // 查询邮箱信息
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getEmail, email);

        // 判断邮箱是否唯一
        return !userMapper.exists(queryWrapper);
    }

    @Override
    public boolean checkUsernameUnique(String username) {

        if (StringUtils.isBlank(username)) {
            throw  new UserException(ResultCode.PARAM_MISSING);
        }

        // 查询信息
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUserName, username);

        // 判断用户名是否唯一
        return !userMapper.exists(queryWrapper);
    }

    @Override
    public List<SysUserResponseVO> selectAllUserList() {

        return userMapper.selectList(null).stream().map(vo -> new SysUserResponseVO(
                vo.getEmployeeId(), vo.getUserName(), vo.getNickName(), vo.getEmail(), vo.getPhoneNumber(),
                vo.getSexName(), vo.getStatusName(), vo.getAvatar(), vo.convertUserType()
        )).toList();
    }

    @Override
    public SysPageResponseVO pageUserList(PageQuery pageQuery) {


        // 分页查询，按 ID 正序
        Page<SysUser> page = PageParam.of(pageQuery).toPage();
        Page<SysUser> sysUserPage = userMapper.selectPage(page, null);

        return SysPageResponseVO.builder()
                .records(sysUserPage.getRecords())
                .total(sysUserPage.getTotal())
                .size(sysUserPage.getSize())
                .current(sysUserPage.getCurrent()).build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserByUserId(Long userId) {
        try {
            // 校验参数
            if (userId == null || userId < 1) {
                throw  new UserException(ResultCode.PARAM_MISSING);
            }
            // 删除用户信息，软删除
            userMapper.deleteById(userId);
        } catch (Exception e) {
            throw new UserException(ResultCode.USER_DELETE_FAILED);
        }
    }




}
