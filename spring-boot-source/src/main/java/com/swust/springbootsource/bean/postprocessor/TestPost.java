package com.swust.springbootsource.bean.postprocessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author : MrLawrenc
 * @date : 2020/5/30 14:47
 * @description : 区分BeanFactoryPostProcessor和BeanPostProcessor
 */
public class TestPost implements BeanPostProcessor, BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}