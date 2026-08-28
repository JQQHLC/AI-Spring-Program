package com.example.aispringboot.dto.command;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRegisterCommandDTO {

    //添加校验注解
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度在3到50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "用户名只能包含字母和数字")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size( max = 100, message = "邮箱长度不超过100个字符之间")
    private String email;

    @Size(max = 50, message = "昵称长度不超过50个字符之间")
    private String nickname;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度在6到50个字符之间")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;


    private int gender;
    private int userType = 1;
    private LocalDate birthday;
}
