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

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author s90006125
 * 
 */
public class LogFile implements Serializable
{
    private static final long serialVersionUID = 146609474818503768L;
    
    private String id;
    
    private String fileName;
    
    private String logType;
    
    private String nodeId;
    
    private String nodeName;
    
    private long size;
    
    private Date archiveTime;
    
    @JsonIgnore
    private transient InputStream inputStream;
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getFileName()
    {
        return fileName;
    }
    
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    
    public String getLogType()
    {
        return logType;
    }
    
    public void setLogType(String logType)
    {
        this.logType = logType;
    }
    
    public String getNodeId()
    {
        return nodeId;
    }
    
    public void setNodeId(String nodeId)
    {
        this.nodeId = nodeId;
    }
    
    public String getNodeName()
    {
        return nodeName;
    }
    
    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public Date getArchiveTime()
    {
        return archiveTime != null ? new Date(archiveTime.getTime()) : null;
    }
    
    public void setArchiveTime(Date archiveTime)
    {
        this.archiveTime = archiveTime != null ? new Date(archiveTime.getTime()) : null;
    }
    
    public InputStream getInputStream()
    {
        return inputStream;
    }
    
    public void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }
    
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}
