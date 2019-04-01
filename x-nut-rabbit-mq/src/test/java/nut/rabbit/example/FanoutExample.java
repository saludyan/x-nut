package nut.rabbit.example;

import org.springframework.stereotype.Service;

/**
 * Fanout模式
 * 每个发到 fanout 类型交换器的消息都会分到所有绑定的队列上去。
 * fanout 交换器不处理路由键，只是简单的将队列绑定到交换器上，每个发送到交换器的消息都会被转发到与该交换器绑定的所有队列上。
 * 很像子网广播，每台子网内的主机都获得了一份复制的消息。 fanout 类型转发消息是最快的 。
 * @Author: Yan
 * @date 2019-03-29
 * @Version: 1.0
 */
@Service
public class FanoutExample {
}
