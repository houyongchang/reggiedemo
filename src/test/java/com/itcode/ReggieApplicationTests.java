package com.itcode;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.itcode.common.BaseContext;
import com.itcode.mapper.DishMapper;
import com.itcode.mapper.UserMapper;
import com.itcode.pojo.Category;
import com.itcode.pojo.Dish;
import com.itcode.pojo.Employee;
import com.itcode.pojo.User;
import com.itcode.service.CategoryService;
import com.itcode.service.EmployeeService;
import com.itcode.service.UserService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
class ReggieApplicationTests {
    /*
     *    **Mapper     多用于自定义sql的使用
     *    **Service    多用于Mybatis-plus封装的方法
     * */
    @Autowired(required = false)
    private UserMapper userMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private EmployeeService employeeService;

    @Resource
    private DishMapper dishMapper;

    @Test
    void select() {
        String s = "    ";
        boolean b1 = StrUtil.isNotBlank(s);
        System.out.println(b1);
    }
}
