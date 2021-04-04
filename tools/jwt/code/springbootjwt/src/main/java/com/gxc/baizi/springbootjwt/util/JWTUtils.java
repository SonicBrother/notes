package com.gxc.baizi.springbootjwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

public class JWTUtils {
    private static final String SINGER = "111";

    // 生成token
    public static String getToken(Map<String, String> map) {

        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, 7);

        JWTCreator.Builder builder = JWT.create();
        map.forEach((k, v) -> {
            builder.withClaim(k, v);
        });
        String token = builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC256(SINGER));
        System.out.println(token);
        return token;
    }

    // 验证token
    // 如果有异常则抛出
    // 该方法同时可以提供相关信息
    public static DecodedJWT verity(String token) {
        return JWT.require(Algorithm.HMAC256(SINGER)).build().verify(token);
    }

}
