package com.example.aispringboot.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.aispringboot.config.JwtConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

//注入到上下文，需要实现ApplicationContextAware接口

@Component
public class JwtTokenUtil implements ApplicationContextAware {
    private static final String ISSUER = "issuer";
    private static ApplicationContext applicationContext;

    //静态工具类中获取spring容器管理的bean
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        JwtTokenUtil.applicationContext = applicationContext;
    }
    public static JwtConfig getJwtConfig() {
        return applicationContext.getBean(JwtConfig.class);
    };

    //生成token的方法
    public static String  generateToken(Long userId, String username, Integer roleType) {
       try{
           //处理生成token的逻辑
           JwtConfig jwtConfig = getJwtConfig();
           //生成签名的方法
           Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
           //生成过期时间
           Date expirationDate = new java.util.Date(System.currentTimeMillis()+jwtConfig.getExpiration());

           String token = JWT.create()
                   .withClaim("userId", userId)
                   .withClaim("username", username)
                   .withClaim("roleType", roleType)
                   .withExpiresAt(expirationDate)  //设置过期时间
                   .withIssuedAt(new Date()) //设置签发时间
                   .withIssuer(ISSUER)
                   .sign(algorithm);
           return token;
       } catch(Exception e){
           throw new RuntimeException("Token生成失败", e);
       }

    }


    //提取token
    public static String extractTokenFromRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String token = request.getHeader("token");
        if(StringUtils.hasText(token)) {
            return token;
        }
        return null;
    }


    //验证token
    public static TokenVerificatioResult validateToken(String token) {

        verifyToken(token);
        DecodedJWT jwt = verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();
        String username = jwt.getClaim("username").asString();

        //获取角色类型
        Integer roleType = null;
        try {
            roleType=jwt.getClaim("roleType").asInt();
        } catch (Exception e) {
           String roleTypeStr = jwt.getClaim("roleType").asString();
           if(StringUtils.hasText(roleTypeStr)){
              roleType= Integer.valueOf(roleTypeStr);

           }
        }

        //判断
        if(userId != null && StringUtils.hasText(username) && roleType != null){
            return new TokenVerificatioResult(userId,username,roleType,true);


        }
        return null;
    }

    //获取当前jwttoken
    public static String getCurrentToken() {
       ServletRequestAttributes attributes=(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
       if(attributes!=null){
           HttpServletRequest request = attributes.getRequest();
            String token = (String) request.getAttribute("jwtToken");
            if(token!=null){
                return token;
            }

            //备用方法
           String headertoken = request.getHeader("token");
            if(headertoken!=null){
                return headertoken;
            }
       }
       return null;
    }

    //验证token有效性
    public static DecodedJWT verifyToken(String token) {
        if(!StringUtils.hasText(token)){
            throw new JWTVerificationException("Token不能为空");
        }

        //token解码
        JwtConfig jwtConfig = getJwtConfig();
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        JWTVerifier verifier = JWT.require(algorithm)
                        .withIssuer(ISSUER)
                        .build();
        return verifier.verify(token);
    }



    //token验证结果封装类
    @Getter
    public static class TokenVerificatioResult{
        private final Long userId;
        private final String username;
        private final Integer roleType;
        private final boolean Valid;

        public TokenVerificatioResult(Long userId, String username, Integer roleType, boolean valid) {
            this.userId = userId;
            this.username = username;
            this.roleType = roleType;
            Valid = valid;
        }

    }




}
