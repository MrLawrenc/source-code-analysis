package com.swust.springbootsource.initializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MutablePropertySources;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/9 10:38
 * @description : 阅读spring boot源码的启动类
 * <p>
 * pom引入了web flux的包，则spring boot环境会切换到reactive模式（没有则会判断是否是否是web环境 是的话切换到servlet模式，不是则使用annotationContext）
 * <br>
 * 详见{@linkplain org.springframework.boot.WebApplicationType}
 */
@SpringBootApplication
public class InitializerTest implements ApplicationRunner {
    @Autowired
    private MySpring mySpring;

    public static void main(String[] args) {

       // SpringApplication.run(InitializerTest.class);
        /*
         * 可以使用如下方式启动spring
         * 传入当前class，spring boot在创建容器之后会将此类注入容器
         */
        SpringApplication application = new SpringApplication(InitializerTest.class);
        application.addInitializers(new MyInitializer2());
        application.run(args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        MutablePropertySources propertySources = ((ConfigurableApplicationContext) mySpring.getContext()).getEnvironment().getPropertySources();

        System.out.println("propertySources "+propertySources);

        System.out.println(mySpring.getContext().getEnvironment().getProperty("key"));

    }
}