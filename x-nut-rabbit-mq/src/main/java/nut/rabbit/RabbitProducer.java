package nut.rabbit;

import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import nut.thas.defind.mq.XData;
import nut.thas.defind.mq.XPrefixNameBuilder;
import nut.thas.defind.mq.XProducer;
import nut.thas.defind.mq.XSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * RabbitMQ 消息生产者
 * <p>
 * 推荐阅读: https://blog.csdn.net/wangbing25307/article/details/80845641
 *
 * @Author: Yan
 * @date 2019-03-27
 * @Version: 1.0
 */
@Slf4j
public class RabbitProducer implements XProducer<Message> {


    @Override
    public boolean produce(Message message) {
        Channel channel = null;
        Connection connection = ConnectionUtil.getConnection();
        try {


            channel = connection.createChannel();
            //声明交换器
            String exchange = message.getExchange();
            String routingKey = message.getRoutingKey();

            String processExchangeName = XPrefixNameBuilder.build(exchange);
            XData xData = new XData(message.getData());
            // 简单模式: 只有队列名称,忽略交换器
            if (StrUtil.isBlank(exchange)) {
                // 往后版本抽出这些具体配置

                // if we are declaring a durable queue (the queue will survive a server restart)
                // 如果我们声明一个持久的队列（队列将在服务器重启后继续存在）
                boolean durable = true;
                // if we are declaring an exclusive queue (restricted to this connection)
                // 如果我们声明一个独占队列（仅限于此连接）
                // 是否排外的，有两个作用，一：当连接关闭时connection.close()该队列是否会自动删除；二：该队列是否是私有的private，如果不是排外的，可以使用两个消费者都访问同一个队列，没有任何问题，如果是排外的，会对当前队列加锁，其他通道channel是不能访问的，如果强制访问会报异常
                boolean exclusive = false;
                // autoDelete true if we are declaring an autodelete queue (server will delete it when no longer in use)
                // 如果我们声明一个自动删除队列（服务器将在不再使用时删除它）
                boolean autoDelete = false;
                // arguments other properties (construction arguments) for the queue
                // 队列的其他属性（构造参数）
                // 配置项较多...这里省略...
                Map<String, Object> arguments = null;
                channel.queueDeclare(routingKey, durable, exclusive, autoDelete, arguments);
            }
            // 交换器模式
            else {
                // 往后版本抽出这些具体配置

                // 交换器名称
                String exchangeName = processExchangeName;
                // region


                /* fanout
                 * 交换器类型
                 * 任何发送到Fanout
                 * 任何发送到Fanout Exchange的消息都会被转发到与该Exchange绑定(Binding)的所有Queue上。
                 *
                 * 1.可以理解为路由表的模式
                 * 2.这种模式不需要RouteKey
                 * 3.这种模式需要提前将Exchange与Queue进行绑定，一个Exchange可以绑定多个Queue，一个Queue可以同多个Exchange进行绑定。
                 * 4.如果接受到消息的Exchange没有与任何Queue绑定，则消息会被抛弃。

                 * direct
                 * 任何发送到Direct Exchange的消息都会被转发到RouteKey中指定的Queue。
                 *
                 * 1.一般情况可以使用rabbitMQ自带的Exchange：””(该Exchange的名字为空字符串，下文称其为default Exchange)。
                 * 2.这种模式下不需要将Exchange进行任何绑定(binding)操作
                 * 3.消息传递时需要一个“RouteKey”，可以简单的理解为要发送到的队列名字。
                 * 4.如果vhost中不存在RouteKey中指定的队列名，则该消息会被抛弃。
                 *
                 * topic
                 * 任何发送到Topic Exchange的消息都会被转发到所有关心RouteKey中指定话题的Queue上
                 *
                 * 1.这种模式较为复杂，简单来说，就是每个队列都有其关心的主题，所有的消息都带有一个“标题”(RouteKey)，Exchange会将消息转发到所有关注主题能与RouteKey模糊匹配的队列。
                 * 2.这种模式需要RouteKey，也许要提前绑定Exchange与Queue。
                 * 3.在进行绑定时，要提供一个该队列关心的主题，如“#.log.#”表示该队列关心所有涉及log的消息(一个RouteKey为”MQ.log.error”的消息会被转发到该队列)。
                 * 4.“#”表示0个或若干个关键字，“”表示一个关键字。如“log.”能与“log.warn”匹配，无法与“log.warn.timeout”匹配；但是“log.#”能与上述两者匹配。
                 * 5.同样，如果Exchange没有发现能够与RouteKey匹配的Queue，则会抛弃此消息。
                 */
                // endregion
                BuiltinExchangeType exchangeType = message.getExchangeType();
                if(exchangeType == null){
                    exchangeType = BuiltinExchangeType.DIRECT;
                }
                // 同上
                boolean durable = false;
                // 同上
                boolean autoDelete = false;
                // if the exchange is internal, i.e. can't be directly
                // 是否内置,如果设置 为true,则表示是内置的交换器,客户端程序无法直接发送消息到这个交换器中,只能通过交换器路由到交换器的方式
                boolean internal = false;
                // 配置项较多...这里省略...
                Map<String, Object> arguments = null;
                channel.exchangeDeclare(exchangeName, exchangeType.getType(), durable, autoDelete, internal, arguments);
            }

            // mandatory：
            // true：如果exchange根据自身类型和消息routeKey无法找到一个符合条件的queue，那么会调用basic.return方法将消息返还给生产者。
            // false：出现上述情形broker会直接将消息扔掉
            boolean mandatory = false;
            // BasicProperties ：
            // 需要注意的是BasicProperties.deliveryMode，
            // 0:不持久化
            // 1：持久化 这里指的是消息的持久化，配合channel(durable=true),queue(durable)可以实现，即使服务器宕机，消息仍然保留
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().deliveryMode(1).build();
            channel.basicPublish(processExchangeName, routingKey==null?"":routingKey, mandatory, props, XSerializer.serialize(xData));
            log.info("[RabbitProducer] Sent:{}", xData.toJson());

            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            try {
                if (channel.isOpen()) {
                    channel.close();
                }
            } catch (Exception e) {
                log.error("channel无法关闭:", e);
            }
            try {
                connection.close();
            } catch (IOException e) {
                log.error("connection无法关闭:", e);
            }
            // 无法关闭,可能发送已经成功
            return true;
        }

    }
}
