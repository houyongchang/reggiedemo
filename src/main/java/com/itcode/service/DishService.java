package com.itcode.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.itcode.dto.DishDto;
import com.itcode.pojo.Dish;

public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

}
