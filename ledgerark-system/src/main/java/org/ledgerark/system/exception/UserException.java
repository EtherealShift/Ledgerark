package org.ledgerark.system.exception;

import org.ledgerark.common.enums.ResultCode;
import org.ledgerark.common.exception.base.BaseException;

import java.io.Serial;

public class UserException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;


    public UserException(String code, String message) {
        super("user", code, message);
    }

    public UserException(String code, String message, Throwable cause) {
        super("user", code, message, cause);
    }

    public UserException(ResultCode resultCode) {
        super("user", resultCode);
    }

}
