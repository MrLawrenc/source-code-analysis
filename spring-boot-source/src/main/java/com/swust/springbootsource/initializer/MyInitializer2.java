package com.swust.springbootsource.initializer;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/10 11:38
 * @description : 系统初始化器
 */
@Order(10)
public class MyInitializer2 implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        System.out.println("MyInitializer2.....................");
    }
}