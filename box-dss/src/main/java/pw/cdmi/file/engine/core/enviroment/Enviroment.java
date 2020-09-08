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
package pw.cdmi.file.engine.core.enviroment;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author s90006125
 * 
 */
public final class Enviroment
{
    private Enviroment()
    {
        
    }
    
    private static final Map<String, Object> CONTEXT = new HashMap<String, Object>(1);
    
    public static Object getEnviroment(String key)
    {
        return CONTEXT.get(key);
    }
    
    public static void regiestEnviroment(String key, Object obj)
    {
        CONTEXT.put(key, obj);
    }
}
