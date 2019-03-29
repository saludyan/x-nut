package nut.rabbit;

import com.rabbitmq.client.BuiltinExchangeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import nut.thas.defind.mq.XMessage;

/**
 * @Author: Yan
 * @date 2019-03-27
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Message<Data> implements XMessage<Data> {

    private String exchange;

    private String queueName;

    private BuiltinExchangeType exchangeType;

    private Data data;
}
