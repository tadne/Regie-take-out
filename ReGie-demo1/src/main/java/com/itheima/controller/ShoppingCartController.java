package com.itheima.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import com.itheima.pojo.Setmeal;
import com.itheima.pojo.ShoppingCart;
import com.itheima.service.CategoryService;
import com.itheima.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController
{
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> insert(@RequestBody ShoppingCart shoppingCart){

        //设置用户 id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //查询当前在购物车中是套餐还是菜品
        LambdaQueryWrapper<ShoppingCart> lqw=new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,currentId);
        Long dishId = shoppingCart.getDishId();
        if(dishId!=null){
            //如果是菜品
            lqw.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            //如果是套餐
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //查询当前菜品或套餐是否在购物车中
        ShoppingCart one = shoppingCartService.getOne(lqw);

        if (one!=null){
            //存在,数量加一
            one.setNumber(one.getNumber()+1);
            shoppingCartService.updateById(one);
        }else{
            //不存在,添加
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one=shoppingCart;
        }

        return R.success(one);
    }


    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> lqw=new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        lqw.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lqw);
        return R.success(list);
    }

    @PostMapping("/sub")
    public R<String> sub(Long dishId){
        LambdaQueryWrapper<ShoppingCart> lqw=new LambdaQueryWrapper<>();
        lqw.eq(dishId!=null,ShoppingCart::getDishId,dishId);
        List<ShoppingCart> list = shoppingCartService.list(lqw);
        ShoppingCart shoppingCart = list.get(0);
        Integer number = shoppingCart.getNumber();
        shoppingCartService.removeById(shoppingCart);
        if (number>1){
            shoppingCart.setNumber(number-1);
            shoppingCartService.save(shoppingCart);
        }
        return R.success("删除成功");
    }


    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> lqw=new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(lqw);
        return R.success("清空成功");
    }




}
