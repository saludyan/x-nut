package nut.rabbit.example;

import nut.rabbit.Message;
import nut.thas.defind.mq.XProducer;
import nut.thas.defind.mq.XSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 简单模式(direct)
 * 1. [发布者]routingKey 和 [消费者] queue名字一样即可
 * 2. 无需exchange
 * @Author: Yan
 * @date 2019-03-29
 * @Version: 1.0
 */
@Service
public class SimpleExample {

    private final static String ROUTING_KEY="simple.routingKey";

    @Autowired
    private XProducer<Message> producer;

    public void product(){
        for(int i=0;i<10;i++){
            producer.produce(new Message()
                    .setRoutingKey(ROUTING_KEY)
                    .setData("["+(i+1)+"]hello"));
        }
    }

    @XSubscriber(queue = ROUTING_KEY)
    public void subscriber(String message){
        System.out.println(message);
    }
}
