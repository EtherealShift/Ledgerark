package org.ledgerark.system.entity.vo;


import lombok.Builder;
import lombok.Data;
import org.ledgerark.common.entity.SysUser;


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

//    private String token;
//
//    private String roleType;
//
//    private DetailLoginResponseVO detail;
//
//
//    @Builder
//    @Data
//    public static class DetailLoginResponseVO {
//        // 用户名
//        private String username;
//
//        // 邮箱
//        private String email;
//
//        // 昵称
//        private String nickname;
//
//        // 性别显示名称
//        private String genderDisplayName;
//
//        // 用户类型显示名称
//        private String userTypeDisplayName;
//
//        // 状态显示名称
//        private String statusDisplayName;
//    }




//    public static DetailLoginResponseVO entityToDetailResponse(SysUser user) {
//        return DetailLoginResponseVO.builder()
//                // 用户名
//                .username(user.getUserName())
//                // 邮箱
//                .email(user.getEmail())
//                // 昵称
//                .nickname(user.getNickName())
//                // 性别显示名称
//                .genderDisplayName(user.getSexName())
//                // 用户类型显示名称
//                .userTypeDisplayName(user.convertRoleType())
//                // 状态显示名称
//                .statusDisplayName(user.getStatusName())
//                .build();
//    }
}
