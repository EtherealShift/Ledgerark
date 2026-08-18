package org.ledgerark.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.ledgerark.common.enums.CommonStatus;
import org.ledgerark.common.enums.RoleCode;
import org.ledgerark.common.enums.SexStatus;


import java.io.Serial;


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
        return RoleCode.fromCode(roleType).getName();
    }


    /**
     * 显示用户状态
     */
    public String getStatusName() {
        return CommonStatus.fromCode(status).getName();
    }


    /**
     * 显示用户性别
     */
    public String getSexName() {
        return SexStatus.fromCode(sex).getName();
    }

}
