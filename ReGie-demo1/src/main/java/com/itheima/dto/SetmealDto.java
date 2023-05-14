package com.itheima.dto;


import com.itheima.pojo.Setmeal;
import com.itheima.pojo.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal
{

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
