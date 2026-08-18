package org.ledgerark.framework.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.ledgerark.framework.config.JwtConfig;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenUtils {

    private static final String ISSUER = "ledgerark";

    private final JwtConfig jwtConfig;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtTokenUtils(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        this.verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
    }

    public String generateToken(Long userId, String username, String roleType) {
        Instant issuedAt = Instant.now();
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(username)
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(Date.from(issuedAt))
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withClaim("roleType", roleType)
                .withExpiresAt(Date.from(issuedAt.plusMillis(jwtConfig.getExpiration())))
                .sign(algorithm);
    }

    public DecodedJWT verifyToken(String token) {
        return verifier.verify(token);
    }
}
