package com.qf.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by 54110 on 2020/9/7.
 */
@Configuration
public class RabbitMqConfig {

    //工作队列模式
    @Bean
    public Queue queue(){
        return new Queue("work-queue-0306");
    }

    //工作队列模式
    @Bean
    public Queue queuevo(){
        return new Queue("work-queue-vo");
    }


  /*  //发布订阅模式
    //定义三个队列
    @Bean(name = "queue1")
    public Queue queue1(){
        return new Queue("fanout-1");
    }

    @Bean(name = "queue2")
    public Queue queue2(){
        return new Queue("fanout-2");
    }

    @Bean(name = "queue3")
    public Queue queue3(){
        return new Queue("fanout-3");
    }
    //定义交换机
    // FanoutExchange 发布订阅模式
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("public-subcrib");
    }

    @Bean
    public Binding fanoutExchangeToqueue1(FanoutExchange fanoutExchange, @Qualifier("queue1") Queue queue){
       return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    @Bean
    public Binding fanoutExchangeToqueue2(FanoutExchange fanoutExchange, @Qualifier("queue2") Queue queue){
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }
    @Bean
    public Binding fanoutExchangeToqueue3(FanoutExchange fanoutExchange, @Qualifier("queue3") Queue queue){
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }*/
    //routing模式
   /* @Bean(name = "queue1")
    public Queue queue1(){
        return new Queue("routing-info");
    }

    @Bean(name = "queue2")
    public Queue queue(){
        return new Queue("routing-error");
    }

    //声明交换机
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("direct-exchange");
    }

    @Bean
    public Binding routingInfoTodirectExchange(@Qualifier("queue1")Queue queue,DirectExchange directExchange){
        return BindingBuilder.bind(queue).to(directExchange).with("info");
    }

    @Bean
    public Binding routing2InfoTodirectExchange(@Qualifier("queue2")Queue queue,DirectExchange directExchange){
        return BindingBuilder.bind(queue).to(directExchange).with("error");
    }*/
    //topic 通配符模式
//
//    @Bean(name = "queue1")
//    public Queue queue1(){
//        return new Queue("topic-queue1");
//    }
//    @Bean(name = "queue2")
//    public Queue queue2(){
//        return new Queue("topic-queue2");
//    }
//
//    //TopicExchange 通配符模式的交换机
//    @Bean
//    public TopicExchange topicExchange(){
//        return new TopicExchange("topic-exchange");
//    }
//    //将队列绑定到交换机上
//    @Bean
//    public Binding bindQueueToTopicExchange(@Qualifier("queue1")Queue queue,TopicExchange topicExchange){
//        //* 代表一个词语，#代表零个或多个词语
//        return BindingBuilder.bind(queue).to(topicExchange).with("send.sms.#");
//    }
//    @Bean
//    public Binding bindQueue2ToTopicExchange(@Qualifier("queue2")Queue queue,TopicExchange topicExchange){
//        //* 代表一个词语，#代表零个或多个词语
//        return BindingBuilder.bind(queue).to(topicExchange).with("send.email.#");
//    }
}
