package org.ledgerark.framework.security;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.ledgerark.common.entity.Result;
import org.ledgerark.common.enums.ResultCode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class SecurityErrorResponseWriter {

    private SecurityErrorResponseWriter() {
    }

    public static void write(
            HttpServletResponse response,
            int httpStatus,
            ResultCode resultCode) throws IOException {
        if (response.isCommitted()) {
            return;
        }

        response.setStatus(httpStatus);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONUtil.toJsonStr(Result.failure(resultCode)));
    }
}
