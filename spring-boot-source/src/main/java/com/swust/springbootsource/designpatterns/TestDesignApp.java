package com.swust.springbootsource.designpatterns;

import com.swust.springbootsource.designpatterns.config.RegisterUtil;
import com.swust.springbootsource.designpatterns.impl.ServiceInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

/**
 * @author : MrLawrenc
 * @date : 2020/5/13 23:29
 * @description : TODO
 */
@SpringBootApplication
@Import(RegisterUtil.class)
public class TestDesignApp implements ApplicationRunner {

    @Autowired
    private ApplicationContext context;

//    @Autowired
//    private ServiceInvoker invoker;

    public static void main(String[] args) {
        SpringApplication.run(TestDesignApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("实际拿到的bean地址:"+context.getBean(ServiceInvoker.class).hashCode());
    }
}