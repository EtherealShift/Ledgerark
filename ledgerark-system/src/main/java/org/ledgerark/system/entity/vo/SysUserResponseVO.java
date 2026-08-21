package org.ledgerark.system.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUserResponseVO {

    // 工号
    private String employeeId;

    // 用户账号
    private String userName;

    // 用户昵称
    private String nickName;

    // 用户邮箱
    private String email;

    // 手机号码
    private String phoneNumber;

    // 用户性别 (0男 1女 2未知)
    private String genderDisplayName;

    // 账号状态 (0正常 1停用)
    private String statusDisplayName;

    // 用户头像
    private String avatar;

    // 用户类型 (1超级管理员 2普通用户)
    private String userTypeDisplayName;

}
