package com.itheima.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.pojo.Employee;
import com.itheima.service.EmployeeService;
import com.itheima.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController
{
    @Autowired
    private EmployeeService employeeService;
    //登录
    @RequestMapping("/login")
    public R<Employee> login(@RequestBody Employee employee,HttpServletRequest request){
        //将页面的密码加密
        String password=employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println(password);
        //根据用户名查询数据库
        LambdaQueryWrapper<Employee> lqw=new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername,employee.getUsername());//等值查询,即条件查询.第一个参数为数据库的,第二个参数为页面的
        Employee employee1 = employeeService.getOne(lqw);

        if (employee1==null){
            return R.error("用户名错误");
        }
        if(!employee1.getPassword().equals(password)){
            return R.error("密码错误");
        }
        if(employee1.getStatus()!=1){
            return R.error("当前账号已经被禁用");
        }
        //登录成功,将员工 id 存入 Session 并返回登录成功结果
        //生成jwt令牌
        Map<String, Object> claims=new HashMap<>();
        claims.put("employee",employee1.getId());
        String token = JwtUtils.generateJwt(claims);

        request.getSession().setAttribute("employee",employee1.getId());


        return R.success(employee1,token);
    }

    //退出
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        String token = request.getHeader("Authorization");
        //将token存在redis中
        Jedis jedis=new Jedis("localhost",6379);
        jedis.hset("tokens",token,"true");
        jedis.expire("tokens:token",60*60);
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpServletRequest request){
        //设置初始密码,加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置创建时间和更新时间
       // employee.setCreateTime(LocalDateTime.now());
       // employee.setUpdateTime(LocalDateTime.now());
        //获取当前登录用户的 id
        Long employeeId = (Long) request.getSession().getAttribute("employee");

        //设置创建人和更新人
      //  employee.setCreateUser(employeeId);
      //  employee.setUpdateUser(employeeId);
        employeeService.save(employee);
        return R.success("新增成功");
    }


    //分页查询
    @GetMapping("/page")
    //缓存注解，将方法返回值放入缓存中，并且下次调用就先查缓存再调用方法
    @Cacheable(value = "EmployeePageCache", key = "#page + '_' + #pageSize + '_' + #name")
    public R<Page> page(int page,int pageSize,String name){
        log.info(page+","+pageSize+","+name);
        Page p=new Page();
        p.setCurrent(page);
        p.setSize(pageSize);
        LambdaQueryWrapper<Employee> lqw=new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        Page page1 = employeeService.page(p,lqw);
        return R.success(page1);
    }


    //根据 id 修改员工信息
    @PutMapping
    //删除EmployeePageCache缓存数据
    @CacheEvict(value="EmployeePageCache",allEntries = true)
    public R<String> update(@RequestBody Employee employee,HttpServletRequest request){

       // employee.setUpdateTime(LocalDateTime.now());
        //Long employeeId = (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateUser(employeeId);

        employeeService.updateById(employee);
        return R.success("修改成功");
    }

    //根据 id 查询员工信息
    @RequestMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee==null){
            return R.error("没有查询到该员工");
        }
        return R.success(employee);
    }


}
