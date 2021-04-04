package com.gxc.baizi.springbootjwt.controller;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.gxc.baizi.springbootjwt.entity.User;
import com.gxc.baizi.springbootjwt.serivce.UserService;
import com.gxc.baizi.springbootjwt.util.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/user/login")
    public Map<String,Object> login( User user){
        System.out.println(user+"------------------------------");
        log.info("username[{}]",user);
        Map<String,Object> map = new HashMap<>();

        try{
            User login = userService.login(user);


            Map<String,String> payload = new HashMap<>();
            map.put("id", login.getId());
            map.put("name", login.getName());
            String token = JWTUtils.getToken(payload);


            map.put("state", true);
            map.put("msg", "登录成功");
            map.put("token", token);
            return map;
        }catch (Exception e){
            map.put("state", false);
            map.put("msg", "登录失败");
            return map;
        }
    }

@GetMapping("/user/test")
    public Map<String,Object> test(){
    System.out.println("=======fffffffffffffffffffff===============");
    Map<String,Object> map = new HashMap<>();


    map.put("stats", false);
    map.put("msg","user/test请求成功");
    return map;
}

}
