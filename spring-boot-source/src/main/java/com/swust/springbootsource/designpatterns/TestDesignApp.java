package com.swust.springbootsource.designpatterns;

import com.swust.springbootsource.designpatterns.config.RegisterUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author : MrLawrenc
 * @date : 2020/5/13 23:29
 * @description : TODO
 */
@SpringBootApplication
@Import(RegisterUtil.class)
public class TestDesignApp {

    public static void main(String[] args) {
        SpringApplication.run(TestDesignApp.class, args);
    }
}