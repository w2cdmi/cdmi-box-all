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

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import pw.cdmi.box.domain.PageRequest;

/**
 * 查询条件
 * 
 * @author s90006125
 * 
 */
public class QueryCondition
{
    private int operateType = -1;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    
    /** 根据adminLoginName和adminShowName两个字段查询 */
    private String admin;
    
    /** 分页参数 */
    private PageRequest pageRequest;
    
    public int getOperateType()
    {
        return operateType;
    }
    
    public void setOperateType(int operateType)
    {
        this.operateType = operateType;
    }
    
    public Date getStartTime()
    {
        if (this.startTime != null)
        {
            return new Date(this.startTime.getTime());
        }
        return null;
    }
    
    public void setStartTime(Date startTime)
    {
        if(startTime != null)
        {
            this.startTime = new Date(startTime.getTime());
        }
        else
        {
            this.startTime = null;
        }
    }
    
    public Date getEndTime()
    {
        if (this.endTime != null)
        {
            return new Date(this.endTime.getTime());
        }
        return null;
    }
    
    public void setEndTime(Date endTime)
    {
        if(endTime != null)
        {
            this.endTime = new Date(endTime.getTime());
        }
        else
        {
            this.endTime = null;
        }
    }
    
    public String getAdmin()
    {
        return admin;
    }
    
    public void setAdmin(String admin)
    {
        this.admin = admin;
    }
    
    public PageRequest getPageRequest()
    {
        return pageRequest;
    }
    
    public void setPageRequest(PageRequest pageRequest)
    {
        this.pageRequest = pageRequest;
    }
}
