package com.mq.receive;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 54110 on 2019/11/7.
 */
//@Configuration
//@RabbitListener(queues = "demoQueue")
public class ReceiveDeadMessage {


    @RabbitHandler
    private void  receiverMessage(String message){
        System.out.println("当前接收时间为===="+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        System.out.println("message = "+message);
    }


}
