package com.swust.springbootsource.designpatterns.impl;

import com.swust.springbootsource.designpatterns.FilterChain;
import com.swust.springbootsource.designpatterns.Request;
import com.swust.springbootsource.designpatterns.Response;
import com.swust.springbootsource.designpatterns.abs.Filter;
import com.swust.springbootsource.designpatterns.abs.Invoker;
import com.swust.springbootsource.designpatterns.config.RegisterUtil;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * @author : MrLawrenc
 * @date : 2020/5/13 22:48
 * @description : 具体的业务
 */
@Component
@Slf4j
public class ServiceInvoker implements Invoker, InitializingBean {
    @Autowired
    private ApplicationContext context;
    @Autowired
    private FilterChain chain;

    @Value("${filter.basepkg: }")
    private String basePkg;

    @Override
    public Response invoke(Request request) {
        System.out.println("service doing....................");
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        basePkg = basePkg.strip();
        //获取容器中的所有过滤器
        List<Filter> beanFilters = new ArrayList<>(context.getBeansOfType(Filter.class).values());
        if (!StringUtils.isEmpty(basePkg)) {
            Reflections reflections = new Reflections(basePkg);
            Set<Class<? extends Filter>> subClz = reflections.getSubTypesOf(Filter.class);

            List<? extends Class<? extends Filter>> sourceClz = beanFilters.stream().map(Filter::getClass).collect(toList());

            List<Class<? extends Filter>> newBeanFilter = subClz.stream().filter(c -> !sourceClz.contains(c)).peek(result -> {
                RootBeanDefinition definition = new RootBeanDefinition();
                definition.setBeanClass(result);
                RegisterUtil.registry.registerBeanDefinition(result.getSimpleName().toLowerCase(), definition);
                beanFilters.add(context.getBean(result));
            }).collect(toList());

            log.info("add bean : {}", newBeanFilter);
        }

        //排序
        AnnotationAwareOrderComparator.sort(beanFilters);

        chain.setBeanFilters(beanFilters);

    }

    public void build(Invoker result, List<Filter> beanFilters) {
        beanFilters.forEach(filter -> {


        });

    }

}