package com.itcode.dto;


import com.itcode.pojo.Dish;
import com.itcode.pojo.DishFlavor;
import lombok.Data;

import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors;

    private String categoryName;

    private Integer copies;
}
