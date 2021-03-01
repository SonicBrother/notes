package com.baizhi.topic;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TopicCustomer {

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(type = "topic",name="topics"),
                    key={"user.save","user.*"}
            )
    })
    public void receive1(String message){
        System.out.println("message1 = " + message);
    }



    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(type = "topic",name="topics"),
                    key={"order.#","product.#","user.*"}
            )
    })
    public void receive2(String message){
        System.out.println("message2 = " + message);
    }




    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(type = "topic",name = "topics"),
                    key = {"1"}
            )
    })
    public void receive3(String  message){
        System.out.println(message+"11111111111111111111");
    }
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(type = "topic",name = "topics"),
                    key = {"2"}
            )
    })
    public void receive4(String  message){
        System.out.println(message+"222222222222222222222222");
    }


}
