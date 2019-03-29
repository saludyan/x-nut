package nut.thas.defind.mq;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface XSubscriber {

    String queue();
    String routingKey() default "";
    String exchange() default "";
    boolean autoAck() default false;

}
