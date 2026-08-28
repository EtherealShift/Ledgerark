package org.ledgerark.framework.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns("/sys/login/doLogin")
                .excludePathPatterns("/sys/login/register")
                .excludePathPatterns("/test/**")
                // 排除 swagger 相关的路径
                .excludePathPatterns("/swagger-ui/**")
                .excludePathPatterns("/swagger-ui.html")
                // Swagger 资源走 swagger-resources 路径
                .excludePathPatterns("/swagger-resources/**")
                // Swagger UI 静态资源走 webjars 路径
                .excludePathPatterns("/webjars/**")
                // Swagger UI API 文档走 v3/api-docs 路径
                .excludePathPatterns("/v3/api-docs/**");
    }
}