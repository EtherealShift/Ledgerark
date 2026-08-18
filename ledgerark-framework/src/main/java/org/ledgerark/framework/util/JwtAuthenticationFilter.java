//package org.ledgerark.framework.util;
//
//import com.auth0.jwt.exceptions.JWTVerificationException;
//import com.auth0.jwt.exceptions.TokenExpiredException;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.ledgerark.common.entity.SysUser;
//import org.ledgerark.common.enums.ResultCode;
//import org.ledgerark.common.enums.RoleCode;
//import org.ledgerark.common.exception.user.UserException;
//import org.ledgerark.framework.config.JwtConfig;
//import org.ledgerark.framework.security.SecurityErrorResponseWriter;
//import org.ledgerark.system.service.ISysUserService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Locale;
//
///**
// * JWT 认证过滤器
// * <p>
// * 在每个请求进入授权判断前，从请求头中提取并校验 JWT Token：
// * 校验通过则查询用户、构建认证信息并存入 SecurityContextHolder，
// * 供后续过滤器与方法级权限注解（@PreAuthorize 等）使用；
// * 校验失败则直接返回 401 统一 JSON 错误体，不再进入后续过滤器链。
// * <p>
// * 继承 OncePerRequestFilter 保证单次请求内只执行一次（不受 forward/include 等二次分发影响）。
// */
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
//
//    private final JwtTokenUtils jwtTokenUtils;
//    private final JwtConfig jwtConfig;
//    private final ISysUserService userService;
//
//    public JwtAuthenticationFilter(
//            JwtTokenUtils jwtTokenUtils,
//            JwtConfig jwtConfig,
//            ISysUserService userService) {
//        this.jwtTokenUtils = jwtTokenUtils;
//        this.jwtConfig = jwtConfig;
//        this.userService = userService;
//    }
//
//    /**
//     * 从请求头提取并校验 Token，认证成功则填充 SecurityContext 后放行。
//     *
//     * @param request     当前请求
//     * @param response    当前响应
//     * @param filterChain 后续过滤器链
//     */
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain) throws ServletException, IOException {
//
//        // 从请求头中提取 Token（如 Authorization: Bearer xxx）
//        String authorization = request.getHeader(jwtConfig.getHeader());
//        String token = extractToken(authorization);
//
//        // 无 Token（未登录访问开放接口）或已有认证信息（同一请求内重复进入）时直接放行，
//        // 交由后续授权规则（authorizeHttpRequests）判定是否拒绝
//        if (token == null || SecurityContextHolder.getContext().getAuthentication() != null) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            // 验证签名与有效期，解析出载荷中的用户声明
//            DecodedJWT jwt = jwtTokenUtils.verifyToken(token);
//            Long userId = jwt.getClaim("userId").asLong();
//            String username = jwt.getClaim("username").asString();
//
//            // 必要声明缺失则视为非法 Token
//            if (userId == null || !StringUtils.hasText(username)) {
//                throw new JWTVerificationException("JWT is missing required claims");
//            }
//
//            // 回读数据库而非仅信任 Token：使禁用账号与角色变更立即生效，
//            // 同时校验 Token 中的用户 ID 与当前账号一致，防止账号被顶替
//            SysUser user = userService.selectUserByUserName(username);
//            if (!userId.equals(user.getId())) {
//                throw new JWTVerificationException("JWT user does not match the current account");
//            }
//
//            // 将数据库中的最新角色转换为 Spring Security 权限标识（如 ROLE_ADMIN）
//            String authority = resolveAuthority(user.getRoleType());
//            // principal 存用户名，credentials 置空（无状态模式下无需保留密码），
//            // 携带单一角色权限，并附加请求级详情（IP、SessionId 等）
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(
//                            user.getUserName(),
//                            null,
//                            List.of(new SimpleGrantedAuthority(authority)));
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//            // 填充认证上下文，后续授权过滤器据此判定放行与否
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            // 透传用户 ID 与原始 Token，供业务层直接获取，避免重复解析
//            request.setAttribute("userId", user.getId());
//            request.setAttribute("jwtToken", token);
//            filterChain.doFilter(request, response);
//        } catch (TokenExpiredException exception) {
//            // Token 已过期：需重新登录获取新 Token
//            log.warn("JWT认证拒绝: {} {} - Token已过期: {}",
//                    request.getMethod(), request.getRequestURI(), exception.getMessage());
//            reject(response, ResultCode.TOKEN_EXPIRED);
//        } catch (JWTVerificationException | UserException | IllegalArgumentException exception) {
//            // Token 无效：签名错误、声明缺失、用户不匹配、角色非法等
//            log.warn("JWT认证拒绝: {} {} - Token无效({}): {}",
//                    request.getMethod(), request.getRequestURI(),
//                    exception.getClass().getSimpleName(), exception.getMessage());
//            reject(response, ResultCode.TOKEN_INVALID);
//        }
//    }
//
//    /**
//     * 从 Authorization 请求头中提取裸 Token。
//     * <p>
//     * 预期格式为「前缀 + 空格 + Token」（如 Bearer xxx），
//     * 头缺失、前缀不匹配或 Token 为空均返回 null。
//     */
//    private String extractToken(String authorization) {
//        // 请求头不存在
//        if (!StringUtils.hasText(authorization)) {
//            return null;
//        }
//
//        // 前缀不匹配（如非 Bearer 开头）视为未携带 Token
//        String prefix = jwtConfig.getTokenPrefix();
//        if (!StringUtils.hasText(prefix) || !authorization.startsWith(prefix)) {
//            return null;
//        }
//
//        // 去掉前缀并去除空白，得到裸 Token；全空白同样视为无效
//        String token = authorization.substring(prefix.length()).trim();
//        return StringUtils.hasText(token) ? token : null;
//    }
//
//    /**
//     * 将数据库中的角色编码转换为 Spring Security 权限标识。
//     * <p>
//     * 例如角色编码 1（管理员）→ ROLE_ADMIN，供 @PreAuthorize("hasRole('ADMIN')") 匹配。
//     *
//     * @param roleType 数据库存储的角色编码
//     * @return 形如 ROLE_XXX 的权限标识
//     * @throws IllegalArgumentException 角色编码未在 RoleCode 枚举中定义
//     */
//    private String resolveAuthority(String roleType) {
//        for (RoleCode role : RoleCode.values()) {
//            if (role.getCode().equals(roleType)) {
//                return "ROLE_" + role.getDescription().toUpperCase(Locale.ROOT);
//            }
//        }
//        // 未知角色编码：拒绝认证，防止非法角色获得默认权限
//        throw new IllegalArgumentException("Unknown role type");
//    }
//
//    /**
//     * 拒绝请求：清空安全上下文（防止残留半成品认证信息）并写入 401 统一 JSON 错误体。
//     */
//    private void reject(HttpServletResponse response, ResultCode resultCode) throws IOException {
//        SecurityContextHolder.clearContext();
//        SecurityErrorResponseWriter.write(response, 401, resultCode);
//    }
//}
