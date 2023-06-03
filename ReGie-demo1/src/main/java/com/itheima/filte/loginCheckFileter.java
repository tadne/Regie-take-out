package com.itheima.filte;


import com.alibaba.fastjson.JSON;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import com.itheima.util.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import redis.clients.jedis.Jedis;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;




//检查用户是否登录
@Slf4j
@WebFilter(filterName = "loginCheckFileter",urlPatterns = "/*")
public class loginCheckFileter implements Filter
{


    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取本次请求的 uri
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}", requestURI);

        //要处理的url的数组
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",//移动端发送短信
                "/user/login"//移动端登录
        };
        boolean check = check(urls, requestURI);//检查是否在urls中

        //如果是不需要处理的url就放行
        if (check)
        {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);//放行
            return;
        }
        //如果是登录操作就直接放行
        if (requestURI.contains("login"))
        {
            log.info("登录操作，放行。。。");
            filterChain.doFilter(request, response);//放行
            return;
        }

        //获取token信息，jwt令牌
        String jwt = request.getHeader("Authorization");
        //如果不存在token就说明没有登录
        if (!StringUtils.hasLength(jwt))
        {
            log.info("请求头没有登录信息");
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return;
        }
        //如果在redis中就说明当前token已经失效
        Jedis jedis=new Jedis("localhost",6379);
        String tokenFlag = jedis.hget("tokens", jwt);
        System.out.println(jwt);
        if (tokenFlag!=null&&tokenFlag!="")
        {
            log.info("请重新登录");
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return;
        }

        try
        {
            //解析jwt令牌，如果解析没通过就执行catch
            Claims claims = JwtUtils.parseJWT(jwt);
            //eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoxNjY0MzIzODAxMzQ0MDIwNDgyLCJleHAiOjE2ODU4MDEyNDR9.zybDpTLVYYffYEXQUMrau5AZfOhkEzj9aD_9l6M86qw
            //eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoxNjY0MzIzODAxMzQ0MDIwNDgyLCJleHAiOjE2ODU4MDEyNDR9.zybDpTLVYYffYEXQUMrau5AZfOhkEzj9aD_9l6M86qw

            //将登录信息存到BaseContext中
            Object employee = claims.get("employee");
            Object user = claims.get("user");
            if (user != null)
            {
                BaseContext.setCurrentId((Long) user);
            }
            if (employee != null)
            {
                Integer employeeId = (Integer) employee;
                long l = employeeId.longValue();
                BaseContext.setCurrentId(l);
            }
            filterChain.doFilter(request, response);//放行
            return;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.info("解析令牌失败");
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return;
        }


        /**
         * 以下代码为session储存登录信息的操作
         */
        /*//  2   判断employee登录状态,已经登录则放行
        if(request.getSession().getAttribute("employee")!=null)//如果登录了
        {
            log.info("用户已登录,id 为:{}",request.getSession().getAttribute("employee"));

            //设置线程的局部变量存储id
            Long userId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);//放行
            return;
        }*/
        //  2   判断手机端user登录状态,已经登录则放行
        /*if(request.getSession().getAttribute("user")!=null)//如果登录了
        {
            log.info("用户已登录,id 为:{}",request.getSession().getAttribute("user"));

            //设置线程的局部变量存储id
            Long employeeId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(employeeId);

            filterChain.doFilter(request,response);//放行
            return;
        }*/

       /* //如果未登录则返回登录页面
        log.info("用户未登录");
        //这里是给页面响应一个记号,在/backend/js/request的响应拦截器,与之匹配
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));*/

    }

    //检查当前请求是否在urls中
    public boolean check(String[] urls, String requestURI)
    {
        for (String url : urls)
        {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match)
            {
                return true;
            }
        }
        return false;
    }

}



