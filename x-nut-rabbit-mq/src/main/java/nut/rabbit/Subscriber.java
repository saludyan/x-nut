package nut.rabbit;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import nut.thas.defind.mq.XData;
import nut.thas.defind.mq.XPrefixNameBuilder;
import nut.thas.defind.mq.XSerializer;
import nut.thas.defind.mq.XSubscriber;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @Author: Yan
 * @date 2019-03-29
 * @Version: 1.0
 */
@Slf4j
@Data
@AllArgsConstructor
public class Subscriber {

    private Object object;
    private List<SubscriberMethod> subscriberMethods;

    @Data
    @AllArgsConstructor
    public static class SubscriberMethod{
        private Method method;
        private XSubscriber subscriber;
    }

    public void subscribe(ExecutorService executorService){
        if(CollUtil.isEmpty(subscriberMethods)){
            return;
        }

        for (Subscriber.SubscriberMethod subscriberMethod : subscriberMethods) {
            executorService.execute(()->{
                try{
                    Connection connection = ConnectionUtil.getConnection();
                    Channel channel = connection.createChannel();
                    Method method = subscriberMethod.getMethod();
                    XSubscriber xSubscriber = subscriberMethod.getSubscriber();
                    String queueName = xSubscriber.queue();
                    String exchange = xSubscriber.exchange();
                    String routingKey= xSubscriber.routingKey();

                    String processExchangeName = XPrefixNameBuilder.build(exchange);
                    boolean autoAck = xSubscriber.autoAck();
                    boolean durable = true;
                    boolean exclusive = false;
                    boolean autoDelete = false;
                    Map<String, Object> arguments = null;
                    channel.queueDeclare(queueName, durable, exclusive, autoDelete, arguments);

                    //同一时刻服务器只发送一条消息给消费端
                    channel.basicQos(1);
                    if(StrUtil.isNotBlank(exchange)){
                        channel.queueBind(queueName, processExchangeName, routingKey);
                    }
                    while(true){
                        channel.basicConsume(queueName, autoAck, new DefaultConsumer(channel) {
                            @Override
                            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] consumerBody)
                                    throws IOException {
                                String routingKey = envelope.getRoutingKey();
                                Long deliveryTagId = envelope.getDeliveryTag();
                                XData xData = XSerializer.deserialize(consumerBody);
                                try {
                                    method.invoke(object, xData.getOriginData());
                                    // 确认消费
                                    channel.basicAck(deliveryTagId, false);
                                } catch (IllegalAccessException e) {
                                    log.error("call method error:",e);
                                } catch (InvocationTargetException e) {
                                    log.error("call method error:",e);
                                }

                            }

                            @Override
                            public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                                log.trace("Channel&Connection closed...");
                            }
                        });
                    }

                } catch (IOException e) {
                    log.error("Consume error:",e);
                }


            });
        }



    }
}
