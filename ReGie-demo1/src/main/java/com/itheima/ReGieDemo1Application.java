package com.itheima;

import com.itheima.pojo.Employee;
import com.itheima.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement//开启事务支持
public class ReGieDemo1Application
{

    public static void main(String[] args)
    {
        SpringApplication.run(ReGieDemo1Application.class, args);
        log.info("启动成功");
    }



}
