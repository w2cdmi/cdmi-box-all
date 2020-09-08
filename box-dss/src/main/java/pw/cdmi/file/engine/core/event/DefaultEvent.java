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

import java.lang.reflect.Method;

/**
 * 默认事件
 * 
 * @author s90006125
 * 
 */
public class DefaultEvent extends AbstractEvent
{
    private static final long serialVersionUID = 7644823355040980359L;
    
    public DefaultEvent(Method method, Object[] args, boolean async)
    {
        super(new DefalutEventObject(method, args), async);
    }
}
