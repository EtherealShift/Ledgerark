package org.ledgerark.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SexStatus {
    MALE("0", "男"),
    FEMALE("1", "女"),
    UNKNOWN("2", "未知");

    private final String code;

    private final String name;


    /**
     * 根据编码获取枚举
     * @param code
     * @return
     */
    public static SexStatus fromCode(String code) {
        for (SexStatus status : SexStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid SexStatus code: " + code);
    }

}
