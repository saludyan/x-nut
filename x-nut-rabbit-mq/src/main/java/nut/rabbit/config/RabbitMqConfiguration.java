package nut.rabbit.config;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import nut.thas.exceptions.XAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Yan
 * @date 2019-03-27
 * @Version: 1.0
 */
@Configuration
public class RabbitMqConfiguration {


    @Value("${x.nut.rabbitmq.host:}")
    private String host;
    @Value("${x.nut.rabbitmq.port}")
    private Integer port;
    @Value("${x.nut.rabbit.virtualHost:}")
    private String virtualHost;
    @Value("${x.nut.rabbitmq.username:}")
    private String username;
    @Value("${x.nut.rabbitmq.password:}")
    private String password;


    @Bean
    public Connection getConnection() throws Exception {
        // 检查配置
        this.checkConfig();

        //定义连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setVirtualHost(virtualHost);
        factory.setUsername(username);
        factory.setPassword(password);
        // 网络故障自动恢复
        factory.setAutomaticRecoveryEnabled(true);
        Connection connection = factory.newConnection();
        return connection;
    }

    private void checkConfig(){
        XAssert.notEmpty(host,"未检测到到配置:x.nut.rabbitmq.host");
        XAssert.notNull(port,"未检测到到配置:x.nut.rabbitmq.port");
        XAssert.notNull(username,"未检测到到配置:x.nut.rabbitmq.username");
        XAssert.notNull(password,"未检测到到配置:x.nut.rabbitmq.password");
    }

}
