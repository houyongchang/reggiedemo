package com.itcode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itcode.pojo.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    void updateType(@Param("type") Integer type, @Param("id") Long id);

    void updateAll(@Param("type") Integer type, @Param("ids") Long[] ids);

}
