package com.baizhi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RabbitmqSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitmqSpringbootApplication.class, args);
	}

}

// 队列由消费着创建
