package com.baizhi.hello;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
// queuesToDeclare 没有队列就声明队列  默认创建出的队列：持久化 非独占 不是自动删除的
@RabbitListener(queuesToDeclare = @Queue(value = "hello")) // 消费者队列监听   可以加在类上  可以加在方法上
//@RabbitListener(queuesToDeclare = @Queue(value = "hello",durable = "false",autoDelete = "false"))
public class HelloCustomer {

    @RabbitHandler
    public void receive1(String message){
        System.out.println("message = " + message);
    }


}
