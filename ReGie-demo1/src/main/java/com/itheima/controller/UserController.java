package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.common.R;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import com.itheima.util.SMSUtils;
import com.itheima.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.cookie.SM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController
{

   @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;


    //发送时间验证码
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
        //生成随机 4 位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code:"+code);
        //调用阿里云的短信访问 api
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);//签名,模版,手机号,验证码
        //将生成的验证码保存到 Session



         //   session.setAttribute("phone",phone);
         //   session.setAttribute("code",code);

         //将验证码存到 redis 中
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);


            return R.success("手机验证码发送成功");
        }
        return R.error("短信发送失败");
    }


    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){


        //Object rightCode = session.getAttribute("code");


        //获取手机号验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        //从 redis 中取出验证码
        String rightCode= (String) redisTemplate.opsForValue().get(phone);

        //从 Session 中获取正确验证码
        //进行比对
        if (code.equals(rightCode) && rightCode != null){
            //比对成功,则登录
            LambdaQueryWrapper<User> lqw=new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone,phone);
            User user = userService.getOne(lqw);
            if (user==null){
                //判断是否为新用户,如果是则自动注册
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());

            //如果用户成功登录,删除 redis 中缓存的验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }
        System.out.println(111);

        return R.error("验证码不正确");
    }




}
