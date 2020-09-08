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
package com.huawei.sharedrive.isystem.adminlog.domain;


/**
 * 管理员操作日志
 * 
 * 
 */
public class OperateTypeDomain
{
    private OperateType operateType;
    
    private String operatrDetails;
    
    public String getOperatrDetails()
    {
        return operatrDetails;
    }
    
    public void setOperatrDetails(String operatrDetails)
    {
        this.operatrDetails = operatrDetails;
    }
    
    public OperateType getOperateType()
    {
        return operateType;
    }
    
    public void setOperateType(OperateType operateType)
    {
        this.operateType = operateType;
    }
    
}
