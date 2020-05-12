package com.swust.springbootsource.initializer;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/10 14:24
 * @description : TODO
 */
@Component
public class MySpring implements ApplicationContextAware {
    @Getter
    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}