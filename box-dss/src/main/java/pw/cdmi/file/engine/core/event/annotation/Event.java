/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package pw.cdmi.file.engine.core.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pw.cdmi.file.engine.core.event.DefaultEvent;

/**
 * 
 * @author s90006125
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Event
{
    /**
     * 发送事件的类型，支持同时发送多个事件<br>
     * 如果已经使用了@EventSource标签，那么就先采用EventSource标签中的事件，如果没有，则采用该事件
     * 
     * @return
     */
    Class<? extends DefaultEvent>[] defalutEvent() default {};
    
    /**
     * 事件类型，标识是否异步处理事件
     * 
     * @return
     */
    boolean isAsync() default true;
    
    /**
     * 标识是否只有执行成功后才发送事件
     * 
     * @return
     */
    boolean successOnly() default true;
}
