package nut.rabbit.example;

import com.rabbitmq.client.BuiltinExchangeType;
import nut.rabbit.Message;
import nut.thas.defind.mq.XProducer;
import nut.thas.defind.mq.XSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Yan
 * @date 2019-04-01
 * @Version: 1.0
 */
@Service
public class TopicExample {

    private final static String EXCHANGE="example.topic.exchange";
    private final static String ROUTING_KEY="animal.dog";

    @Autowired
    private XProducer<Message> producer;

    public void product(){
        for(int i=0;i<10;i++){
            producer.produce(new Message()
                    .setExchange(EXCHANGE)
                    .setRoutingKey(ROUTING_KEY)
                    .setExchangeType(BuiltinExchangeType.TOPIC)
                    .setData("["+(i+1)+"]hello"));
        }
    }

    /**
     * 消费者1
     * @param message
     */
    @XSubscriber(
            exchange = EXCHANGE,
            queue = "example.topic.subscriber1",
            routingKey = "animal.dog.#"
    )
    public void subscriber1(String message){
        System.out.println("subscriber1:"+message);
    }

    /**
     * (这个是无法消费的)
     * 消费者2
     * @param message
     */
    @XSubscriber(
            exchange = EXCHANGE,
            queue = "example.topic.subscriber2",
            routingKey = "animal.cat.#"
    )
    public void subscriber2(String message){
        System.out.println("subscriber2:"+message);
    }
}
