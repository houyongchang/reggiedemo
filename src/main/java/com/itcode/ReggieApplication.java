package com.itcode;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//过滤器开启扫描
@ServletComponentScan
//开启事务注解
@EnableTransactionManagement
@MapperScan("com.itcode.mapper")
@Slf4j
public class ReggieApplication {
    public static void main(String[] args) {
        log.info("启动成功");
        log.info("6666");
        log.info("1111");
        SpringApplication.run(ReggieApplication.class, args);
    }

}
