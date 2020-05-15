package com.swust.springbootsource.designpatterns.config;

import com.swust.springbootsource.designpatterns.FilterChain;
import com.swust.springbootsource.designpatterns.Request;
import com.swust.springbootsource.designpatterns.abs.Invoker;
import com.swust.springbootsource.designpatterns.impl.ServiceInvoker;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.core.NamingPolicy;
import org.springframework.cglib.core.Predicate;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Random;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/14 9:45
 * @description : TODO
 * BeanPostProcessor不是指bean的初始化，而是指的在bean的属性初始化前后处理
 * 因此这儿使用FactoryBean实现
 */
@Component
public class BeanPostProcessorConfig implements BeanPostProcessor {
    @Autowired
    private FilterChain filterChain;
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass() == ServiceInvoker.class) {
            System.out.println("源bean的地址:"+bean.hashCode());
            return proxyInvoker((Class<? extends Invoker>) (bean.getClass()));
        }
        return bean;
    }

    /**
     * 为所有的Invoker对象生成代理对象，在执行{@link com.swust.springbootsource.designpatterns.abs.Invoker#invoke(Request)}方法
     * 前、后进行过滤器的调用
     */
    public <T extends Invoker> T proxyInvoker(Class<T> invoker) {
      /*  return (Invoker) Proxy.newProxyInstance(bean.getClass().getClassLoader(), new Class[]{Invoker.class}, (obj, method, args) -> {
            filterChain.doFilter((Request) args[0], (Response) args[1]);
            return method.invoke(obj, args);
        });*/

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(invoker);
        enhancer.setNamingPolicy(new NamingPolicy() {
            @Override
            public String getClassName(String s, String s1, Object o, Predicate predicate) {
                return "Proxy$" + invoker.getSimpleName() + new Random().nextInt(100);
            }
        });
        enhancer.setCallback(new InvocationHandler() {
            @Override
            public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
                filterChain.doFilter((Request) args[0], null);
                return method.invoke(obj, args);
            }
        });
        return (T) enhancer.create();
    }
}