package nut.thas.defind.mq;

/**
 * @Author: Yan
 * @date 2019-03-27
 * @Version: 1.0
 */
public interface XProducer<Message extends XMessage> {

    boolean produce(Message message);
}
