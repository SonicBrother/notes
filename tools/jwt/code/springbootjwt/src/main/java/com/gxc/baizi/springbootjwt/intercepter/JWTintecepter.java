package com.gxc.baizi.springbootjwt.intercepter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gxc.baizi.springbootjwt.entity.User;
import com.gxc.baizi.springbootjwt.util.JWTUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 拦截器  单体式
 * HandlerInterceptor springmvc提供
 */
public class JWTintecepter  implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("token");
        Map<String,Object> map = new HashMap<>();

        try{
            DecodedJWT verity = JWTUtils.verity(token);
            System.out.println("JWTintecepter 认证成功");
            return true;
        }catch (Exception e){

        }
        System.out.println("JWTintecepter 认证失败");
        map.put("statu", false);
        String s = new ObjectMapper().writeValueAsString(map);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(s);
        // responsbody 底层 jackson工具
        return false;
    }
}
