package com.mq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 54110 on 2019/11/7.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MqTest {


    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void testSend(){

            rabbitTemplate.convertAndSend("1905-topic","niubi","发送给不牛逼的人",messagePostProcessor);

    }

    private final static MessagePostProcessor messagePostProcessor = message -> {
        message.getMessageProperties().setContentType("text/plain");
        message.getMessageProperties().setContentEncoding("UTF-8");
        message.getMessageProperties().setHeader("error","这是一条错误信息");
        return message;
    };

    @Test
    public  void testDeadLine() {

    for (int i =0 ;i<=5;i++){

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        rabbitTemplate.convertAndSend("demoTtlExchange", "demoTtlRoutes", "测i试消息"+i, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //设置10s过期，过期转发到指定路由
                message.getMessageProperties().setExpiration("60000");

                return message;
            }
        });
        System.out.println("当前发送时间为===="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
         }

    }
}
