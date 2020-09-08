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
package com.huawei.sharedrive.isystem.logfile.domain;

import java.util.Date;

/**
 * 
 * @author s90006125
 *
 */
public class FSEndpoint
{
    private int id;
    private String fsType;
    // 如果是S3，则格式为：http:endpoint:httpport:httpsport:ak:sk:bucket-logfilearchive-V1R2C10
    private String endpoint;
    private boolean isCurrentStatus;
    private Date createdAt;
    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }
    public String getFsType()
    {
        return fsType;
    }
    public void setFsType(String fsType)
    {
        this.fsType = fsType;
    }
    public String getEndpoint()
    {
        return endpoint;
    }
    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }
    public boolean isCurrent()
    {
        return isCurrentStatus;
    }
    public void setCurrent(boolean isCurrent)
    {
        this.isCurrentStatus = isCurrent;
    }
    public Date getCreatedAt()
    {
        return createdAt != null ? new Date(createdAt.getTime()) : null;
    }
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt != null ? new Date(createdAt.getTime()) : null;
    }
    
}
