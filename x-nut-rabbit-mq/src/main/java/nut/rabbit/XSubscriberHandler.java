package nut.rabbit;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import nut.thas.defind.mq.XSubscriber;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Super Yan on 2018/8/6.
 */
@Slf4j
public class XSubscriberHandler implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware, DisposableBean {


    private ApplicationContext applicationContext;

    @Value("${app.rabbit.retryMax:3}")
    private Integer retryMax;


    private static List<Subscriber> subscribers = new ArrayList<>(20);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initSubscribers();
        
        processSubscribers();
    }

    private void processSubscribers() {
            if(CollUtil.isEmpty(subscribers)){
                return;
            }

            ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                    60L, TimeUnit.SECONDS,
                    new SynchronousQueue<>(), r -> {
                        Thread t =new Thread(r, "RabbitMQSubscriberThread");
                        t.setUncaughtExceptionHandler((t1, e) ->
                                log.error("RabbitMQSubscriberThread exception:",e)
                        );
                        return t;
                    });

            for (Subscriber subscriber : subscribers) {
                    subscriber.subscribe(executorService);
            }
    }

    private void initSubscribers() {
        for (String beanDefinitionName : applicationContext.getBeanDefinitionNames()) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            Method[] methods = bean.getClass().getDeclaredMethods();
            if (ArrayUtil.isEmpty(methods)) {
                continue;
            }
            List<Method> matchMethod = null;
            for (Method method : methods) {
                if (method.getAnnotation(XSubscriber.class) != null) {
                    matchMethod = matchMethod == null ? new ArrayList<>(1) : matchMethod;
                    matchMethod.add(method);
                }
            }

            // 过滤掉父类的方法
            for (int i = 0; i < matchMethod.size(); i++) {
                matchMethod.set(i, AopUtils.getMostSpecificMethod(matchMethod.get(i), targetClass));
            }

            if(CollUtil.isEmpty(matchMethod)){
                continue;
            }
            List<Subscriber.SubscriberMethod> subscriberMethods = new ArrayList<>(matchMethod.size());
            for (Method method : matchMethod) {
                XSubscriber xSubscriber = method.getAnnotation(XSubscriber.class);
                subscriberMethods.add(new Subscriber.SubscriberMethod(method,xSubscriber));
            }

            subscribers.add(new Subscriber(bean,subscriberMethods));

        }
    }

    @Override
    public void destroy()  {

    }


}
