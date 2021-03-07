package com.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 54110 on 2019/11/7.
 */
@Configuration
public class RabbitMqConfig {

    // 测试ack手动确认消费
    @Bean("siQue")
    public Queue simpleQueue(){
        return new Queue("hello-rabbit");
    }

    // 测试生产者手动确认机制
    @Bean("tocQue")
    public Queue tocQue() {
        return new Queue("tocQue");
    }
    @Bean("tocExchange")
    public TopicExchange topicExchange(){
        return new TopicExchange("tocExc");
    }

    @Bean
    public Binding tocBinding(@Qualifier("tocQue")Queue queue,@Qualifier("tocExchange")TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("123");
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @PostConstruct
    void rabbitTemplate(){
        //消息发送确认，发送到交换器Exchange后触发回调
        rabbitTemplate.setConfirmCallback(new ConfirmCallBackHandler());
        //消息发送确认，如果消息从交换器发送到对应队列失败时触发（比如根据发送消息时指定的routingKey找不到队列时会触发）
        rabbitTemplate.setReturnCallback(new ReturnCallBackHandler());
    }

    // 测试死信队列

    @Bean("dead-queue")
    public Queue deadQue() {
        Map<String, Object> map = new HashMap();
        /* arguments.put("x-dead-letter-exchange", "demoExchange");
        arguments.put("x-dead-letter-routing-key", "demoRoutes");*/
        map.put("x-dead-letter-exchange", "tocExc");
        map.put("x-dead-letter-routing-key", "123");
        return new Queue("dead-queue",true,false,false,map);
    }

    @Bean("deadTop")
    public TopicExchange deadTopicExchange(){
        return new TopicExchange("deadTop");
    }

    @Bean
    public Binding binding(@Qualifier("dead-queue")Queue queue,@Qualifier("deadTop")TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("456");
    }

}
