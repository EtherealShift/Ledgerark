package org.ledgerark.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.ledgerark.common.enums.CommonStatus;
import org.ledgerark.common.enums.RoleCode;


import java.io.Serial;
import java.util.Date;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user")
public class SysUser extends BaseEntity {
	@Serial
    private static final long serialVersionUID = 1L;

    // 工号
    private String employeeId;

    // 用户账号
    private String userName;

    // 用户昵称
    private String nickName;

    // 密码
    private String password;

    // 用户邮箱
    private String email;

    // 手机号码
    private String phoneNumber;

    // 用户性别 (0男 1女 2未知)
    private String sex;

    // 账号状态 (0正常 1停用)
    private String status;

    // 用户头像
    private String avatar;

    // 用户角色
    private String roleType;

    // 创建者
    private String createBy;

    // 创建时间
    private Date createTime;

    // 更新时间
    private Date updateTime;

    /**
     * 校验用户登陆状态
     */
    public boolean isActive() {
        return CommonStatus.NORMAL.getCode().equals(this.status);
    }

    /**
     * 转换用户角色
     */
    public String convertRoleType() {
        return roleType.replace(RoleCode.ADMIN.getCode(), RoleCode.ADMIN.getName())
                .replace(RoleCode.COMMON.getCode(), RoleCode.COMMON.getName());
    }


    /**
     * 显示用户状态
     */
    public String getStatusName() {
        return CommonStatus.fromCode(status).getName();
    }
}
