package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.controller.CustomException;
import com.itheima.mapper.SetmealMapper;
import com.itheima.pojo.Setmeal;
import com.itheima.pojo.SetmealDish;
import com.itheima.service.SetmealDishService;
import com.itheima.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService
{
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    public void removeWithDish(List<Long> ids)
    {

        LambdaQueryWrapper<Setmeal> lqw=new LambdaQueryWrapper<>();
        lqw.in(Setmeal::getId,ids);
        //查出Setmeal表中与 ids 对应 id 的套餐状态为 1 的套餐数量,如果大于 0 就不能删除
        lqw.eq(Setmeal::getStatus,1);
        int count =setmealService.count(lqw);
        if (count>0){
           throw  new CustomException("套餐正在售卖中,不能删除");
        }
        //为 0 就可以删除
        setmealService.removeByIds(ids);

        //在删除表SetmealDish中改套餐对应的菜品即可
        LambdaQueryWrapper<SetmealDish> lqw1=new LambdaQueryWrapper<>();
        lqw1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lqw1);




    }
}
