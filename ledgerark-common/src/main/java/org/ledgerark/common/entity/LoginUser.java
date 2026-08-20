package org.ledgerark.common.entity;


import lombok.Builder;
import lombok.Data;



@Data
@Builder
public class LoginUser {

    // 用户名
    private String username;

    // 昵称
    private String nickname;

    // 邮箱
    private String email;

    // 角色类型
    private String roleType;

    // 工号
    private String employeeId;

}
