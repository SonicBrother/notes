package com.gxc.baizi.springbootjwt.config;

import com.gxc.baizi.springbootjwt.intercepter.JWTintecepter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashSet;

@Configuration
public class IntercepterConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTintecepter())
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login");
        System.out.println("拦截器配置中");

        new HashSet<>();
    }
}
