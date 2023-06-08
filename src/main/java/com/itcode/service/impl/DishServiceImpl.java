package com.itcode.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcode.common.R;
import com.itcode.dto.DishDto;
import com.itcode.mapper.DishMapper;
import com.itcode.pojo.Category;
import com.itcode.pojo.Dish;
import com.itcode.pojo.DishFlavor;
import com.itcode.service.CategoryService;
import com.itcode.service.DishFlavorService;
import com.itcode.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     *
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {

        //将dishDto对象 添加到dish中去 ，基本信息
        this.save(dishDto);
        //获取菜品id
        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        //遍历口味，给每个设置DishId
        flavors = flavors.stream().map((stem) -> {
            stem.setDishId(dishId);
            return stem;

        }).collect(Collectors.toList());
        //添加
        dishFlavorService.saveBatch(flavors);

    }



}
