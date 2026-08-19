package org.ledgerark.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.ledgerark.system.entity.dto.SysUserRegisterCommandDTO;
import org.ledgerark.system.mapper.SysUserMapper;
import org.ledgerark.system.service.ISysUserService;
import org.ledgerark.common.entity.SysUser;
import org.ledgerark.common.enums.ResultCode;
import org.ledgerark.common.exception.user.UserException;
import org.springframework.stereotype.Service;


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

        // 插入用户数据
        userMapper.insert(user);

    }

}
