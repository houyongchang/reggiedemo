package com.itcode.filter;

import com.alibaba.fastjson.JSON;
import com.itcode.common.BaseContext;
import com.itcode.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object employeeId = request.getSession().getAttribute("employee");
        log.info("employeeID {}", employeeId);
        String requestURI = request.getRequestURI();
        log.info("拦截到的请求 {}", requestURI);
        Long emId = (Long) employeeId;

        if (employeeId != null) {
            //将empId存到ThreadLocal中去
            BaseContext.setCurrentId(emId);
            return true;
        }

        Long userId = (Long) request.getSession().getAttribute("user");
        if (userId != null) {
            BaseContext.setCurrentId(userId);
            return true;
        }

        //返回登录界面
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
