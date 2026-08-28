package com.example.aispringboot.config;


import cn.hutool.core.text.AntPathMatcher;
import com.example.aispringboot.util.JwtAuthticationFiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration  //配置类注解
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private static final String[] PUBLIC_PATHS = {
            "/",
            "/api/test",
            "/api/user/login",
            "/api/user/add"
    };


    public static Boolean isPublicPATH(String requestUrl) {
        for (String path : PUBLIC_PATHS) {
            if (antPathMatcher.match(path, requestUrl)) {
                return true;
            }
        }
        return false;
    }

    @Bean
    public JwtAuthticationFiter jwtAuthticationFiter() {
        return new JwtAuthticationFiter();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF保护（前后端分离API无需CSRF）
                .csrf(AbstractHttpConfigurer::disable)

                // 配置会话管理为无状态（JWT模式使用）
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ) // 这里必须闭合sessionManagement的配置块，回到HttpSecurity主链路

                // 配置请求授权规则（和csrf、sessionManagement同级）
                .authorizeHttpRequests(auth -> auth
                        // 公开路径放行
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        // 其余所有请求都需要认证
                        .anyRequest().authenticated()
                )
        //添加JWT认证过滤器
                  .addFilterBefore(jwtAuthticationFiter(), UsernamePasswordAuthenticationFilter.class);



        return http.build();
    }
}

