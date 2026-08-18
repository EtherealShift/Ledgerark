//package org.ledgerark.framework.config;
//
//import jakarta.servlet.DispatcherType;
//import org.ledgerark.common.enums.ResultCode;
//import org.ledgerark.framework.security.SecurityErrorResponseWriter;
//import org.ledgerark.framework.util.JwtAuthenticationFilter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.access.AccessDeniedHandler;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
///**
// * Spring Security 安全配置
// * <p>
// * 采用无状态（STATELESS）JWT 认证模式：
// * 关闭所有基于会话/页面的传统认证机制（表单登录、HttpBasic、Logout、RequestCache），
// * 由自定义的 {@link JwtAuthenticationFilter} 在过滤器链中解析并校验 Token，
// * 认证信息存入 SecurityContextHolder，供后续授权判断使用。
// */
//@Configuration(proxyBeanMethods = false)
//@EnableMethodSecurity
//public class SecurityConfig {
//
//    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
//
//    /**
//     * 密码加密器（BCrypt）
//     * <p>
//     * 统一以 Bean 方式暴露，登录校验与密码重置等场景均注入此实例，
//     * 避免各处自行 new 导致编码参数（盐长度、强度）不一致。
//     */
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    /**
//     * 未认证处理器（401）
//     * <p>
//     * 匿名/未认证用户访问受保护资源时触发，
//     * 返回统一 JSON 错误体（USER_NOT_LOGIN）而非默认的登录页跳转。
//     */
//    @Bean
//    public AuthenticationEntryPoint authenticationEntryPoint() {
//        return (request, response, exception) -> {
//            log.warn("认证失败(401): {} {} - 未认证访问受保护资源, 原因: {}",
//                    request.getMethod(), request.getRequestURI(), exception.getMessage());
//            SecurityErrorResponseWriter.write(response, 401, ResultCode.USER_NOT_LOGIN);
//        };
//    }
//
//    /**
//     * 权限不足处理器（403）
//     * <p>
//     * 已认证但无权限访问时触发，
//     * 返回统一 JSON 错误体（USER_PERMISSION_DENIED）。
//     */
//    @Bean
//    public AccessDeniedHandler accessDeniedHandler() {
//        return (request, response, exception) -> {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            log.warn("授权失败(403): {} {} - 用户[{}]权限不足, 原因: {}",
//                    request.getMethod(), request.getRequestURI(),
//                    authentication != null ? authentication.getName() : "anonymous",
//                    exception.getMessage());
//            SecurityErrorResponseWriter.write(response, 403, ResultCode.USER_PERMISSION_DENIED);
//        };
//    }
//
//    /**
//     * 安全过滤器链配置
//     * <p>
//     * 职责：关闭传统会话机制、接管 401/403 响应、定义接口放行规则、
//     * 并将 JWT 过滤器插入到 UsernamePasswordAuthenticationFilter 之前。
//     */
//    @Bean
//    public SecurityFilterChain securityFilterChain(
//            HttpSecurity http,
//            JwtAuthenticationFilter jwtAuthenticationFilter,
//            AuthenticationEntryPoint authenticationEntryPoint,
//            AccessDeniedHandler accessDeniedHandler) throws Exception {
//
//        http
//                // 前后端分离 + 无 Cookie 会话，无需 CSRF 防护
//                .csrf(AbstractHttpConfigurer::disable)
//                // 启用 CORS，具体跨域规则由 CorsConfigurationSource 提供
//                .cors(cors -> { })
//                // 关闭表单登录、HttpBasic、登出、请求缓存等基于会话/页面的传统机制
//                .formLogin(AbstractHttpConfigurer::disable)
//                .httpBasic(AbstractHttpConfigurer::disable)
//                .logout(AbstractHttpConfigurer::disable)
//                .requestCache(AbstractHttpConfigurer::disable)
//                // 完全不创建 HttpSession，符合 JWT 无状态设计
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                // 401/403 交给上方自定义处理器，返回统一 JSON
//                .exceptionHandling(exceptions -> exceptions
//                        .authenticationEntryPoint(authenticationEntryPoint)
//                        .accessDeniedHandler(accessDeniedHandler))
//                .authorizeHttpRequests(authorize -> authorize
//                        // 放行错误转发（/error 的 ERROR dispatch），避免异常页本身又触发 401 导致二次包装
//                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
//                        // 预检请求不携带 Token，必须放行
//                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                        // 登录接口需匿名访问
//                        .requestMatchers(HttpMethod.POST, "/login/login").permitAll()
//                        // 首页、错误页、网站图标
//                        .requestMatchers("/", "/error", "/favicon.ico").permitAll()
//                        // 其余全部需要 JWT 认证
//                        .anyRequest().authenticated())
//                // JWT 过滤器插入在用户名密码认证过滤器之前，在授权判断前完成 Token 解析与认证填充
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        log.info("SecurityFilterChain 配置完成: 无状态JWT认证, 放行接口=[OPTIONS /**, POST /login/login, /, /error, /favicon.ico], 其余请求需认证");
//
//        return http.build();
//    }
//}
