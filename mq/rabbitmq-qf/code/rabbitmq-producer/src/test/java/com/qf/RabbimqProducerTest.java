package com.qf;

import com.qf.bean.UserVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by 54110 on 2020/9/7.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbimqProducerTest {

    @Autowired
    RabbitTemplate rabbitTemplate;


    @Test
    public void testFirstSend(){
        //work-queue模式。1.队列的名称。2.消息的主体
        for (int i =0;i<10;i++){
            rabbitTemplate.convertAndSend("work-queue-0306","hello rabbitmq"+i);
        }
    }

    // 有问题
    @Test
    public void testFirstSendVO(){
        //work-queue模式。1.队列的名称。2.消息的主体
        UserVO userVO = new UserVO(1,"aa");
            rabbitTemplate.convertAndSend("work-queue-vo",userVO);
    }

    @Test
    public void testPublish(){
        //publish-subcrib 发布订阅
        //1.当前交换机的名称。2.规则 发布订阅模式设置为“”。3.内容的主体
        for (int i =0;i<10;i++){
        rabbitTemplate.convertAndSend("public-subcrib","","你好么，我很好"+i);
        }
    }

    @Test
    public void testRouting(){
        //routingkey模式
        //1.交换机的名称。2.当前队列绑定到交换机的规则。3.内容的主体
       // rabbitTemplate.convertAndSend("direct-exchange","info","hello info 消息");
        rabbitTemplate.convertAndSend("direct-exchange","error","hello error 消息");
    }

    @Test
    public void testTopic(){
        rabbitTemplate.convertAndSend("topic-exchange","send.sms.111.2.3.3.4","这是一条发送短信的消息");
        rabbitTemplate.convertAndSend("topic-exchange","send.email.111.2.3.3.4","这是一条发送邮箱的消息");
       // rabbitTemplate.convertAndSend("topic-exchange","send.email","这是一条发送短信的消息");
    }
}
