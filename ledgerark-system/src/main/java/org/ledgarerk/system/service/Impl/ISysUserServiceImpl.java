package org.ledgarerk.system.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.ledgarerk.system.mapper.SysUserMapper;
import org.ledgarerk.system.service.ISysUserService;
import org.ledgerark.common.entity.SysUser;
import org.ledgerark.common.enums.ResultCode;
import org.ledgerark.common.exception.user.UserException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class ISysUserServiceImpl implements ISysUserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
}
