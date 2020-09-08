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

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 查询条件
 * 
 * @author s90006125
 * 
 */
public class QueryCondition implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -7667162620883060009L;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    
    private String fileName;
    
    private String logType;
    
    private long maxResult;
    
    private String node;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    
    public QueryCondition()
    {
    }
    
    public QueryCondition(String fileName, String node, Date start, Date end)
    {
        this.fileName = fileName;
        this.node = node;
        this.startTime = start == null ? null : (Date) start.clone();
        this.endTime = end == null ? null : (Date) end.clone();
    }
    
    public Date getEndTime()
    {
        return endTime != null ? new Date(endTime.getTime()) : null;
    }
    
    public String getFileName()
    {
        return fileName;
    }
    
    public String getLogType()
    {
        return logType;
    }
    
    public long getMaxResult()
    {
        return maxResult;
    }
    
    public String getNode()
    {
        return node;
    }
    
    public Date getStartTime()
    {
        return startTime != null ? new Date(startTime.getTime()) : null;
    }
    
    public void setEndTime(Date endTime)
    {
        this.endTime = endTime != null ? new Date(endTime.getTime()) : null;
    }
    
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    
    public void setLogType(String logType)
    {
        this.logType = logType;
    }
    
    public void setMaxResult(long maxResult)
    {
        this.maxResult = maxResult;
    }
    
    public void setNode(String node)
    {
        this.node = node;
    }
    
    public void setStartTime(Date startTime)
    {
        this.startTime = startTime != null ? new Date(startTime.getTime()) : null;
    }
    
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
