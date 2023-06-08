package com.itcode.controller;


import cn.hutool.core.lang.UUID;
import com.itcode.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {

        log.info(file.toString());
        //获取原文件名称
        String originalFilename = file.getOriginalFilename();
        //接取文件后缀
        String huoZhui = originalFilename.substring(originalFilename.lastIndexOf("."));
        //生成新的文件名称
        String fileName = UUID.randomUUID().toString() + huoZhui;
        //判断路径是否存在
        File file1 = new File(basePath);
        if (!file1.exists()) {
            file1.mkdirs();
        }

        //文件转存到到该目录
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    /**
     * 文件下载，回显到浏览器
     *
     * @param name
     * @param response
     */
    @GetMapping("download")
    public void download(@RequestParam("name") String name, HttpServletResponse response) {

        try {
            //从目录中读取文件
            FileInputStream inputStream = new FileInputStream(new File(basePath + name));
            //获取输出流
            ServletOutputStream outputStream = response.getOutputStream();
            //设置响应文件类型
            response.setContentType("image/.jpeg");
            //读写
            int len;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            //关闭资源
            inputStream.close();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
