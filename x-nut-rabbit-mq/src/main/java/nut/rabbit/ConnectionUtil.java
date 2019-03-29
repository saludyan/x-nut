package nut.rabbit;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import nut.thas.exceptions.XException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: Yan
 * @date 2019-03-29
 * @Version: 1.0
 */
public class ConnectionUtil {

    @Autowired
    public ConnectionUtil(ConnectionFactory argConnectionFactory){
        connectionFactory = argConnectionFactory;
    }

    private static ConnectionFactory connectionFactory;

    public static Connection getConnection(){
        try {
            return connectionFactory.newConnection();
        } catch (IOException e) {
            throw new XException(e);
        } catch (TimeoutException e) {
            throw new XException(e);
        }
    }
}
