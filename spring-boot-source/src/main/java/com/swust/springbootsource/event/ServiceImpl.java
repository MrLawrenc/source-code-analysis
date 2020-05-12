package com.swust.springbootsource.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author : hz20035009-逍遥
 * @date : 2020/4/15 9:45
 * @description : 业务类,使用事件机制，即观察者模式，可以最大程度解耦，spring默认的是同步监听，异步监听需要自行开启
 */
@Service
public class ServiceImpl {
    @Autowired
    private ApplicationEventPublisher publisher;


    public ServiceImpl() {
        System.out.println("构造器中 pub:" + publisher);
    }

    /**
     * 构造器之后会调用的方法。
     * <br>
     * 自动注入：若不使用以下方式注入
     * <pre>
     *      @Autowired
     *     private ApplicationEventPublisher publisher;
     * </pre>
     * 则可以使用有残构造器注入
     * <pre>
     *      @Autowired
     *     public ServiceImpl(ApplicationEventPublisher publisher) {
     *         this.publisher = publisher;
     *     }
     * </pre>
     */
    @PostConstruct
    public void init() {
        System.out.println("PostConstruct中  pub:" + publisher);
    }

    public void service() {
        System.out.println(Thread.currentThread().getName() + "业务操作...........");
        // TODO: 2020/4/15
        publisher.publishEvent(new MyEvent("我是发布的资源1"));
        publisher.publishEvent(new MyEvent2("我是发布的资源2"));
        System.out.println("发布资源结束............................");
    }

    public static class MyEvent extends ApplicationEvent {
        public MyEvent(Object source) {
            super(source);
        }
    }

    public static class MyEvent2 extends ApplicationEvent {
        public MyEvent2(Object source) {
            super(source);
        }
    }

    /**
     * 会监听所有事件，包括spring的
     */
    @EventListener()
    public void allEventListener(ApplicationEvent e) {
        System.out.println(Thread.currentThread().getName() + "监听所有事件：" + e);
    }

    /**
     * 会监听指定的多个事件
     */
    @EventListener(classes = {MyEvent.class, MyEvent2.class})
    public void moreEventListener(ApplicationEvent e) {
        System.out.println(Thread.currentThread().getName() + "监听指定多个事件：" + e);
    }

    /**
     * 会监听指定一个事件
     */
    @EventListener()
    public void eventListener(MyEvent e) {
        System.out.println(Thread.currentThread().getName() + "监听指定事件：" + e);
    }


    /**
     * 异步监听，配合{@link org.springframework.scheduling.annotation.EnableAsync} （开启spring的异步线程池）使用
     */
    @EventListener
    @Async
    public void eventAsyncListener(MyEvent2 e) {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "异步监听指定事件结束：" + e);
    }

    @EventListener
    @Async("taskExecutor")
    public void eventAsyncListener1(MyEvent2 e) {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "使用自定义线程池taskExecutor的异步监听指定事件结束：" + e);
    }

    @EventListener
    @Async("taskExecutor2")
    public void eventAsyncListener2(MyEvent2 e) {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "使用自定义线程池taskExecutor2的异步监听指定事件结束：" + e);
    }
}