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
import java.util.Arrays;

/**
 * 
 * @author s90006125
 *
 */
public class DefalutEventObject
{
    private Object[] args;
    
    private Method method;
    
    public DefalutEventObject(Method method, Object[] args)
    {
        this.method = method;
        this.setArgs(args);
    }
    
    public Object[] getArgs()
    {
        if(null == this.args)
        {
            return new Object[0];
        }
        
        return Arrays.copyOf(this.args, this.args.length);
    }
    
    public Method getMethod()
    {
        return method;
    }
    
    public void setArgs(Object[] args)
    {
        if(null != args)
        {
            this.args = Arrays.copyOf(args, args.length);
        }

        this.args = null;
    }
    
    public void setMethod(Method method)
    {
        this.method = method;
    }
}
