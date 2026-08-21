package org.ledgerark.common.entity.sys;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.ledgerark.common.entity.base.BaseEntity;
import org.ledgerark.common.enums.CommonStatus;
import org.ledgerark.common.enums.UserType;
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

    // 密码（敏感字段，序列化时不返回前端）
    @JsonIgnore
    private String password;

    // 用户邮箱
    private String email;

    // 手机号码
    private String phoneNumber;

    // 用户性别 (0男 1女 2未知)
    private String sex;

    // 账号状态 (0正常 1停用)
    private String status;

    // 删除标志
    // 逻辑删除：0-正常 1-已删除
    @TableLogic
    private Integer delFlag;

    // 用户头像
    private String avatar;

    // 用户类型 (1超级管理员 2普通用户)
    private String userType;


    /**
     * 校验用户登陆状态
     */
    public boolean isActive() {
        return CommonStatus.NORMAL.getCode().equals(this.status);
    }

    /**
     * 转换用户类型
     */
    public String convertUserType() {
        return UserType.fromCode(userType).getName();
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
