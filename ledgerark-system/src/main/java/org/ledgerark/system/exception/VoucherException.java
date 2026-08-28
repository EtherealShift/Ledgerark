package org.ledgerark.system.exception;

import org.ledgerark.common.enums.ResultCode;
import org.ledgerark.common.exception.base.BaseException;

import java.io.Serial;

public class VoucherException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    public VoucherException(ResultCode resultCode) {
        super("voucher", resultCode);
    }

    public VoucherException(ResultCode resultCode, String message) {
        super(
                "voucher",
                resultCode.getCode(),
                message == null || message.isBlank() ? resultCode.getMsg() : message
        );
    }

    public VoucherException(ResultCode resultCode, Throwable cause) {
        super("voucher", resultCode.getCode(), resultCode.getMsg(), cause);
    }
}
