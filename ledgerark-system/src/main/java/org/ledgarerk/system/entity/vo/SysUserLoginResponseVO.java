package org.ledgarerk.system.entity.vo;


import lombok.Builder;
import lombok.Data;
import org.ledgerark.common.entity.SysUser;


@Data
@Builder
public class SysUserLoginResponseVO {
    private String token;

    private String roleType;

    private DetailLoginResponseVO detail;


    @Builder
    @Data
    public static class DetailLoginResponseVO {
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


    public static DetailLoginResponseVO entityToDetailResponse(SysUser user) {
        return DetailLoginResponseVO.builder()
                .username(user.getUserName())
                .email(user.getEmail())
                .nickname(user.getNickName())
                .genderDisplayName(user.getSex())
                .userTypeDisplayName(user.convertRoleType())
                .statusDisplayName(user.getStatusName())
                .build();
    }
}
