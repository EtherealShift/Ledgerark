//package org.ledgerark.framework.config;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Positive;
//import jakarta.validation.constraints.Size;
//import lombok.Data;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//import org.springframework.validation.annotation.Validated;
//
///**
// * JWT 配置属性类
// * <p>
// * 绑定 application.yaml 中前缀为 jwt 的配置项（密钥、有效期、请求头等），
// * 供 {@link org.ledgerark.framework.util.JwtTokenUtils} 签发/校验 Token
// * 及 {@link org.ledgerark.framework.util.JwtAuthenticationFilter} 提取 Token 使用。
// * <p>
// * 通过 @Validated 启动时即校验参数合法性，配置缺失或不合法时应用启动失败，避免带病运行。
// */
//@Data
//@Component
//@Validated
//@ConfigurationProperties(prefix="jwt")
//public class JwtConfig {
//
//    /** 签名密钥（HMAC-SHA256），要求非空白且不少于 32 字符，防止弱密钥被暴力破解 */
//    @NotBlank
//    @Size(min = 32)
//    private String secret;
//
//    /** Access Token 有效期（毫秒） */
//    @Positive
//    private long expiration;
//
//    /** Refresh Token 有效期（毫秒） */
//    @Positive
//    private long refreshExpiration;
//
//    /** 携带 Token 的请求头名称（如 Authorization） */
//    @NotBlank
//    private String header;
//
//    /** Token 前缀（如 Bearer），与 Token 之间以空格分隔 */
//    @NotBlank
//    private String tokenPrefix;
//}
