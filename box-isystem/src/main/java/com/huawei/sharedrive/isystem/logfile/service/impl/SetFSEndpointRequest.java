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
package com.huawei.sharedrive.isystem.logfile.service.impl;

/**
 * 
 * @author s90006125
 *
 */
public class SetFSEndpointRequest
{
    private String fsType;
    private String endpoint;
    
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
}
