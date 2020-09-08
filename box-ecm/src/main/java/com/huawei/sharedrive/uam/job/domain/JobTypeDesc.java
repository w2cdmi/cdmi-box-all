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
package com.huawei.sharedrive.uam.job.domain;

public enum JobTypeDesc
{
    Cron(0, "job.type.cron"),
    ClusterCron(1, "job.type.cron.cluster"),
    Daemon(2, "job.type.daemon"),
    ClusterDaemon(3, "job.type.daemon.cluster");
    
    private int code;
    
    private String nameCode;
    
    private JobTypeDesc(int code, String nameCode)
    {
        this.code = code;
        this.nameCode = nameCode;
    }
    
    public int getCode()
    {
        return code;
    }
    
    public String getNameCode()
    {
        return this.nameCode;
    }
    
    public static JobTypeDesc parseType(int code)
    {
        for (JobTypeDesc type : JobTypeDesc.values())
        {
            if (code == type.getCode())
            {
                return type;
            }
        }
        
        return null;
    }
}
