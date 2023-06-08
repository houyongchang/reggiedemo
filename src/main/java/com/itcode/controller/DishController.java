package com.itcode.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired(required = false)
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新建菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {

        dishService.saveWithFlavor(dishDto);

        return R.success("添加成功");

    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize, @RequestParam(value = "name", required = false) String name) {
        //dishPage模型
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        //dishDtoPage模型 返回给前端
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, queryWrapper);

        //此次pageInfo已经封装数据 ，将pageInfo中除了records排除出去，拷贝到dishDtoPage中去

        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
//        BeanUtil.copyProperties(pageInfo,dishDtoPage,"records");


        //对pageInfo中的records数据进行处理

        List<Dish> dishList = pageInfo.getRecords();

        List<DishDto> dishDtoList = dishList.stream().map((stem) -> {
            //将dish中的数据拷贝到DishDto中 封装categoryName字段
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(stem, dishDto);

            Long categoryId = stem.getCategoryId();

            Category category = categoryService.getById(categoryId);
            //解决空指针异常
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;


        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);


        return R.success(dishDtoPage);
    }

    /**
     * 修改 启售停售 批量启售停售
     *
     * @param type
     * @param id
     * @return
     */
    @PostMapping("/status/{type}")
    public R<String> update(@PathVariable Integer type, @RequestParam("ids") Long[] id) {
        log.info(type.toString());
        //单个修改
        if (id.length == 1) {

            Long DishId = id[0];

            dishMapper.updateType(type, DishId);

        } else {
            //批量启售停售

            dishMapper.updateAll(type, id);
        }
        return R.success("修改成功");


    }

    /**
     * 删除  批量删除
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long[] id) {
        for (Long aLong : id) {
            log.info(aLong.toString());
        }
        if (id.length == 1) {
            Long dishId = id[0];
            dishService.removeById(dishId);
        } else {
            List<Long> arr = new ArrayList<>();
            for (Long aLong : id) {
                arr.add(aLong);
            }
            dishService.removeByIds(arr);

        }
        return R.success("删除成功");


    }

    /**
     * 数据回显
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable("id") Long id) {

        DishDto dishDto = new DishDto();
        //根据id查询菜品信息
        Dish dish = dishService.getById(id);
        //将菜品基本信息拷贝到Dto对象中去
        BeanUtils.copyProperties(dish, dishDto);
        //根据dishId查询口味表
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        //查询口味表
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        //设置dto对象中的Flavor属性
        dishDto.setFlavors(list);

        return R.success(dishDto);
    }

    /**
     * 修改
     *
     * @param dishDto
     * @return
     */
    @Transactional
    @PutMapping
    public R<String> updateSave(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        //将菜品的基本信息修改
        dishService.updateById(dishDto);
        //将DishFlavor中相关id的口味删除
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //将新的口味数据添加表中
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((stem) -> {
            stem.setDishId(dishDto.getId());
            return stem;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

        return R.success("修改成功");
    }

    /**
     * 获取菜品列表
     *
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus,1);
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);
        //将list中的dish封装成dishDto
        List<DishDto> listDto = list.stream().map((stem) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(stem, dishDto);
            Long categoryId = stem.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, stem.getId());
            List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(listDto);
    }

}
