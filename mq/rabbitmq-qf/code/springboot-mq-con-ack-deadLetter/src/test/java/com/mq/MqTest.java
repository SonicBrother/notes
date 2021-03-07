package com.mq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by 54110 on 2019/11/7.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MqTest {


    @Autowired
    RabbitTemplate rabbitTemplate;
    @Test
   @RabbitListener(queues = "1905-queue")
    public void testSend(String str){
       System.out.println("接受到的参数为:================================"+str);
    }
}
