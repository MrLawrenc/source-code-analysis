package com.swust.springbootsource.bean.annotation;

import com.swust.springbootsource.bean.entity.Animal;
import com.swust.springbootsource.bean.entity.Cat;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * @author : MrLawrenc
 * @date : 2020/5/11 21:32
 * @description : 实现工厂bean的方式注入
 */
@Component
public class FactoryBeanTest implements FactoryBean<Animal> {
    @Override
    public Cat getObject() throws Exception {
        return new Cat();
    }

    @Override
    public Class<?> getObjectType() {
        return Cat.class;
    }
}