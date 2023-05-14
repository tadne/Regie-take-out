package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.controller.CustomException;
import com.itheima.mapper.CategoryMapper;
import com.itheima.pojo.Category;
import com.itheima.pojo.Dish;
import com.itheima.pojo.Setmeal;
import com.itheima.service.CategoryService;
import com.itheima.service.DishService;
import com.itheima.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService
{
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    //根据 id 来删除分类
    //删除之前要进行判断
    //判断该分类是否还含有菜品,套餐,如果有,就不能删除
    @Override
    public void remove(Long id)
    {

        LambdaQueryWrapper<Dish> lqwDish=new LambdaQueryWrapper<>();
        lqwDish.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(lqwDish);
        if (count1>0)
        {
            throw new CustomException("当前分类关联了菜品,不能删除");
        }
        //查询当前分类是否关联菜品
        LambdaQueryWrapper<Setmeal> lqwSetmeal=new LambdaQueryWrapper<>();
        lqwSetmeal.eq(Setmeal::getCategoryId, id);
        int count2 = dishService.count(lqwDish);
        if (count2>0)
        {
            throw new CustomException("当前分类关联了套餐,不能删除");
        }
        //查询当前分离是否管理套餐
        categoryService.removeById(id);

    }
}
