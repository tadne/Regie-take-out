package com.itheima.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.mapper.DishFlavorMapper;
import com.itheima.pojo.DishFlavor;
import com.itheima.service.DishFlavorService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService
{
}
