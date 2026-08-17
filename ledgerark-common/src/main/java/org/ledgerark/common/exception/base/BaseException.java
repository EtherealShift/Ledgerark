package org.ledgerark.common.exception.base;


import lombok.Getter;
import org.ledgerark.common.enums.ResultCode;

import java.io.Serial;

/**
 * 基础异常
 */

@Getter
public class BaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    // 所属模块
    private final String module;

    // 错误码
    private final String code;

    public BaseException(String module, String code, String message) {
        super(message);
        this.module = module;
        this.code = code;
    }

    public BaseException(String module, String code, String message, Throwable cause) {
        super(message, cause);
        this.module = module;
        this.code = code;
    }

    public BaseException(String module, ResultCode resultCode) {
        super(resultCode.getMsg());
        this.module = module;
        this.code = resultCode.getCode();
    }

}
