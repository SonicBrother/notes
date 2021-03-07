package com.mq.controller;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("send/{msg}")
    public String send(@PathVariable("msg")String msg) {
        rabbitTemplate.convertAndSend("hello-rabbit",msg);
        return "success";
    }

    @GetMapping("sendToc/{msg}")
    public String sendToc(@PathVariable("msg")String msg) {
        rabbitTemplate.convertAndSend("tocExc","123",msg);
        return "success";
    }

 // 测试死信队列
    @GetMapping("sendDead/{msg}")
    public String sendDead(@PathVariable("msg")String msg) {
        rabbitTemplate.convertAndSend("deadTop", "456", msg, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration("5000");
                return message;
            }
        });
        return "success";
    }
}
