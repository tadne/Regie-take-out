package com.itheima.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;//身份证号

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


    //公共字段自动填充注解,@TableField
    //在实体类的属性上加入@TableField注解,指定自动填充的策略
    //填充测罗有 insert,update 等等.增改的时候就可以自动为这些字段填充
    //按照框架要求编写类,元数据对象处理器
    //在此类中统一为公共字段赋值,此类需要实现 MetaObjectHandler 接口
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
