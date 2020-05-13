package com.swust.springbootsource.designpatterns.impl;

import com.swust.springbootsource.designpatterns.Config;
import com.swust.springbootsource.designpatterns.FilterChain;
import com.swust.springbootsource.designpatterns.Request;
import com.swust.springbootsource.designpatterns.Response;
import com.swust.springbootsource.designpatterns.abs.Filter;
import org.springframework.core.Ordered;

/**
 * @author : MrLawrenc
 * @date : 2020/5/13 22:46
 * @description : 具体的过滤器
 */
public class Filter2 implements Filter , Ordered {
    @Override
    public void init(Config filterConfig) {
        System.out.println("过滤器2 init...........");
    }

    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {
        System.out.println("过滤器2 doFilter...........");
    }

    @Override
    public void destroy() {
        System.out.println("过滤器2 destroy...........");
    }

    @Override
    public int getOrder() {
        return 2;
    }
}