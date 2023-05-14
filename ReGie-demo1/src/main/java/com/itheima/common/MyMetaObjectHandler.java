package com.itheima.common;



import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


//自定义的元数据处理器
//可以在对应操作下自动对实体对象进行操作


@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler
{

    @Override
    public void insertFill(MetaObject metaObject)
    {

        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        //每次客户端的 http 请求都会分配一个新的线程来处理
        //其中,LoginCheckFilter 的 doFilter 方法
        //EmployeeController的 update 方法
        //MyMetaObjectHandle 的 update 方法
        //都是同一个线程来处理的

        //ThreadLocal不是 Thread,而是 Thread 的局部变量,使用它可以维护变量,为每个使用该变量的线程
        //提供福利的变量副本,即可以让每一个线程独立改变自己的副本,二不影响其他线程对应副本
        //为每一个线程提供单独一份储存空间,具有线程隔离的效果
        //只有在线程内才能取得对应的值,线程外无法访问

        //就可以基于这个来存储用户 id,在 doFilter 来获取当前用户 id
        //调用 ThreadLocal 的 set set 方法设置当前线程局部变量的值
        //然后在 MyMetaObjectHandle 的 updateFilter 方法中
        // 调用 ThreadLocal 的 get方法来获取id
        //

        //由此,我们就可以通过 ThreadLocal
        Long currentId = BaseContext.getCurrentId();
        metaObject.setValue("createUser",currentId);
        metaObject.setValue("updateUser",currentId);
        log.info("公共字段填充");
        log.info(metaObject.toString());
    }

    @Override
    public void updateFill(MetaObject metaObject)
    {
        Long currentId = BaseContext.getCurrentId();

        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser",currentId);

        log.info("公共字段填充");
        log.info(metaObject.toString());
    }
}
