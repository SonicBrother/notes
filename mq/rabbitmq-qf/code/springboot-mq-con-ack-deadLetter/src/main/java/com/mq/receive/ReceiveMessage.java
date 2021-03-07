package com.mq.receive;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * Created by 54110 on 2019/11/7.
 */
@Component
public class ReceiveMessage {
    @RabbitListener(queues = "hello-rabbit")
    public void listen(String msg, Channel channel, Message message) {
        try {
            System.out.println(msg);
//            int i = 1/0;
            // 手动ack回复，标注当前消息已经被正确消费
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {

            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println(e);
        }
    }

    @RabbitListener(queues = "tocQue")
    public void listenTocQue(String msg, Channel channel, Message message) {
        try {
            System.out.println(msg + "   " + new Date());
//            int i = 1/0;
            // 手动ack回复，标注当前消息已经被正确消费
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {

            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println(e);
        }
    }


}
