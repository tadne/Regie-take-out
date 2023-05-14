package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.pojo.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal>
{
    public void removeWithDish(List<Long> ids);
}
