package org.ledgerark.system.entity.vo;


import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class SysUserLoginResponseVO {

    // 用户名
    private String username;

    // 邮箱
    private String email;

    // 昵称
    private String nickname;

    // 性别显示名称
    private String genderDisplayName;

    // 用户类型显示名称
    private String userTypeDisplayName;

    // 状态显示名称
    private String statusDisplayName;

}
