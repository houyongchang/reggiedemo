package com.itcode.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcode.common.CustException;
import com.itcode.common.R;
import com.itcode.dto.SetmealDto;
import com.itcode.mapper.SetmealMapper;
import com.itcode.pojo.Category;
import com.itcode.pojo.Dish;
import com.itcode.pojo.Setmeal;
import com.itcode.pojo.SetmealDish;
import com.itcode.service.CategoryService;
import com.itcode.service.SetmealDishService;
import com.itcode.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired(required = false)
    private SetmealMapper setmealMapper;

    /**
     * 新建套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {

        setmealService.saveWithDish(setmealDto);
        return R.success("添加成功");

    }

    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize, @RequestParam(value = "name", required = false) String name) {
        //Setmeal模型
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        //SetmealDto模型
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByAsc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
//
        List<Setmeal> setmealList = pageInfo.getRecords();

        List<SetmealDto> setmealDtoList = setmealList.stream().map((stem) -> {
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(stem, setmealDto);

            Long categoryId = stem.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoList);

        return R.success(setmealDtoPage);
    }

    /**
     * 删除 批量删除
     *
     * @param ids
     * @return
     */
    @Transactional
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids) {

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids).eq(Setmeal::getStatus, 1);
        int count = setmealService.count(queryWrapper);
        if (count > 0) {
            throw new CustException("有套餐在售卖");
        }
        //删除setmeal中的数据
        setmealService.removeByIds(ids);

//        //删除setmealDish中关联的数据
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);

        setmealDishService.remove(setmealDishLambdaQueryWrapper);


        return R.success("删除成功");
    }

    /**
     * 修改状态 status
     *
     * @param type
     * @param ids
     * @return
     */
    @PostMapping("status/{type}")
    public R<String> updateStatus(@PathVariable("type") int type, @RequestParam("ids") List<Long> ids) {

        if (ids.size() == 1) {
            for (Long id : ids) {
                setmealMapper.updateType(type, id);
            }
        } else {
            setmealMapper.updateAll(type, ids);
        }

        return R.success("修改成功");

    }

    /**
     * 数据回显
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable("id") Long id) {

        SetmealDto setmealDto = new SetmealDto();

        Setmeal setmeal = setmealService.getById(id);

        BeanUtils.copyProperties(setmeal, setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(setmealDishList);


        return R.success(setmealDto);
    }

    /**
     * 修改保存
     *
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> updateAndSave(@RequestBody SetmealDto setmealDto) {
        //修改setmeal中数据
        setmealService.updateById(setmealDto);

        //修改setmeal_dish中关联的数据
        List<SetmealDish> setmealDishesList = setmealDto.getSetmealDishes();
        Long setmealId = setmealDto.getId();
        //先将setmeal_dish中的数据删除
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getId, setmealId);
        setmealDishService.remove(queryWrapper);

        setmealDishesList = setmealDishesList.stream().map((stem) -> {

            stem.setSetmealId(setmealId);
            return stem;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishesList);

        return R.success("修改成功");
    }

    /**
     *
     * @param setmeal
     * @return
     */
    @GetMapping("list")
    public R<List<Setmeal>> list(Setmeal setmeal) {

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByAsc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);

    }

    @GetMapping("/dish/{id}")
    public R<Setmeal> getImage(@PathVariable("id") Long id){
        Setmeal setmeal = setmealService.getById(id);
        if (setmeal==null){
            return R.error("未有此套餐");
        }
        return R.success(setmeal);


    }

}
