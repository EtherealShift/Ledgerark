package org.ledgerark.framework.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Component
@Validated
@ConfigurationProperties(prefix="jwt")
public class JwtConfig {
    @NotBlank
    @Size(min = 32)
    private String secret;

    @Positive
    private long expiration;

    @Positive
    private long refreshExpiration;

    @NotBlank
    private String header;

    @NotBlank
    private String tokenPrefix;
}
