package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.pojo.Category;
import com.itheima.pojo.Employee;
import com.itheima.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController
{
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category, HttpServletRequest request){
        log.info("category:"+category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        Page<Category> pageInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //lqw.eq(Category::getType, 1);
        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> deleteById(Long ids){
       // categoryService.removeById(id);
        categoryService.remove(ids);


        return R.success("删除成功");
    }


    @PutMapping
    public R<String> updeteById(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    //根据
    @GetMapping("/list")
    public R<List<Category>> getListByType(Category category){
        LambdaQueryWrapper<Category> lqw=new LambdaQueryWrapper<>();
        //按照 type 字段查询
        lqw.eq(category.getType()!=null, Category::getType,category.getType());
        //排序
        lqw.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
        List<Category> list = categoryService.list(lqw);
        return R.success(list);
    }




}
