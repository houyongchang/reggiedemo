package com.itcode.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.itcode.common.CustException;
import com.itcode.mapper.CategoryMapper;
import com.itcode.pojo.Category;
import com.itcode.pojo.Dish;
import com.itcode.pojo.Setmeal;
import com.itcode.service.CategoryService;
import com.itcode.service.DishService;
import com.itcode.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void move(Long id) {
        //判断该id是否关联了Dish
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if (count1 > 0) {
            //抛出异常 全局异常接收
            throw new CustException("关联了菜品,无法删除");
        }

        //判断该id是否关联了Setmeal
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2>0){
            throw new CustException("关联了套餐,无法删除");
        }
        //正常删除
        removeById(id);

    }
}
