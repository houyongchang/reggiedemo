package com.itcode.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.itcode.dto.SetmealDto;
import com.itcode.mapper.SetmealMapper;
import com.itcode.pojo.Setmeal;
import com.itcode.pojo.SetmealDish;
import com.itcode.service.SetmealDishService;
import com.itcode.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //将数据添加到setmeal表中去
        save(setmealDto);
        //获取setmealDish中的数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //setmealDishes中缺少setmeal_id
       setmealDishes= setmealDishes.stream().map((stem)->{

           Long setmealId = setmealDto.getId();
           stem.setSetmealId(setmealId);
           return stem;

        }).collect(Collectors.toList());

       setmealDishService.saveBatch(setmealDishes);

    }
}
