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
package com.huawei.sharedrive.isystem.cluster.domain.filesystem;


/**
 * 
 * @author s90006125
 *
 */
public enum StorageStatus
{
    /**
     * 未启用状态
     */
    NOT_ENABLED(0),
    /**
     * 启用状态
     */
    ENABLE(1),
    /**
     * 停用状态
     */
    DISABLED(2);
    
    private int code;
    
    private StorageStatus(int code)
    {
        this.code = code;
    }
    
    public int getCode()
    {
        return this.code;
    }
    
    public static StorageStatus parseState(int code)
    {
        for (StorageStatus s : StorageStatus.values())
        {
            if (s.getCode() == code)
            {
                return s;
            }
        }
        return null;
    }
}
