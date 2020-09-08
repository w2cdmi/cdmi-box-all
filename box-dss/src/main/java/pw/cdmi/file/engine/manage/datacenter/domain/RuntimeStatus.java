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
package pw.cdmi.file.engine.manage.datacenter.domain;

/**
 * 节点状态
 * 
 * @author s90006125
 * 
 */
public enum RuntimeStatus
{
    /**
     * 正常
     */
    Normal(0),
    
    /**
     * 1：存储异常
     */
    StorageAbnormal(1),
    
    /**
     * 离线
     */
    Offline(2);
    
    private int code;
    
    private RuntimeStatus(int code)
    {
        this.code = code;
    }
    
    public int getCode()
    {
        return code;
    }
    
    public static RuntimeStatus parseStatus(int status)
    {
        for (RuntimeStatus s : RuntimeStatus.values())
        {
            if (status == s.getCode())
            {
                return s;
            }
        }
        
        return null;
    }
}
