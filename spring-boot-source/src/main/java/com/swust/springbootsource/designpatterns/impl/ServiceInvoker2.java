package com.swust.springbootsource.designpatterns.impl;

import com.swust.springbootsource.designpatterns.Request;
import com.swust.springbootsource.designpatterns.Response;
import com.swust.springbootsource.designpatterns.abs.Invoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : MrLawrenc
 * @date : 2020/5/13 22:48
 * @description : 具体的业务
 */
@Component
@Slf4j
public class ServiceInvoker2 implements Invoker {
    public ServiceInvoker2() {
    }

    @Override
    public Response invoke(Request request) {
        System.out.println("service2 doing....................");
        return null;
    }

}