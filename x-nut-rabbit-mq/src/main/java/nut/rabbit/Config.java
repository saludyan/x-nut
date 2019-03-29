package nut.rabbit;

import nut.thas.defind.mq.XConfig;

import java.lang.annotation.*;

/**
 * @Author: Yan
 * @date 2019-03-28
 * @Version: 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Config  {
    String name();

    String value();
}
