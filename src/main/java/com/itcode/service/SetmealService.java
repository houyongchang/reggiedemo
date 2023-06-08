package com.itcode.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.itcode.dto.SetmealDto;
import com.itcode.pojo.Setmeal;


import java.util.List;

public interface SetmealService  extends IService<Setmeal> {

    void saveWithDish(SetmealDto setmealDto);
}
