//package org.ledgerark.framework.util;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import com.auth0.jwt.interfaces.JWTVerifier;
//import org.ledgerark.framework.config.JwtConfig;
//import org.springframework.stereotype.Component;
//
//import java.time.Instant;
//import java.util.Date;
//import java.util.UUID;
//
///**
// * JWT Token 工具类
// * <p>
// * 负责令牌的签发与验证：登录成功后调用 generateToken 生成携带用户身份的 Token，
// * 后续请求由 JwtAuthenticationFilter 调用 verifyToken 完成签名与有效期校验。
// * <p>
// * 签名算法与密钥在构造时初始化并复用，验证器同步限定签发者为 ledgerark，
// * 防止其他系统签发的令牌被误接受。
// */
//@Component
//public class JwtTokenUtils {
//
//    /** 令牌签发者标识，验证时作为固定校验条件 */
//    private static final String ISSUER = "ledgerark";
//
//    /** JWT 配置（密钥、有效期等） */
//    private final JwtConfig jwtConfig;
//    /** HMAC-SHA256 签名算法，签名与验证共用同一实例 */
//    private final Algorithm algorithm;
//    /** 预构建的验证器，内置签发者与签名校验规则 */
//    private final JWTVerifier verifier;
//
//    public JwtTokenUtils(JwtConfig jwtConfig) {
//        this.jwtConfig = jwtConfig;
//        this.algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
//        this.verifier = JWT.require(algorithm)
//                .withIssuer(ISSUER)
//                .build();
//    }
//
//    /**
//     * 签发 JWT Token。
//     * <p>
//     * 载荷中携带用户 ID、用户名、角色类型三类声明，
//     * 并附带唯一 ID（JWTId）用于将来吊销/黑名单场景，
//     * 有效期由配置项 jwt.expiration 控制。
//     *
//     * @param userId   用户 ID
//     * @param username 用户名
//     * @param roleType 角色编码
//     * @return 已签名的 Token 字符串
//     */
//    public String generateToken(Long userId, String username, String roleType) {
//        Instant issuedAt = Instant.now();
//        return JWT.create()
//                // 签发者
//                .withIssuer(ISSUER)
//                // 主题：用户名
//                .withSubject(username)
//                // 唯一标识，便于日后扩展令牌吊销
//                .withJWTId(UUID.randomUUID().toString())
//                // 签发时间
//                .withIssuedAt(Date.from(issuedAt))
//                // 自定义声明：用户身份与角色信息
//                .withClaim("userId", userId)
//                .withClaim("username", username)
//                .withClaim("roleType", roleType)
//                // 过期时间 = 签发时间 + 配置的有效期（毫秒）
//                .withExpiresAt(Date.from(issuedAt.plusMillis(jwtConfig.getExpiration())))
//                .sign(algorithm);
//    }
//
//    /**
//     * 验证 Token 的签名、有效期与签发者。
//     *
//     * @param token 待验证的 Token 字符串
//     * @return 解码后的 Token（可读取载荷中的声明）
//     * @throws com.auth0.jwt.exceptions.JWTVerificationException 验证失败（签名错误/过期/非本系统签发等）
//     */
//    public DecodedJWT verifyToken(String token) {
//        return verifier.verify(token);
//    }
//}
