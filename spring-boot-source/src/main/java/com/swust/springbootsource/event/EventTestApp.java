package com.swust.springbootsource.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/4/15 9:44
 * @description : main
 */
@SpringBootApplication
@EnableAsync
public class EventTestApp implements ApplicationRunner {
    @Autowired
    ServiceImpl service;

    public static void main(String[] args) {
        SpringApplication.run(EventTestApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        service.service();
    }
}