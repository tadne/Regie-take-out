package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.dto.DishDto;
import com.itheima.mapper.DishMapper;
import com.itheima.pojo.Category;
import com.itheima.pojo.Dish;
import com.itheima.pojo.DishFlavor;
import com.itheima.service.CategoryService;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService
{
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;


    @Override
    @Transactional//开启事务管理
    public void saveWithFlavor(DishDto dishDto)
    {
        dishService.save(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors=dishDto.getFlavors();
        flavors=flavors.stream().map((s)->{//map方法将每一个元素映射到对应的结果
                s.setDishId(dishId);
                return s;
            //collect 方法可以手机流中的元素到集合或者数组中
            //Collectors.toList 可以 collect 手机来的元素转换为新的集合
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);//saveBatch:批量插入方法




    }


    //根据 id 查询菜品,口味
    @Override
    public DishDto getByIdWithFlavor(Long id)
    {
        DishDto dishDto=new DishDto();
        //获取 dish 并将其复制到 dishDto
        Dish dish = dishService.getById(id);
        BeanUtils.copyProperties(dish,dishDto);

        /*//获取categoryName 并赋值
        Category category = categoryService.getById(id);
        String name = category.getName();
        dishDto.setCategoryName(name);*/
        //获取 flavors 并赋值
        LambdaQueryWrapper<DishFlavor> lqw=new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list = dishFlavorService.list(lqw);
        dishDto.setFlavors(list);


        return dishDto;
    }

    //更新菜品信息和口味信息
    @Override
    @Transactional//事务注解,管理事务
    public void updateWithFlavor(DishDto dishDto)
    {
        //更新 dish 表基本信息
        dishService.updateById(dishDto);


        //清理口味表数据
        LambdaQueryWrapper<DishFlavor> lqw=new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lqw);


        //插入新数据
        List<DishFlavor> flavors = dishDto.getFlavors();
            //这里也是,获取的 flavors 没有 id 属性,需要处理赋值
             flavors=flavors.stream().map(s->{
                 s.setDishId(dishDto.getId());
                 return  s;
             }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
