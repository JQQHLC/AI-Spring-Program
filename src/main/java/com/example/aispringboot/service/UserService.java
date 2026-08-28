package com.example.aispringboot.service;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.aispringboot.common.Result;
import com.example.aispringboot.dto.command.UserLoginCommandDto;
import com.example.aispringboot.dto.command.UserRegisterCommandDTO;
import com.example.aispringboot.dto.response.UserLoginResponseDTO;
import com.example.aispringboot.entity.User;
import com.example.aispringboot.enumClass.UserType;
import com.example.aispringboot.exception.BusinessException;
import com.example.aispringboot.mapper.UserMapper;
import com.example.aispringboot.service.convert.UserConvert;
import com.example.aispringboot.util.JwtTokenUtil;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Resource
    UserMapper userMapper;
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    public UserLoginResponseDTO login(UserLoginCommandDto userLoginCommandDto) {
     //构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //根据用户名或邮箱查询
        queryWrapper.eq(User::getUsername, userLoginCommandDto.getUsername())
                .or()
                .eq(User::getEmail, userLoginCommandDto.getUsername());
        //调用mp的查询方法
        User user = userMapper.selectOne(queryWrapper);
        System.out.println(user);

        //判断用户是否存在
        if(user == null){
            throw new BusinessException("用户不存在");

        }
        //验证密码
        //获取用户输入的密码
        String password = userLoginCommandDto.getPassword();
        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        //检查用户状态
        if(user.isActive()) {
            throw new BusinessException("用户已禁用,请联系管理员");
        }
        //生成token
        String token = JwtTokenUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());
        System.out.println(token);
        UserLoginResponseDTO.UserDetailResponseDTO userInfo = UserConvert.entityToDetailResponse(user);
        return UserConvert.entityToDetailResponse(token, userInfo);

    }

    //用户注册逻辑
    public UserLoginResponseDTO.UserDetailResponseDTO register(UserRegisterCommandDTO userRegisterCommandDTO) {
        System.out.println(JSONUtil.parseObj(userRegisterCommandDTO));
        //验证密码
        if (!userRegisterCommandDTO.getPassword().equals(userRegisterCommandDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        //检查用户是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userRegisterCommandDTO.getUsername())
                .eq(User::getEmail, userRegisterCommandDTO.getEmail());
        Long count = userMapper.selectCount(queryWrapper);
        if (count != null && count > 0) {
            throw new BusinessException("用户已存在");
        }

        //检查邮箱是否已存在
        LambdaQueryWrapper<User> emailQueryWrapper = new LambdaQueryWrapper<>();
        emailQueryWrapper.eq(User::getEmail, userRegisterCommandDTO.getEmail());
        Long emailCount = userMapper.selectCount(emailQueryWrapper);
        if (emailCount != null && emailCount > 0) {
            throw new BusinessException("邮箱已存在");
        }

        //用户类型
        if(!UserType.isValidCode(userRegisterCommandDTO.getUserType())){
            throw new BusinessException("无效的用户类型");
        }

        //创建用户
        String password = userRegisterCommandDTO.getPassword().trim();
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        User user =UserConvert.registerCommandToEntity(userRegisterCommandDTO, encodedPassword);

        //插入数据库
        userMapper.insert(user);


        return UserConvert.entityToDetailResponse(user);

    }


    public UserLoginResponseDTO.UserDetailResponseDTO getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if(user == null){
            throw new BusinessException("用户不存在");

        }
        return UserConvert.entityToDetailResponse(user);
    }
}
