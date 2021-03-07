package com.mq.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @Description:通过实现ReturnCallback接口
 * 如果消息从交换器发送到对应队列失败时触发（比如根据发送消息时指定的routingKey找不到队列时会触发）
 */
public class ReturnCallBackHandler implements RabbitTemplate.ReturnCallback {
    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        System.out.println("消息主体 message："+message);
        System.out.println("应答码 replyCode: ："+i);
        System.out.println("原因描述 replyText："+s);
        System.out.println("交换机 exchange："+s1);
        System.out.println("消息使用的路由键 routingKey："+s2);
    }
}
