package com.itheima.controller;

import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterSessionStatement;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.DishDto;
import com.itheima.pojo.Category;
import com.itheima.pojo.Dish;
import com.itheima.pojo.Orders;
import com.itheima.pojo.User;
import com.itheima.service.OrderService;
import com.itheima.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController
{
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Orders> pageInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        orderService.page(pageInfo,queryWrapper);
        System.out.println(pageInfo.getRecords().get(0).getUserId());
        System.out.println("---------------");
        pageInfo.getRecords().forEach(s->{
            Long userId = s.getUserId();
            System.out.println(userId);
            User user = userService.getById(userId);
            String name1 = user.getName();
            System.out.println(name1);
            s.setUserName(name1);
        });
        //lqw.eq(Category::getType, 1);
        //queryWrapper.orderByAsc(Orders::getSort);


        return R.success(pageInfo);
    }



}
