package com.swust.springbootsource.designpatterns;

import com.swust.springbootsource.designpatterns.config.RegisterUtil;
import com.swust.springbootsource.designpatterns.impl.ServiceInvoker;
import com.swust.springbootsource.designpatterns.impl.ServiceInvoker2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.concurrent.TimeUnit;

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

    @Autowired
    private ServiceInvoker invoker1;
    @Autowired
    private ServiceInvoker2 invoker2;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TestDesignApp.class, args);
        TimeUnit.DAYS.sleep(1);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("-------------");
        invoker1.doInvoke(new Request());
       // invoker2.doInvoke(new Request());

    }
}