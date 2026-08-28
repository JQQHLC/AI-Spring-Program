package com.example.aispringboot.controller;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.aispringboot.common.Result;
import com.example.aispringboot.dto.command.UserLoginCommandDto;
import com.example.aispringboot.dto.command.UserRegisterCommandDTO;
import com.example.aispringboot.dto.response.UserLoginResponseDTO;
import com.example.aispringboot.service.UserService;
import com.example.aispringboot.util.JwtTokenUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
public class User {

    @Resource
    private UserService userService;

    //用户登录接口
    @PostMapping("/login")
    public Result<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginCommandDto userLoginCommandDto) {
        System.out.println(userLoginCommandDto.getUsername());
        System.out.println(userLoginCommandDto.getPassword());
        // 调用服务层的登录方法
        UserLoginResponseDTO result = userService.login(userLoginCommandDto);
        System.out.println(result);
        return Result.ok(result);
    }


    //用户注册接口
    @PostMapping("/add")
    public Result<UserLoginResponseDTO.UserDetailResponseDTO> register(@Valid @RequestBody UserRegisterCommandDTO userRegisterCommandDTO){

        UserLoginResponseDTO.UserDetailResponseDTO result = userService.register(userRegisterCommandDTO);
        return Result.ok(result);

    }

    //获取当前用户名称
    @GetMapping("/current")
    public Result<UserLoginResponseDTO.UserDetailResponseDTO> getCurrentUser() {
        //如何从token中解析输出用户的ID
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt=JwtTokenUtil.verifyToken(token);
        Long userId= jwt.getClaim("userId").asLong();

        //调用service获取用户详情
        UserLoginResponseDTO.UserDetailResponseDTO result=userService.getUserById(userId);
        return Result.ok(result);

    }


}
