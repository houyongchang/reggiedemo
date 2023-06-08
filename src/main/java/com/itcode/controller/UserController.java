package com.itcode.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itcode.common.BaseContext;
import com.itcode.common.R;
import com.itcode.pojo.User;
import com.itcode.service.UserService;
import com.itcode.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送验证码
     *
     * @param user
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpServletRequest httpServletRequest) {
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            log.info("验证码为：{}", code);

            httpServletRequest.getSession().setAttribute(phone, code);
            return R.success("发送成功");
        }
        return R.error("发送失败");

    }

    /**
     * 移动端登录
     * @param map
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpServletRequest httpServletRequest) {
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();

        String attributeCode = (String) httpServletRequest.getSession().getAttribute(phone);

        if ( attributeCode != null && code.equals(attributeCode)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user==null){
                //证明是新用户，将该用户添加到数据库
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            //将用户id存到session中去
            httpServletRequest.getSession().setAttribute("user",user.getId());

            return R.success(user);

        }

        return R.error("失败");
    }

    /**
     * 退出
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest httpServletRequest){
        httpServletRequest.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
