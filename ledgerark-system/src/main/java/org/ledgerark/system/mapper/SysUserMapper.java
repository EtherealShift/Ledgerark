package org.ledgerark.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ledgerark.system.entity.sys.SysUser;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
