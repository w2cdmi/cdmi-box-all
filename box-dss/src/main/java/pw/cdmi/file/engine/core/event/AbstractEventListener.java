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

/**
 * 事件监听器
 * 
 * @author s90006125
 * 
 */
public abstract class AbstractEventListener<T extends AbstractEvent>
{
    
    /**
     * 获取支持的事件类型
     * 
     * @return
     */
    public abstract Class<T> getSupportType();
    
    public abstract void onApplicationEvent(T event);
}
