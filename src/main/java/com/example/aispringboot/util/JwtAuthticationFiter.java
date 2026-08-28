package com.example.aispringboot.util;

import cn.hutool.json.JSONUtil;
import com.example.aispringboot.common.ResultCode;
import com.example.aispringboot.config.SecurityConfig;
import com.example.aispringboot.dto.response.UserLoginResponseDTO;
import com.example.aispringboot.enumClass.UserStatus;
import com.example.aispringboot.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

//确保每个请求只被处理一次
public class JwtAuthticationFiter extends OncePerRequestFilter {

    @Resource
    private  UserService userService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)  {
        String requestUrl = request.getRequestURI();
        return SecurityConfig.isPublicPATH(requestUrl);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 获取请求的url和方法
        String requestUrl = request.getRequestURI();
        String requestMethod = request.getMethod();
        System.out.println("Request URL: " + requestUrl + ", Method: " + requestMethod);

        // 1.提取JWT token
        String token = JwtTokenUtil.extractTokenFromRequest(request);
        if(StringUtils.hasText(token)){
            // 2.验证token并获取用户信息
            JwtTokenUtil.TokenVerificatioResult verificationResult = JwtTokenUtil.validateToken(token);
            if(verificationResult != null && verificationResult.isValid()){
            //3.查询用户信息验证用户状态
                UserLoginResponseDTO.UserDetailResponseDTO user = userService.getUserById(verificationResult.getUserId());
                System.out.println(JSONUtil.parseObj(user));
                if(user != null && UserStatus.NORMAL.getCode().equals(user.getStatus())){
                    //4.创建Spring Security认证对象
                   List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE"+verificationResult.getRoleType())
                    );
                   //5创建usernamePasswordAuthenticationToken对象
                    UsernamePasswordAuthenticationToken authcation = new UsernamePasswordAuthenticationToken(
                            verificationResult.getUsername(),
                            null,
                            authorities
                    );
                    //6设置认证信息到spring security上下文  (最关键的内容)
                    SecurityContextHolder.getContext().setAuthentication(authcation);

                    //7将token存储到请求的属性中
                    request.setAttribute("jwtToken", token);
                }else{
                    clearSecurityContext();
                    ResponseUtil.writeError(response, ResultCode.TOKEN_ACCESS_FORBIDDEN);

                }
            }

        }else{
            // 清理spring security上下文
            clearSecurityContext();
            ResponseUtil.writeError(response, ResultCode.TOKEN_INVALID);
            return;

        }
        // 继续处理请求
        filterChain.doFilter(request, response);
    }


    private void clearSecurityContext() {
        // 实现清理Spring Security上下文的逻辑
        SecurityContextHolder.clearContext();
    }
}
