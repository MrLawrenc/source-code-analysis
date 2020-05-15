package com.swust.springbootsource.designpatterns.listener;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/14 15:27
 * @description : TODO
 */
@Component
public class ContextListener implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {

    }
}