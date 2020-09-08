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
package com.huawei.sharedrive.isystem.job.domain;

import java.util.Locale;

import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.common.job.thrift.JobInfo;

import pw.cdmi.common.job.JobState;
import pw.cdmi.core.utils.I18nUtils;

/**
 * 
 * @author s90006125
 * 
 */
public class JobDomain
{
    private String jobName;
    
    private String description;
    
    private boolean isStopStatus;
    
    private String state;
    
    private String beanName;
    
    private String parameter;
    
    private String jobType;
    
    private String cron;
    
    private int recordNumber;
    
    private boolean pauseAble;
    
    private int threadNumber;
    
    private long totalSuccess;
    
    private long totalFailed;
    
    private boolean lastResult;
    
    private String changeAbleProperties;
    
    private String model;
    
    private int clusterId;
    
    public JobDomain()
    {
    }
    
    public JobDomain(JobInfo jobInfo, Locale locale)
    {
        this.setJobName(HtmlUtils.htmlEscape(jobInfo.getJobName()));
        this.setDescription(HtmlUtils.htmlEscape(jobInfo.getDescription()));
        
        this.setStop(jobInfo.getState() == JobState.STOP.getCode() ? true : false);
        JobStateDesc state = JobStateDesc.parseStatus(jobInfo.getState());
        String nameCode = state == null ? "" : state.getNameCode();
        this.setState(I18nUtils.toI18n(nameCode, locale));
        
        this.setBeanName(jobInfo.getBeanName());
        this.setParameter(jobInfo.getParameter());
        
        JobTypeDesc type = JobTypeDesc.parseType(jobInfo.getJobType());
        nameCode = type == null ? "" : type.getNameCode();
        this.setJobType(I18nUtils.toI18n(nameCode, locale));
        
        this.setCron(jobInfo.getCron());
        this.setRecordNumber(jobInfo.getRecordNumber());
        this.setPauseAble(jobInfo.isPauseAble());
        this.setThreadNumber(jobInfo.getThreadNumber());
        this.setTotalSuccess(jobInfo.getTotalSuccess());
        this.setTotalFailed(jobInfo.getTotalFailed());
        this.setLastResult(jobInfo.isLastResult());
        this.setChangeAbleProperties(jobInfo.getChangeAbleProperties());
        this.setModel(jobInfo.getModel());
        this.setClusterId(jobInfo.getClusterId());
    }
    
    public String getJobName()
    {
        return jobName;
    }
    
    public void setJobName(String jobName)
    {
        this.jobName = jobName;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public boolean isStop()
    {
        return isStopStatus;
    }
    
    public void setStop(boolean isStop)
    {
        this.isStopStatus = isStop;
    }
    
    public String getState()
    {
        return state;
    }
    
    public void setState(String state)
    {
        this.state = state;
    }
    
    public String getBeanName()
    {
        return beanName;
    }
    
    public void setBeanName(String beanName)
    {
        this.beanName = beanName;
    }
    
    public String getParameter()
    {
        return parameter;
    }
    
    public void setParameter(String parameter)
    {
        this.parameter = parameter;
    }
    
    public String getJobType()
    {
        return jobType;
    }
    
    public void setJobType(String jobType)
    {
        this.jobType = jobType;
    }
    
    public String getCron()
    {
        return cron;
    }
    
    public void setCron(String cron)
    {
        this.cron = cron;
    }
    
    public int getRecordNumber()
    {
        return recordNumber;
    }
    
    public void setRecordNumber(int recordNumber)
    {
        this.recordNumber = recordNumber;
    }
    
    public boolean isPauseAble()
    {
        return pauseAble;
    }
    
    public void setPauseAble(boolean pauseAble)
    {
        this.pauseAble = pauseAble;
    }
    
    public int getThreadNumber()
    {
        return threadNumber;
    }
    
    public void setThreadNumber(int threadNumber)
    {
        this.threadNumber = threadNumber;
    }
    
    public long getTotalSuccess()
    {
        return totalSuccess;
    }
    
    public void setTotalSuccess(long totalSuccess)
    {
        this.totalSuccess = totalSuccess;
    }
    
    public long getTotalFailed()
    {
        return totalFailed;
    }
    
    public void setTotalFailed(long totalFailed)
    {
        this.totalFailed = totalFailed;
    }
    
    public boolean isLastResult()
    {
        return lastResult;
    }
    
    public void setLastResult(boolean lastResult)
    {
        this.lastResult = lastResult;
    }
    
    public String getChangeAbleProperties()
    {
        return changeAbleProperties;
    }
    
    public void setChangeAbleProperties(String changeAbleProperties)
    {
        this.changeAbleProperties = changeAbleProperties;
    }
    
    public String getModel()
    {
        return model;
    }
    
    public void setModel(String model)
    {
        this.model = model;
    }
    
    public int getClusterId()
    {
        return clusterId;
    }
    
    public void setClusterId(int clusterId)
    {
        this.clusterId = clusterId;
    }
}
