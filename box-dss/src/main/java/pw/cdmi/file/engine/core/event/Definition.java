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

import java.lang.reflect.Constructor;

/**
 * 事件生成器定义 
 * @author s90006125
 *
 */
public class Definition<T>
{
    private boolean async;
    
    private Constructor<? extends T> constructor;
    
    public Definition(Constructor<? extends T> constructor, boolean async)
    {
        this.constructor = constructor;
        this.async = async;
    }
    
    public Constructor<? extends T> getConstructor()
    {
        return constructor;
    }
    
    public boolean isAsync()
    {
        return async;
    }
}
