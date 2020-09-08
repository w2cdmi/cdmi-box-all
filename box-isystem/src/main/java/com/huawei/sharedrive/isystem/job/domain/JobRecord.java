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

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.common.job.thrift.ThriftJobExecuteRecord;

import pw.cdmi.core.utils.I18nUtils;

/**
 * 
 * @author s90006125
 *
 */
public class JobRecord
{
    private static final String RESULT_SUCCESS = "job.record.result.success";
    private static final String RESULT_FAILED = "job.record.result.failed";
    public JobRecord()
    {
    }
    
    public JobRecord(ThriftJobExecuteRecord record, Locale locale)
    {
        this.setJobName(HtmlUtils.htmlEscape(record.getJobName()));
        this.setResult(record.isIsSuccess() ? I18nUtils.toI18n(RESULT_SUCCESS, locale) : I18nUtils.toI18n(RESULT_FAILED, locale));
        this.setTimes(record.getTimes());
        
        Date d = new Date(record.getExecuteTime());
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
        
        this.setExecuteTime(df.format(d));
        this.setExecuteMachine(HtmlUtils.htmlEscape(record.getExecuteMachine()));
        this.setOutput(HtmlUtils.htmlEscape(record.getOutput()));
    }
    
    private String jobName;
    
    // 是否成功
    private String result;
    
    // 耗时
    private long times;
    
    // 执行时间
    private String executeTime;
    
    // 执行的服务器标识
    private String executeMachine;
    
    // 输出
    private String output;

    public String getJobName()
    {
        return jobName;
    }

    public void setJobName(String jobName)
    {
        this.jobName = jobName;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public long getTimes()
    {
        return times;
    }

    public void setTimes(long times)
    {
        this.times = times;
    }

    public String getExecuteTime()
    {
        return executeTime;
    }

    public void setExecuteTime(String executeTime)
    {
        this.executeTime = executeTime;
    }

    public String getExecuteMachine()
    {
        return executeMachine;
    }

    public void setExecuteMachine(String executeMachine)
    {
        this.executeMachine = executeMachine;
    }

    public String getOutput()
    {
        return output;
    }

    public void setOutput(String output)
    {
        this.output = output;
    }
}
