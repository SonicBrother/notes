package com.gxc.baizi.springbootjwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.HashMap;

@SpringBootTest
class SpringbootjwtApplicationTests {

    @Test
    void contextLoads() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, 300);

        HashMap<String,Object> map = new HashMap<>();// 设置headers信息 不写也一样
        String sign = JWT.create()
//                .withHeader(map) // header
                .withClaim("userid", 21)  // payload
                .withClaim("userName", "zhangsan")
                .withExpiresAt(instance.getTime()) // 过期时间 一般3天，一周
                .sign(Algorithm.HMAC256("111"));// 签名

        System.out.println(sign);
    }

    @Test
    public void test1(){
        // 创建验证对象
        JWTVerifier build = JWT.require(Algorithm.HMAC256("111")).build();
        DecodedJWT verify = build.verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyTmFtZSI6InpoYW5nc2FuIiwiZXhwIjoxNjEyMTg2NDA3LCJ1c2VyaWQiOjIxfQ.N6YqzWfwQVl3mPclk5eII0FrNZqebcpickvuVVEpmS8");
        System.out.println(verify.getClaim("userid").asInt());// 取得时候要和存入时候的类型相同
        System.out.println(verify.getClaim("userName").asString());
        verify.getExpiresAt();// 获取过期时间
    }

}
