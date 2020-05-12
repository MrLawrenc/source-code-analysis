package com.swust.springbootsource.event.listener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/9 10:17
 * @description : 通过配置spring 提供的spi机制，即配置spring.factories文件装载 runner listener（用于容器启动阶段的监听）
 * 这些监听方法会在容器初始化各个阶段被调用，可以在{@linkplain org.springframework.boot.context.event.EventPublishingRunListener}
 * 查看构建了哪些事件
 * <br>
 * 或者也可以使用如下方法监听容器各阶段产生的事件,如监听准备启动阶段事件
 * <pre>
 *     @EventListener(classes = {ApplicationStartingEvent.class})
 *     public void moreEventListener(ApplicationStartingEvent e) {
 *         // TODO
 *     }
 * </pre>
 */
public class MyListener4SpringFactoryConfig implements SpringApplicationRunListener, Ordered {
    public MyListener4SpringFactoryConfig(SpringApplication application, String[] args) {
        System.out.println("construct...........");
    }

    @Override
    public int getOrder() {
        //spring默认配置的EventPublishingRunListener类是返回0
        return 1;
    }

    @Override
    public void starting() {
        System.out.println("MyListener4SpringFactoryConfig ===> starting");
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        Object pid = environment.getPropertySources().get("systemProperties").getProperty("PID");
        System.out.println("进程号:" + pid);
        System.out.println("MyListener4SpringFactoryConfig ===> environmentPrepared");
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        System.out.println("MyListener4SpringFactoryConfig ===> contextPrepared");
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        System.out.println("MyListener4SpringFactoryConfig ===> contextLoaded");

    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        System.out.println("MyListener4SpringFactoryConfig ===> started");

    }

    @Override
    public void running(ConfigurableApplicationContext context) {
        System.out.println("MyListener4SpringFactoryConfig ===> running");

    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        System.out.println("MyListener4SpringFactoryConfig ===> failed");

    }
}