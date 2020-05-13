package com.swust.springbootsource.designpatterns;

import com.swust.springbootsource.designpatterns.abs.Filter;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : MrLawrenc
 * @date : 2020/5/13 22:37
 * @description : TODO
 */
@Data@Component
public class FilterChain {
    private List<Filter> beanFilters;
    private int current;

    public Filter next() {
        return beanFilters.get(++current);
    }

    public Filter pre() {
        return beanFilters.get(--current);
    }
}