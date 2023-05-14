package com.itheima.common;

import com.itheima.controller.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;


//异常处理器

@ControllerAdvice(annotations = {RestController.class , Controller.class})
@ResponseBody
@Slf4j
public class globalExceptionHandlers
{
    //SQL 语句异常处理
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error("创建失败:"+ex.getMessage());

        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg=split[2]+"已经存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }


    //删除分类的异常处理
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error("创建失败:"+ex.getMessage());


        return R.error(ex.getMessage());
    }

}


