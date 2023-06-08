package com.itcode.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.itcode.mapper.OrderDetailMapper;
import com.itcode.pojo.OrderDetail;
import com.itcode.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl  extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
