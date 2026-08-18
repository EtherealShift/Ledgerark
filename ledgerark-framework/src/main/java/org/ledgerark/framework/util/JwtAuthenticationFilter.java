package org.ledgerark.framework.util;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ledgerark.common.entity.SysUser;
import org.ledgerark.common.enums.ResultCode;
import org.ledgerark.common.enums.RoleCode;
import org.ledgerark.common.exception.user.UserException;
import org.ledgerark.framework.config.JwtConfig;
import org.ledgerark.framework.security.SecurityErrorResponseWriter;
import org.ledgerark.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenUtils jwtTokenUtils;
    private final JwtConfig jwtConfig;
    private final ISysUserService userService;

    public JwtAuthenticationFilter(
            JwtTokenUtils jwtTokenUtils,
            JwtConfig jwtConfig,
            ISysUserService userService) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.jwtConfig = jwtConfig;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader(jwtConfig.getHeader());
        String token = extractToken(authorization);

        if (token == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            DecodedJWT jwt = jwtTokenUtils.verifyToken(token);
            Long userId = jwt.getClaim("userId").asLong();
            String username = jwt.getClaim("username").asString();

            if (userId == null || !StringUtils.hasText(username)) {
                throw new JWTVerificationException("JWT is missing required claims");
            }

            // Re-read the user so disabled accounts and role changes take effect immediately.
            SysUser user = userService.selectUserByUserName(username);
            if (!userId.equals(user.getId())) {
                throw new JWTVerificationException("JWT user does not match the current account");
            }

            String authority = resolveAuthority(user.getRoleType());
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user.getUserName(),
                            null,
                            List.of(new SimpleGrantedAuthority(authority)));
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.setAttribute("userId", user.getId());
            request.setAttribute("jwtToken", token);
            filterChain.doFilter(request, response);
        } catch (TokenExpiredException exception) {
            log.warn("JWT认证拒绝: {} {} - Token已过期: {}",
                    request.getMethod(), request.getRequestURI(), exception.getMessage());
            reject(response, ResultCode.TOKEN_EXPIRED);
        } catch (JWTVerificationException | UserException | IllegalArgumentException exception) {
            log.warn("JWT认证拒绝: {} {} - Token无效({}): {}",
                    request.getMethod(), request.getRequestURI(),
                    exception.getClass().getSimpleName(), exception.getMessage());
            reject(response, ResultCode.TOKEN_INVALID);
        }
    }

    private String extractToken(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            return null;
        }

        String prefix = jwtConfig.getTokenPrefix();
        if (!StringUtils.hasText(prefix) || !authorization.startsWith(prefix)) {
            return null;
        }

        String token = authorization.substring(prefix.length()).trim();
        return StringUtils.hasText(token) ? token : null;
    }

    private String resolveAuthority(String roleType) {
        for (RoleCode role : RoleCode.values()) {
            if (role.getCode().equals(roleType)) {
                return "ROLE_" + role.getDescription().toUpperCase(Locale.ROOT);
            }
        }
        throw new IllegalArgumentException("Unknown role type");
    }

    private void reject(HttpServletResponse response, ResultCode resultCode) throws IOException {
        SecurityContextHolder.clearContext();
        SecurityErrorResponseWriter.write(response, 401, resultCode);
    }
}
