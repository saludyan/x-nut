package nut.jpa.component;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AliasFor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ComponentScan
@EnableJpaRepositories
@EntityScan
@EnableScheduling
@SpringBootApplication
public @interface XApplication {


    @AliasFor("basePackages")
    String[] value() default {"com.x.*"};

    @AliasFor("value")
    String[] basePackages() default {"com.x.*"};
}
