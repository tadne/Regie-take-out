package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.DishDto;
import com.itheima.pojo.Category;
import com.itheima.pojo.Dish;
import com.itheima.pojo.DishFlavor;
import com.itheima.service.CategoryService;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController
{
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;


    //新增菜品
    @PostMapping
    public R<String> insert(@RequestBody DishDto dishDto){
        //这里的 dish 的参数不够保存这里的所有数据
        //所有这里新创建了一个新的类 dishDto 继承 dish 类扩展
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("添加成功");
    }


    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //创建Dish page 分页对象
        Page<Dish> pageInfo=new Page(page,pageSize);
        //由于 Dish 中没有分类名称,业务需要分类名称,所有创建 DishDto 类并 new
        Page<DishDto> dishDtoPage=new Page<>();
        //根据name 字段模糊查询
        LambdaQueryWrapper<Dish> lqw=new LambdaQueryWrapper();
        lqw.like(name!=null,Dish::getName,name);
        lqw.orderByDesc(Dish::getUpdateTime);
        //对 pageInfo 进行赋值,将模糊查询的结果赋给 pageInfo
        dishService.page(pageInfo, lqw);

        //对象拷贝,将赋值之后的 pageInfo 中的数据复制给dishDtoPage,
        //但是要去除 records 这个属性
        //因为要对其进行处理
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //取出 records 对象
        List<Dish> records = pageInfo.getRecords();
        //对 records 对象进行处理
        List<DishDto> list=records.stream().map(s->{//对records中元素进行操作
            //创建一个 dishDto 为最终返回对象
            DishDto dishDto=new DishDto();
            //将records中的 Dish 复制到 dishDto 中
            BeanUtils.copyProperties(s,dishDto);
            //取出其中的 CategoryId
            Long categoryId = s.getCategoryId();
            //根据CategoryId在Category表中查询CategoryName
            Category category = categoryService.getById(categoryId);
            //将CategoryName 赋值到 dishDto 中
            //这里为了避免空指针异常,如果为空就不设置了
            if(category!=null){
                String name1 = category.getName();
                dishDto.setCategoryName(name1);
            }
            //这个就得到了一个满足业务需求的records元素了
            return dishDto;
        }).collect(Collectors.toList());//将这些新 disDish 汇集成新的集合list
        //将 list 放到 records 属性中并返回page
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    //根据 id 查询
    @GetMapping("{id}")
    public R<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }


    //修改
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功 ");
    }

   /* //根据  categoryId 查询菜品
    @RequestMapping("/list")
    public R<List<Dish>> getListById(Long categoryId){
        LambdaQueryWrapper<Dish> lqw=new LambdaQueryWrapper<>();
        lqw.eq(categoryId!=null,Dish::getCategoryId,categoryId);
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lqw);
        return R.success(list);
    }*/

    //根据  categoryId 查询菜品
    @RequestMapping("/list")
    public R<List<DishDto>> getListById(Long categoryId){
        LambdaQueryWrapper<Dish> lqw=new LambdaQueryWrapper<>();
        lqw.eq(categoryId!=null,Dish::getCategoryId,categoryId);
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lqw);
        List<DishDto> dishDtoList = list.stream().map(s ->
        {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(s, dishDto);
            Long id = s.getId();
            LambdaQueryWrapper<DishFlavor> lqw1=new LambdaQueryWrapper<>();
            lqw1.eq(DishFlavor::getDishId,id);
            List<DishFlavor> list1 = dishFlavorService.list(lqw1);
            dishDto.setFlavors(list1);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }


}
