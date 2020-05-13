package com.swust.springbootsource.designpatterns.abs;

import com.swust.springbootsource.designpatterns.Request;
import com.swust.springbootsource.designpatterns.Response;

/**
 * @author : MrLawrenc
 * @date : 2020/5/13 22:43
 * @description :   业务逻辑接口
 */
public interface Invoker {

    Response invoke(Request request);
}
