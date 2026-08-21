package org.ledgerark.system.entity.sys;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user_role")
public class SysUserRole {

    // ID（数据库自增）
    @TableId(type = IdType.AUTO)
    private Long id;

    // 用户 ID
    private Long userId;

    // 角色 ID
    private Long roleId;

    // 更新时间
    private Date updateTime;

    //更新者
    private String updateBy;

}
