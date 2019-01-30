package nut.jpa.component;

import cn.hutool.core.util.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.util.concurrent.TimeUnit;

public class SequenceProvider {

    @Autowired
    private RedisTemplate redisTemplate;


    public String generate(String prefix){


        return this.generate(prefix,5,2, TimeUnit.DAYS);
    }

    public String generate(String prefix,int length){
        return this.generate(prefix,length,1,TimeUnit.DAYS);
    }

    public String generate(String prefix,int length,long timeout,TimeUnit timeUnit){
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(prefix, redisTemplate.getConnectionFactory());
        Long increment = entityIdCounter.getAndIncrement();


        String format = "";
        for (int i = 0; i < length; i++) {
            format += "0";
        }


        entityIdCounter.expire(timeout, timeUnit);

        return prefix+ NumberUtil.decimalFormat(format,increment);
    }
}
