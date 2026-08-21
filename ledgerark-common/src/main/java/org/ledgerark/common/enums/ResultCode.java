package org.ledgerark.common.enums;

import lombok.Getter;

/**
 * 统一响应错误码
 * <p>
 * 参照《阿里巴巴 Java 开发手册》错误码规范：
 * 错误码为 5 位字符串，首位标识错误来源（A-用户端，B-当前系统，C-第三方服务），
 * 后四位为功能模块内的分段编号，00000 表示操作成功。
 * 业务错误码不与 HTTP 状态码混用，HTTP 状态码仅由传输层使用。
 */
@Getter
public enum ResultCode {

    // ========== 通用 ==========
    SUCCESS("00000", "操作成功"),
    SYSTEM_ERROR("B0001", "系统执行出错"),

    // ========== 参数相关 A020x ==========
    PARAM_ERROR("A0200", "请求参数错误"),
    PARAM_MISSING("A0201", "缺少必要参数"),
    PARAM_INVALID("A0202", "参数格式不正确"),

    // ========== 用户相关 A01xx ==========
    USER_NOT_FOUND("A0112", "用户不存在"),
    USER_USERNAME_EXIST("A0111", "用户名已存在"),
    USER_EMAIL_EXIST("A0113", "邮箱已存在"),
    USER_PHONE_EXIST("A0114", "手机号已存在"),
    USER_PASSWORD_ERROR("A0120", "用户密码错误"),
    USER_INVALID("A0143", "用户已禁用, 请联系管理员"),
    USER_NOT_LOGIN("A0141", "用户未登录"),
    USER_LOGIN_EXPIRED("A0142", "用户登录过期"),
    USER_DELETE_FAILED("A0144", "用户删除失败"),


    // ========== 权限相关 A022x ==========
    USER_PERMISSION_DENIED("A0220", "用户权限不足"),
    USER_ROLE_INVALID("A0221", "用户角色无效"),

    // ========== Token 相关 A023x ==========
    TOKEN_INVALID("A0230", "token无效"),
    TOKEN_EXPIRED("A0231", "token已过期"),
    TOKEN_BLOCKED("A0233", "token已加入黑名单"),

    // ========== 访问授权相关 A03xx ==========
    AUTHORIZED_ERROR("A0300", "访问权限异常"),
    ACCESS_UNAUTHORIZED("A0301", "访问未授权"),

    // ========== 文件相关（用户侧 A05xx / A06xx，系统侧 B10xx） ==========
    FILE_NOT_FOUND("A0501", "文件不存在"),
    FILE_TYPE_NOT_SUPPORTED("A0601", "不支持的文件类型"),
    FILE_SIZE_EXCEEDED("A0602", "文件大小超过限制"),
    FILE_CONTENT_INVALID("A0603", "文件内容不合法"),
    FILE_NAME_INVALID("A0604", "文件名不合法"),
    FILE_UPLOAD_FAILED("B1001", "文件上传失败"),
    FILE_SAVE_FAILED("B1002", "文件保存失败"),
    FILE_DELETE_FAILED("B1003", "文件删除失败"),

    // ========== 业务相关 ==========
    BUSINESS_ERROR("B0002", "业务处理失败"),
    USERNAME_OR_PASSWORD_EMPTY("B0003", "用户名或密码为空"),
    USERNAME_LENGTH_ERROR("B0004", "用户名长度错误"),
    PASSWORD_LENGTH_ERROR("B0005", "密码长度错误");



    private final String code;
    private final String msg;

    ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
