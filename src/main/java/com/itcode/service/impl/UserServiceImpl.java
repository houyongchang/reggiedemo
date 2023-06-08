package com.itcode.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcode.mapper.UserMapper;
import com.itcode.pojo.User;
import com.itcode.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl  extends ServiceImpl<UserMapper, User> implements UserService {
}
