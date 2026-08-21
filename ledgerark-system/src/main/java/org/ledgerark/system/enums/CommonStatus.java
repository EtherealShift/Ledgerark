package org.ledgerark.system.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 通用状态管理
 */
@Getter
@AllArgsConstructor
public enum CommonStatus {

    NORMAL("0", "正常"),
    DISABLE("1", "停用");

    private final String code;

    private final String name;


    public static CommonStatus fromCode(String code) {
        for (CommonStatus status : CommonStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid Status code: " + code);
    }

}
