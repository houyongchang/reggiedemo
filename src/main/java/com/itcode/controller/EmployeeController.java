package com.itcode.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcode.common.R;
import com.itcode.pojo.Employee;
import com.itcode.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 用户登录
     *
     * @param employee
     * @param request
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request) {
        String password = employee.getPassword();
        password = DigestUtil.md5Hex(password.getBytes());
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee employeeOne = employeeService.getOne(queryWrapper);
        if (employeeOne == null) {
            return R.error("登录失败");
        }
        if (!employeeOne.getPassword().equals(password)) {
            return R.error("密码错误");
        }
        if (employeeOne.getStatus() == 0) {
            return R.error("用户被禁用");
        }

        request.getSession().setAttribute("employee", employeeOne.getId());

        return R.success(employeeOne);
    }

    /**
     * 用户退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 添加员工
     *
     * @param employee
     * @param request
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpServletRequest request) {
        //设置员工初始信息
        employee.setPassword(DigestUtil.md5Hex("123456".getBytes()));

        //使用mp自动填充策略生成
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        //
//        long emId = (long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(emId);
//        employee.setUpdateUser(emId);

        employeeService.save(employee);
        return R.success("添加成功");

    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize, @RequestParam(value = "name", required = false) String name) {
        log.error("page {} pageSize{} name {}", page, pageSize, name);
        //创建分页对象
        Page pageInfo = new Page(page, pageSize);
        //查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!StrUtil.isEmpty(name), Employee::getName, name);
        queryWrapper.orderByDesc(Employee::getCreateTime);
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);

    }

    /**
     * 修改员工信息和状态
     * @param employee
     * @param request
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody  Employee employee,HttpServletRequest request){

        Long emId =(Long) request.getSession().getAttribute("employee");
        //自动填充
//        employee.setUpdateUser(emId);
//        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);

        return R.success("修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee==null){
            return R.error("未找到");
        }
        return  R.success(employee);
    }

}
