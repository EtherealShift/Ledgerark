package org.ledgerark.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 角色
 */
@Getter
@AllArgsConstructor
public enum RoleCode {


    ADMIN("1", "超级管理员", "admin"),
    COMMON("2", "普通用户", "common");


    // 角色编码
    private final String code;

    // 角色名称
    private final String name;

    // 角色权限
    private final String description;


    /**
     * 根据角色编码获取角色
     * @param code
     * @return
     */
    public static RoleCode fromCode(String code) {
        for (RoleCode roleCode : RoleCode.values()) {
            if (roleCode.getCode().equals(code)) {
                return roleCode;
            }
        }
        throw new IllegalArgumentException("Invalid Role code: " + code);
    }




}
