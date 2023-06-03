package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.SetmealDto;
import com.itheima.pojo.Category;
import com.itheima.pojo.Setmeal;
import com.itheima.pojo.SetmealDish;
import com.itheima.service.CategoryService;
import com.itheima.service.SetmealDishService;
import com.itheima.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController
{
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;


    @PostMapping
    @Transactional
    //删除所有缓存数据
    @CacheEvict(value="setmealCache",allEntries = true)
    public R<String> insert(@RequestBody SetmealDto setmealDto){
        //先插入setmeal数据
        setmealService.save(setmealDto);
        //插入 setmealDish 数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> list = setmealDishes.stream().map(s ->
        {
            s.setSetmealId(setmealDto.getId());
            return s;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(list);
        return R.success("添加成功");
    }

    @RequestMapping("/page")
    //缓存注解，将方法返回值放入缓存中，并且下次调用就先查缓存再调用方法
    @Cacheable(value="setmealPageCache",key="#page+'_'+#pageSize+'_'+#name")
    public R<Page<SetmealDto>> page(int page,int pageSize,String name){
        //创建 Setmeal 页对象,但是不够业务需求
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        //满足业务需求,创建SetmealDto页对象
        Page<SetmealDto> setmealDtoPage=new Page<>();

        //查出 setmeal 中的相应数据
        LambdaQueryWrapper<Setmeal> lqw =new LambdaQueryWrapper<>();
        lqw.like(name!=null,Setmeal::getName,name);
        setmealService.page(pageInfo,lqw);
        //将pageInfo中的数据复制到setmealDtoPage中
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");

        //取出pageInfo中的 records 集合,对其所缺的业务属性进行补全
        List<Setmeal> records = pageInfo.getRecords();
        //对 records 中的每一个元素遍历操作
        List<SetmealDto> list = records.stream().map(s ->
        {
            //创建一个setmealDto对象,因为setmeal的属性不满足业务需求
            SetmealDto setmealDto = new SetmealDto();
            //获取records的元素的setmeal中的categoryId,并用此进行查询categoryName
            Long categoryId = s.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            //存入setmealDto中
            setmealDto.setCategoryName(categoryName);
            //再将setmeal中的数据传到setmealDto中
            BeanUtils.copyProperties(s, setmealDto);
            return setmealDto;
        }).collect(Collectors.toList());//以setmealDto 为元素封装集合

        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }


    @DeleteMapping
    //删除setmealCache缓存数据
    @CacheEvict(value={"setmealListCache","setmealPageCache"},allEntries = true)
    public R<String> deleteByIds(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    //id查套餐
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        SetmealDto setmealDto=new SetmealDto();
        Setmeal setmeal = setmealService.getById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);

        Long setmealId = setmeal.getId();
        LambdaQueryWrapper<SetmealDish> lqw =new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,setmealId);
        List<SetmealDish> list = setmealDishService.list(lqw);
        setmealDto.setSetmealDishes(list);

        return R.success(setmealDto);
    }

    //修改套餐
    @PutMapping
    //删除setmealCache缓存数据
    @CacheEvict(value= {"setmealListCache","setmealPageCache"},allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto){
        //修改setmeal 的数据
        setmealService.updateById(setmealDto);

        //修改 setmealDish 的数据
        //先删除,再添加
        //删除
        Long id = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> lqw=new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,id);
        setmealDishService.remove(lqw);

        //添加
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().peek(s-> s.setSetmealId(id)).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);

        return R.success("修改成功");
    }



    @GetMapping("/list")
    //缓存注解，将方法返回值放入缓存中，并且下次调用就先查缓存再调用方法
    @Cacheable(value="setmealListCache",key="#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list( Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> lqw=new LambdaQueryWrapper<>();
        lqw.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lqw.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        lqw.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(lqw);
        return R.success(list);

    }


}
