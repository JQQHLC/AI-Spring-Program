package com.example.aispringboot.dto.command;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginCommandDto {
    @NotBlank(message = "用户名和邮箱不能诶空")
    @Size(max = 100, message = "用户名和邮箱不能超过100个字符")
    private String username;
    @NotBlank(message = "密码不能诶空")
    @Size(max = 50,min = 6, message = "密码不能超过50个字符,不能少于6个字符")
    private String password;
}
