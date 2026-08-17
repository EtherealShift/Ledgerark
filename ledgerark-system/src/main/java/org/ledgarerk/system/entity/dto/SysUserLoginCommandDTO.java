package org.ledgarerk.system.entity.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class SysUserLoginCommandDTO {

    @NotBlank(message = "用户名或邮箱不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(max = 50, min = 6, message = "密码长度必须介于 6 ~ 50 个字符之间")
    private String password;

}
