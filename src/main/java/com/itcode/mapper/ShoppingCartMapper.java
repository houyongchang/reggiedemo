package com.itcode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itcode.pojo.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
