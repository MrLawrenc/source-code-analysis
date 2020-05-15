package com.swust.springbootsource.designpatterns.config;

import com.swust.springbootsource.designpatterns.Request;
import com.swust.springbootsource.designpatterns.abs.Invoker;
import com.swust.springbootsource.designpatterns.impl.ServiceInvoker;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/5/15 11:30
 * @description : TODO
 */
public class Test {
    public static void main(String[] args) {
        create1().doInvoke(new Request());
        System.out.println("==========================");
        create2().doInvoke(new Request());

        System.out.println("============效率对比测试开始==============");
        List<ServiceInvoker> invokerList1 = IntStream.range(0, 10000).mapToObj(i -> create1()).collect(Collectors.toList());
        List<ServiceInvoker> invokerList2 = IntStream.range(0, 10000).mapToObj(i -> create2()).collect(Collectors.toList());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("通过索引调用父类方法");
        invokerList1.forEach(in->in.doInvoke(new Request()));
        stopWatch.stop();
        stopWatch.start("通过反射调用父类方法");
        invokerList2.forEach(in->in.doInvoke(new Request()));
        stopWatch.stop();
        System.out.println("============效率对比测试结束==============");
        System.out.println(stopWatch.prettyPrint());
    }

    /**
     * 该方式不经过反射调用父类源方法
     */
    public static ServiceInvoker create1() {
        Enhancer enhancer = new Enhancer();
        //设置需要创建子类的类
        enhancer.setSuperclass(ServiceInvoker.class);
        enhancer.setCallback(new MethodInterceptor() {
            //o 为当前代理对象，这点很重要
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("前置代理");
                //通过代理类时机会调用父类中的方法（使用super.方法名来调用，不经过反射）
                Object result = methodProxy.invokeSuper(o, objects);
                System.out.println("后置代理");
                return result;
            }
        });
        //通过字节码技术动态创建子类实例
        //通过生成子类的方式创建代理类
        return (ServiceInvoker) enhancer.create();
    }

    /**
     * 该方法使用反射调用父类源方法
     */
    public static ServiceInvoker create2() {
        Enhancer enhancer = new Enhancer();
        //设置需要创建子类的类
        enhancer.setSuperclass(ServiceInvoker.class);
        enhancer.setCallback(new MyInterceptor(new ServiceInvoker()));
        //通过字节码技术动态创建子类实例
        //通过生成子类的方式创建代理类
        return (ServiceInvoker) enhancer.create();
    }


}

class MyInterceptor implements MethodInterceptor {

    /**
     * 这是源对象，该代理对象的父类
     */
    private Invoker old;

    public MyInterceptor(ServiceInvoker invoker) {
        this.old = invoker;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("前置代理");
        //通过反射调用父类方法，注意必须传入非代理对象，否则会出现循环调用(如下两种方式调用均可)
        Object result = method.invoke(old, objects);
        //Object result = methodProxy.invoke(old, objects);

        System.out.println("后置代理");
        return result;
    }
}


