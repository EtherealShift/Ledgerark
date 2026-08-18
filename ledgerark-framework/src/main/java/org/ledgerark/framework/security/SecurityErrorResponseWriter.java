//package org.ledgerark.framework.security;
//
//import cn.hutool.json.JSONUtil;
//import jakarta.servlet.http.HttpServletResponse;
//import org.ledgerark.common.entity.Result;
//import org.ledgerark.common.enums.ResultCode;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//
///**
// * Security 错误响应写出工具类
// * <p>
// * 在 Spring Security 认证/授权失败时，向客户端直接写出统一的 JSON 错误体（Result 结构），
// * 供 SecurityConfig 的 401/403 处理器与 JwtAuthenticationFilter 的认证拒绝逻辑复用，
// * 保证安全层错误与业务接口返回格式一致，前端只需一套解析逻辑。
// */
//public final class SecurityErrorResponseWriter {
//
//    /** 工具类禁止实例化 */
//    private SecurityErrorResponseWriter() {
//    }
//
//    /**
//     * 向响应中写出统一 JSON 错误体。
//     * <p>
//     * 先检查响应是否已提交，避免过滤器链中多方写响应导致 IllegalStateException；
//     * 再设置 HTTP 状态码与 UTF-8 编码，最后序列化写出 Result.failure 结果。
//     *
//     * @param response   Servlet 响应对象
//     * @param httpStatus HTTP 状态码（如 401、403）
//     * @param resultCode 业务错误码枚举，携带 code 与 message
//     * @throws IOException 写出响应流时发生 I/O 错误
//     */
//    public static void write(
//            HttpServletResponse response,
//            int httpStatus,
//            ResultCode resultCode) throws IOException {
//        // 响应已提交（已开始写出到客户端）则不再重复写，防止 IllegalStateException
//        if (response.isCommitted()) {
//            return;
//        }
//
//        // 设置 HTTP 状态码（401 未认证 / 403 无权限等）
//        response.setStatus(httpStatus);
//        // 强制 UTF-8 编码，防止中文错误信息乱码
//        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
//        response.setContentType("application/json;charset=UTF-8");
//        // 序列化为与业务接口一致的 Result JSON 结构写出
//        response.getWriter().write(JSONUtil.toJsonStr(Result.failure(resultCode)));
//    }
//}
