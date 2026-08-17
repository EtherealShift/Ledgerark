package org.ledgerark.framework.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import org.ledgerark.framework.config.JwtConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtils implements ApplicationContextAware {

    private static final String ISSUER = "mental-health-assistant";

    private static ApplicationContext applicationContext;
    // 用于再静态工具类中获取Spring容器管理的Bean
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        JwtTokenUtils.applicationContext = applicationContext;
    }

    private static JwtConfig getJwtConfig() {
        return applicationContext.getBean(JwtConfig.class);
    };

    public static String generateToken(Long userId, String username, String roleType) {
        try {
            // 获取jwt的配置
            JwtConfig jwtConfig = getJwtConfig();
            // 生成签名的算法
            Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
            // 生成JWT
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withClaim("userId", userId)
                    .withClaim("username", username)
                    .withClaim("roleType", roleType)
                    .withExpiresAt(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                    .sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException("生成token 失败: " + e);
        }
    }
}

