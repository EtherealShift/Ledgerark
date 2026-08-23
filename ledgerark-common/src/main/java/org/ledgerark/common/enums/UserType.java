package org.ledgerark.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 用户类型
 */
@Getter
@AllArgsConstructor
public enum UserType {


    ADMIN("1", "超级管理员", "admin"),
    COMMON("2", "普通用户", "common");


    // 用户类型编码
    private final String code;

    // 用户类型名称
    private final String name;

    // 用户类型描述
    private final String description;


    /**
     * 根据用户类型编码获取用户类型
     * @param code
     * @return
     */
    public static UserType fromCode(String code) {
        for (UserType userType : UserType.values()) {
            if (userType.getCode().equals(code)) {
                return userType;
            }
        }
        throw new IllegalArgumentException("Invalid UserType code: " + code);
    }


}
