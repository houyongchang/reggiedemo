package com.itcode.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itcode.pojo.Setmeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {

    void updateType(@Param("type") Integer type, @Param("id") Long id);

    void updateAll(@Param("type") Integer type, @Param("ids")List<Long> ids);
}
