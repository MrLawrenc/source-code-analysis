package com.swust.springbootsource.initializer;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/10 11:38
 * @description : 系统初始化器
 */
@Order(20)
public class MyInitializer1 implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        //可以在容器初始化之前注入一些环境变量
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        // 设置必备属性，常被提供服务方使用，如mybatis-start需要整合boot，则可以配置数据库连接等必备属性
        environment.setRequiredProperties("mustKey");


        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");
        MapPropertySource mapPropertySource = new MapPropertySource("myMapProperties", map);
        environment.getPropertySources().addLast(mapPropertySource);
        System.out.println("MyInitializer1.....................");
    }

}