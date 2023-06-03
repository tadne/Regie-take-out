package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.common.R;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import com.itheima.util.JwtUtils;
import com.itheima.util.SMSUtils;
import com.itheima.util.ValidateCodeUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.cookie.SM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController
{

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;


    //发送手机验证码
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
        //生成随机 4 位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code:"+code);
        //调用阿里云的短信访问 api
            //SMSUtils.sendMessage("","",phone,code);//签名,模版,手机号,验证码
        //将生成的验证码保存到 Session
            //session.setAttribute("phone",phone);
            //session.setAttribute("code",code);

            //将生成的验证码保存到redis中
            redisTemplate.opsForValue().set(phone,code,5,TimeUnit.MINUTES);

            return R.success("手机验证码发送成功");



        }
        return R.error("短信发送失败");
    }


    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){

        //Object rightCode = session.getAttribute("code");


        //System.out.println(rightCode);

        //获取手机号验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        //从redis中获取缓存的验证码
        String rightCode = (String) redisTemplate.opsForValue().get(phone);

        //从 Session 中获取正确验证码
        //进行比对
        if (code.equals(rightCode) && rightCode != null){
            //比对成功,则登录
            //登录的时候还要给下一个页面一个响应，将用户信息返回
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
            session.setAttribute("user",user.getId());//将user登录的信息保存到session中，方便之后验证登录信息
            //生成jwt令牌
            Map<String, Object> claims=new HashMap<>();
            claims.put("user",user.getId());
            String token = JwtUtils.generateJwt(claims);

            //删除redis中缓存的验证码
            redisTemplate.delete(phone);

            return R.success(user,token);
        }
        return R.error("验证码不正确");
    }

    //退出
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        String token = request.getHeader("Authorization");
        //将token存在redis中
        Jedis jedis=new Jedis("localhost",6379);
        jedis.hset("tokens",token,"true");
        jedis.expire("tokens:token",60*60);
        return R.success("退出成功");
    }










}
