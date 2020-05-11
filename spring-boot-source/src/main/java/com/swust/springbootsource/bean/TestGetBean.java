package com.swust.springbootsource.bean;

import com.swust.springbootsource.bean.annotation.FactoryBeanTest;
import com.swust.springbootsource.bean.annotation.ImportBeanDefinitionRegistrarTest;
import com.swust.springbootsource.bean.entity.Animal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;

/**
 * @author : MrLawrenc
 * @date : 2020/5/11 21:39
 * @description : 主要是测试三种方式注入bean
 *
 * 顺带比较了通过FactoryBean的注入的特殊性
 * 保存registry之后可以随时注入bean
 * 可以自动注入Spring容器 context
 */
@SpringBootApplication
@Import(value = ImportBeanDefinitionRegistrarTest.class)
public class TestGetBean implements ApplicationRunner {

    /**
     * 由于工厂bean是特殊bean，使用
     * <pre>
     *     @Autowired
     *     @Qualifier("factoryBeanTest")
     *     private Animal cat;
     * </pre>
     * 是注入不了的
     */
    @Autowired
    @Qualifier("factoryBeanTest")
    private Animal cat;
    @Autowired()
    @Qualifier("dog")
    private Animal dog;
    @Autowired()
    @Qualifier("bird")
    private Animal bird;


    /**
     * 对比
     * <pre>
     *     @Autowired
     *     @Qualifier("factoryBeanTest")
     *     private Animal cat;
     * </pre>
     * 保留疑问，后面会详细分析{@linkplain org.springframework.beans.factory.FactoryBean}
     */
    @Autowired
    @Qualifier("&factoryBeanTest")
    private FactoryBeanTest factoryBeanTest;

    @Autowired
    private AnnotationConfigApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(TestGetBean.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        //保存了registry之后可以随时向spring容器注入bean
        RootBeanDefinition definition = new RootBeanDefinition();
        definition.setBeanClass(Object.class);
        ImportBeanDefinitionRegistrarTest.registry.registerBeanDefinition("aa",definition);

        //获取所有bean的名字
        context.getBeanDefinitionNames();


        System.out.println(factoryBeanTest);
        System.out.println(cat.getName() + "  " + dog.getName() + "  " + bird.getName());
    }
}