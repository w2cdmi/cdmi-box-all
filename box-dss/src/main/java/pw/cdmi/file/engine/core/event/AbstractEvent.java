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
package pw.cdmi.file.engine.core.event;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.springframework.context.ApplicationEvent;

/**
 * 抽象事件类
 * 
 * @author s90006125
 * 
 */
public abstract class AbstractEvent extends ApplicationEvent
{
    private static final long serialVersionUID = 8618429736369362378L;
    
    private boolean async = true;
    
    /**
     * 
     * @param source 时间原
     * @param async 是否异步事件
     */
    public AbstractEvent(Object source, boolean async)
    {
        super(source);
        this.async = async;
    }
    
    /**
     * 判断是否异步事件
     * 
     * @return
     */
    public boolean isAsync()
    {
        return async;
    }
    
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
