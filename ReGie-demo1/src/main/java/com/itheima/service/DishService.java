package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.dto.DishDto;
import com.itheima.pojo.Dish;


public interface DishService extends IService<Dish>
{
    //新增菜品,同时插入菜品对应的口味数据,操作dish和 dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //更加 id 查询 dishDto
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息和口味信息
    public void updateWithFlavor(DishDto dishDto);
}
