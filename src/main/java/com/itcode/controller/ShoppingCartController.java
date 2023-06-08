package com.itcode.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itcode.common.BaseContext;
import com.itcode.common.R;
import com.itcode.pojo.ShoppingCart;
import com.itcode.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //设置用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        //查询该菜品或者套餐是否被添加过
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        //如果菜品id不为空，封装查询条件
        if (shoppingCart.getDishId()!=null){
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        //能够查询到该数据做数量加一操作
       if (cart!=null){
           Integer number = cart.getNumber()+1;
           cart.setNumber(number);

           shoppingCartService.updateById(cart);
           //不存在做入库操作
       }else {
           shoppingCart.setNumber(1);
           shoppingCart.setCreateTime(LocalDateTime.now());
           shoppingCartService.save(shoppingCart);
           cart=shoppingCart;
       }
        return R.success(cart);

    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public  R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public  R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);
        return R.success("清空完成");
    }

    /**
     * 减少一份
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<List<ShoppingCart>> sub(@RequestBody ShoppingCart shoppingCart){
        Long dishId = shoppingCart.getDishId();
        log.info("id{}",dishId);

        // 查询到该购物车的数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getDishId,dishId);

        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        //查看number数据做减少和移除操作
        shoppingCarts=shoppingCarts.stream().map((stem)->{
            Integer number = stem.getNumber();
            if (number>1){
                stem.setNumber(number-1);
                shoppingCartService.updateById(stem);
            }else {
                shoppingCartService.removeById(stem);
            }
            return stem;
        }).collect(Collectors.toList());


        return R.success(shoppingCarts);


    }


}
