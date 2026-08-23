package org.ledgerark.system.entity.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SysUserChangePasswordDTO {

    @NotBlank(message = "用户ID不能为空")
    private Long userId; // 用户ID

    @NotBlank(message = "旧密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6到20个字符之间")
    private String oldPassword; // 旧密码

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6到20个字符之间")
    private String newPassword; // 新密码

}
