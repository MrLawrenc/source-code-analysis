package com.swust.springbootsource.designpatterns.impl;

import com.swust.springbootsource.designpatterns.Config;
import com.swust.springbootsource.designpatterns.FilterChain;
import com.swust.springbootsource.designpatterns.Request;
import com.swust.springbootsource.designpatterns.Response;
import com.swust.springbootsource.designpatterns.abs.Filter;
import org.springframework.stereotype.Component;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/14 9:42
 * @description : 对于入站来说是第一个filter，对于出站则是最后一个filter
 * <p>
 * 留给子类扩展，默认空实现
 */
@Component
public class FirstFilter implements Filter {
    @Override
    public void init(Config filterConfig) {

    }

    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {

    }

    @Override
    public void destroy() {

    }
}