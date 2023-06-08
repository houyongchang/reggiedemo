package com.itcode.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcode.common.BaseContext;
import com.itcode.common.R;
import com.itcode.pojo.Orders;
import com.itcode.pojo.User;
import com.itcode.service.OrdersService;
import com.itcode.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private UserService userService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);

        return R.success("下单成功！");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize,
                        @RequestParam(value = "number",required = false) Long number,
                        @DateTimeFormat String beginTime, @DateTimeFormat String endTime){
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(number!=null,Orders::getNumber,number);
        if (beginTime!=null&&endTime!=null){
            queryWrapper.between(Orders::getCheckoutTime,beginTime,endTime);
        }
        ordersService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 修改订单那状态
     * mp通过接口更新数据时数据为NULL值时将不更新进数据库
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> updateStatus(@RequestBody Orders orders){
        ordersService.updateById(orders);
        return R.success("修改成功");
    }

    /**
     *分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(@RequestParam("page") int page,@RequestParam("pageSize") int pageSize){
        Page<Orders> orderInfo=new Page<>(page,pageSize);

        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(orderInfo,queryWrapper);
        return R.success(orderInfo);
    }

    /**
     * 再来一单
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public R<String> again( @RequestBody Orders orders){
        log.info("{}",orders.getId());

        Orders ordersAgain= ordersService.getById(orders.getId());

        ordersAgain.setStatus(2);

        long orderId = IdWorker.getId();//
        ordersAgain.setId(orderId);

        ordersService.save(ordersAgain);

        return R.success("谢谢肯定");
    }

    @GetMapping("getuser")
    public R<User> getUser(@RequestParam("userPhone") String userPhone){
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,userPhone);
        User user = userService.getOne(queryWrapper);
        return R.success(user);
    }
}
