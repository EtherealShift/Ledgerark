package org.ledgerark.common.entity.sys;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.ledgerark.common.entity.base.BaseEntity;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_role")
public class SysRole extends BaseEntity
{

	@Serial
    private static final long serialVersionUID = 1L;

    // 角色编码
    private Long roleCode;

    // 角色名称
    private String roleName;

    // 角色权限
    private String roleKey;

    // 数据权限范围
    private String dataScope;

    // 角色状态 (0正常 1停用)
    private String status;


}
