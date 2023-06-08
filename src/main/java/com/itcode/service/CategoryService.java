package com.itcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itcode.pojo.Category;


public interface CategoryService extends IService<Category> {


     void move(Long id);
}
