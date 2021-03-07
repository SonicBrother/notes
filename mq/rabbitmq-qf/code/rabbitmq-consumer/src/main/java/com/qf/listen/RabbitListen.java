package com.qf.listen;

import com.qf.bean.UserVO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created by 54110 on 2020/9/7.
 */
@Component
public class RabbitListen {

    @RabbitListener(queues = "work-queue-0306")
    public void messageListen(String msg){
       // int i = 1/0;
        System.out.println(msg);
    }

    @RabbitListener(queues = "work-queue-vo")
    public void messageListenvo(UserVO msg){
        // int i = 1/0;
        System.out.println(msg);
    }

//    @RabbitListener(queues = "fanout-1")
//    public void messageListenPubish(String msg){
//        // int i = 1/0;
//        System.out.println(msg);
//    }

//    @RabbitListener(queues = "routing-info")
//    public void messageListenRoutingInfo(String msg){
//        System.out.println(msg);
//    }
//
//    @RabbitListener(queues = "routing-error")
//    public void messageListenRoutingError(String msg){
//        System.out.println(msg);
//    }
//    @RabbitListener(queues = "topic-queue1")
//    public void messageListenTopicSms(String msg){
//        System.out.println(msg);
//    }
//    @RabbitListener(queues = "topic-queue2")
//    public void messageListenTopicEmail(String msg){
//        System.out.println(msg);
//    }
}
