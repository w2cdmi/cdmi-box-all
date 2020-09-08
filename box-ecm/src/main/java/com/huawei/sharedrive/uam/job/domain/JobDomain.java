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

import java.util.Locale;

import org.springframework.web.util.HtmlUtils;

import pw.cdmi.common.job.JobDefinition;
import pw.cdmi.common.job.JobRuntimeInfo;
import pw.cdmi.common.job.JobState;
import pw.cdmi.common.job.daemon.DaemonJobDefinition;
import pw.cdmi.common.job.quartz.QuartzJobDefinition;
import pw.cdmi.core.utils.I18nUtils;

public class JobDomain
{
    private String jobName;
    
    private String description;
    
    private boolean ifStop;
    
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
    
    public JobDomain(String model, int clusterId, JobDefinition jobDefinition, JobRuntimeInfo jobRuntimeInfo,
        Locale locale)
    {
        this.setJobName(HtmlUtils.htmlEscape(jobDefinition.getJobName()));
        this.setDescription(HtmlUtils.htmlEscape(jobDefinition.getDescription()));
        
        this.setStop(jobDefinition.getState() == JobState.STOP ? true : false);
        JobStateDesc status = JobStateDesc.parseStatus(jobDefinition.getState().getCode());
        if (status != null)
        {
            this.setState(I18nUtils.toI18n(status.getNameCode(), locale));
        }
        
        this.setBeanName(HtmlUtils.htmlEscape(jobDefinition.getBeanName()));
        this.setParameter(HtmlUtils.htmlEscape(jobDefinition.getParameter()));
        
        JobTypeDesc parseType = JobTypeDesc.parseType(jobDefinition.getJobType().getCode());
        
        JobTypeDesc type;
        if (parseType != null)
        {
            type = parseType;
            this.setJobType(I18nUtils.toI18n(type.getNameCode(), locale));
        }
        
        if (jobDefinition instanceof QuartzJobDefinition)
        {
            this.setCron(((QuartzJobDefinition) jobDefinition).getCron());
        }
        
        this.setRecordNumber(jobDefinition.getRecordNumber());
        this.setPauseAble(jobDefinition.isPauseAble());
        
        if (jobDefinition instanceof DaemonJobDefinition)
        {
            DaemonJobDefinition dj = (DaemonJobDefinition) jobDefinition;
            this.setThreadNumber(dj.getThreadNumber());
        }
        
        this.setTotalSuccess(jobRuntimeInfo.getTotalSuccess());
        this.setTotalFailed(jobRuntimeInfo.getTotalFailed());
        this.setLastResult(jobRuntimeInfo.isLastResult());
        this.setChangeAbleProperties(HtmlUtils.htmlEscape(jobDefinition.getChangeAblePropertyNames()));
        this.setModel(HtmlUtils.htmlEscape(model));
        this.setClusterId(clusterId);
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
        return ifStop;
    }
    
    public void setStop(boolean ifStop)
    {
        this.ifStop = ifStop;
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
