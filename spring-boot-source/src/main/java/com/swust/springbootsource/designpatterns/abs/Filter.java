package com.swust.springbootsource.designpatterns.abs;

import com.swust.springbootsource.designpatterns.Config;
import com.swust.springbootsource.designpatterns.FilterChain;
import com.swust.springbootsource.designpatterns.Request;
import com.swust.springbootsource.designpatterns.Response;

/**
 * @author : MrLawrenc
 * @date : 2020/5/13 22:35
 * @description : 过滤器抽象接口
 */
public interface Filter {
    /**
     * 初始化方法，整个生命周期中只会被调用一次
     *
     * @param filterConfig
     */
    void init(Config filterConfig);

    /**
     * 执行过滤任务的方法，参数FilterChain表示Filter链，doFilter方法中只有执行FilterChain只有执行了doFilter方法，
     * 才能将请求交经下一个Filter具体的业务方法执行
     *
     * @param request
     * @param response
     * @param chain
     */
    void doFilter(Request request, Response response, FilterChain chain);

    /**
     * 销毁方法，当移出被调用。整个生命周期中destroy方法只会执行一次
     * destroy方法可用于释放持有的资源，如内存、文件句柄等
     */
    void destroy();
}