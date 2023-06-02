package com.itheima.filte;


import com.alibaba.fastjson.JSON;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//检查用户是否登录
@Slf4j
@WebFilter(filterName = "loginCheckFileter",urlPatterns = "/*")
public class loginCheckFileter implements Filter
{
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
    {
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;

        //获取本次请求的 uri
        String requestURI = request.getRequestURI();

        log.info("拦截到请求：{}",requestURI);

        //判断本次请求是否要处理
        String[] urls=new String[]
        {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",//移动端发送短信
                "/user/login"//移动端登录
        };
        boolean check = check(urls, requestURI);

        //如果不需要就放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);//放行
            return;
        }
        //  2   判断employee登录状态,已经登录则放行
        if(request.getSession().getAttribute("employee")!=null)//如果登录了
        {
            log.info("用户已登录,id 为:{}",request.getSession().getAttribute("employee"));

            //设置线程的局部变量存储id
            Long userId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(userId);


            //获取令牌，解析令牌
            String jwt = request.getHeader("token");
            System.out.println(jwt);
            System.out.println("-----------------");
            /*Jwts.parser()//解析
                    .setSigningKey("itheima")
                    .parseClaimsJws(jwt)
                    .getBody();*/
            filterChain.doFilter(request,response);//放行
            return;
        }

        //  2   判断手机端user登录状态,已经登录则放行
        if(request.getSession().getAttribute("user")!=null)//如果登录了
        {
            log.info("用户已登录,id 为:{}",request.getSession().getAttribute("user"));

            //设置线程的局部变量存储id
            Long employeeId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(employeeId);

            //获取令牌，解析令牌
            /*String jwt = request.getHeader("token");
            Jwts.parser()//解析
                    .setSigningKey("itheima")
                    .parseClaimsJws(jwt)
                    .getBody();*/

            filterChain.doFilter(request,response);//放行
            return;
        }



        //如果未登录则返回登录页面
        log.info("用户未登录");
        //这里是给页面响应一个记号,在/backend/js/request的响应拦截器,与之匹配
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    public boolean check(String[] urls,String requestURI){
        for (String url : urls)
        {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return  false;
    }

}
