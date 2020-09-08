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

import pw.cdmi.file.engine.core.event.AbstractEvent;

/**
 * 
 * @author s90006125
 * 
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventSource
{
    /**
     * 发送事件的类型，支持同时发送多个事件
     * 
     * @return
     */
    Class<? extends AbstractEvent>[] event();
    
    /**
     * 事件类型，标识是否异步处理事件
     * 
     * @return
     */
    boolean isAsync() default true;
}
