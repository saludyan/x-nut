package nut.rabbit;

import nut.rabbit.example.FanoutExample;
import nut.rabbit.example.TopicExample;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Yan
 * @date 2019-03-29
 * @Version: 1.0
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class TopicExampleTest {

    @Autowired
    private TopicExample topicExample;

    @Test
    public void product() throws InterruptedException {
        topicExample.product();
        TimeUnit.SECONDS.sleep(10);
    }
}
