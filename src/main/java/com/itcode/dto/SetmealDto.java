package com.itcode.dto;


import com.itcode.pojo.Setmeal;
import com.itcode.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
