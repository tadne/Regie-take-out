package com.itheima;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//@SpringBootTest
class ReGieDemo1ApplicationTests
{

    @Test
    void contextLoads()
    {
    }

    //生成JWT令牌
    @Test
    public void testFenJwt(){
        Map<String,Object> claims=new HashMap<>();
        claims.put("id","1");
        claims.put("name","zhangsan");
        String jwt = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, "itheima")//签名算法
                .setClaims(claims)//自定义内容，荷载
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))//设置有效期
                .compact();
        System.out.println(jwt);
    }

    //解析令牌
    @Test
    public void testParseJWT(){
        Claims claims = Jwts.parser()
                .setSigningKey("itheima")
                .parseClaimsJws("eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiemhhbmdzYW4iLCJpZCI6IjEiLCJleHAiOjE2ODUzNTQyNTB9.9tGf7nv08zStX8o-fYnwCytEnBVV90IdBaDgz1Ij_AY")
                .getBody();
        System.out.println(claims);//{name=zhangsan, id=1, exp=1685354250}第三个是过期时间
    }

}
