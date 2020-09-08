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
package com.huawei.sharedrive.isystem.job.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.common.job.thrift.JobInfo;
import com.huawei.sharedrive.common.job.thrift.ThriftJobExecuteRecord;
import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.job.domain.JobDomain;
import com.huawei.sharedrive.isystem.job.domain.JobRecord;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.thrift.client.JobThriftServiceClient;

import pw.cdmi.common.job.manage.thrift.JobThriftServiceImpl;
import pw.cdmi.common.log.UserLog;
import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;

/**
 * 
 * @author s90006125
 *         
 */
@Controller
@RequestMapping(value = "/job")
public class JobController extends AbstractCommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(JobController.class);
    
    @Autowired
    private ThriftClientProxyFactory ufmThriftClientProxyFactory;
    
    // Isystem本地的jobThriftService
    @Autowired
    @Qualifier("jobThriftService")
    private JobThriftServiceImpl isystemJobService;
    
    // isystem的clusterId
    private static final int ISYSTEM_CLUSTER_ID = -2;
    
    @RequestMapping(value = "", method = {RequestMethod.GET})
    public String enter(Model model)
    {
        model.addAttribute("showCopyPlocy", showCopyPlocy);
        
        return "job/jobManageMain";
    }
    
    @RequestMapping(value = "enterList", method = {RequestMethod.GET})
    public String enterList(Model model)
    {
        return "job/jobList";
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "list", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<?> listAllJob(Locale locale)
    {
        try
        {
            // 获取isystem的所有任务列表
            List<JobInfo> jobInfos = isystemJobService.listAllJobDetail();
            if (null == jobInfos)
            {
                jobInfos = new ArrayList<JobInfo>(1);
            }
            
            for (JobInfo job : jobInfos)
            {
                job.setClusterId(ISYSTEM_CLUSTER_ID);
            }
            
            try
            {
                // 获取ufm dss的所有任务列表
                List<JobInfo> temp = ufmThriftClientProxyFactory.getProxy(JobThriftServiceClient.class)
                    .listAllJobDetail();
                if (null != temp)
                {
                    jobInfos.addAll(temp);
                }
            }
            catch (Exception e)
            {
                LOGGER.error("load job list from ufm failed.", e);
            }
            
            List<JobDomain> jobDomains = null;
            JobDomain jobDomain = null;
            if (jobInfos.isEmpty())
            {
                jobDomains = new ArrayList<JobDomain>(0);
            }
            else
            {
                jobDomains = new ArrayList<JobDomain>(jobInfos.size());
                
                for (JobInfo info : jobInfos)
                {
                    jobDomain = new JobDomain(info, locale);
                    jobDomains.add(jobDomain);
                }
            }
            
            return new ResponseEntity(jobDomains, HttpStatus.OK);
        }
        catch (TException e)
        {
            LOGGER.error("list all job failed.", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "start", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> startJob(int clusterId, String jobName, HttpServletRequest request, String token)
    {
        UserLog userLog = userLogService.initUserLog(request, UserLogType.JOB_START, new String[]{jobName});
        
        userLogService.saveUserLog(userLog);
        
        super.checkToken(token);
        
        JobInfo jobInfo = createNewJobInfo(clusterId, jobName);
        
        try
        {
            if (isIsystem(clusterId))
            {
                isystemJobService.startJob(jobInfo);
            }
            else
            {
                ufmThriftClientProxyFactory.getProxy(JobThriftServiceClient.class).startJob(jobInfo);
            }
            userLog.setDetail(UserLogType.JOB_START.getDetails(new String[]{jobName}));
            userLog.setLevel(UserLogService.SUCCESS_LEVEL);
            userLogService.update(userLog);
            
            return new ResponseEntity(HttpStatus.OK);
        }
        catch (TException e)
        {
            LOGGER.error("start job [ " + clusterId + ',' + jobName + "] failed.", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "stop", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> stopJob(int clusterId, String jobName, HttpServletRequest request, String token)
    {
        UserLog userLog = userLogService.initUserLog(request, UserLogType.JOB_STOP, new String[]{jobName});
        userLogService.saveUserLog(userLog);
        
        super.checkToken(token);
        
        JobInfo jobInfo = createNewJobInfo(clusterId, jobName);
        
        try
        {
            if (isIsystem(clusterId))
            {
                isystemJobService.stopJob(jobInfo);
            }
            else
            {
                ufmThriftClientProxyFactory.getProxy(JobThriftServiceClient.class).stopJob(jobInfo);
            }
            userLog.setDetail(UserLogType.JOB_STOP.getDetails(new String[]{jobName}));
            userLog.setLevel(UserLogService.SUCCESS_LEVEL);
            userLogService.update(userLog);
            
            return new ResponseEntity(HttpStatus.OK);
        }
        catch (TException e)
        {
            LOGGER.error("stop job [ " + clusterId + ',' + jobName + "] failed.", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value = "recordPage/{clusterId}/{jobName}", method = {RequestMethod.GET})
    public String enterRecordList(@PathVariable("clusterId") int clusterId,
        @PathVariable("jobName") String jobName, Model model)
    {
        model.addAttribute("clusterId", clusterId);
        model.addAttribute("jobName", jobName);
        return "job/jobRecordList";
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "record/{clusterId}/{jobName}", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<?> listJobExecuteRecord(@PathVariable("clusterId") int clusterId,
        @PathVariable("jobName") String jobName, Locale locale, Model model)
    {
        JobInfo jobInfo = createNewJobInfo(clusterId, jobName);
        
        try
        {
            List<ThriftJobExecuteRecord> records = null;
            if (isIsystem(clusterId))
            {
                records = isystemJobService.listJobExecuteRecord(jobInfo);
            }
            else
            {
                records = ufmThriftClientProxyFactory.getProxy(JobThriftServiceClient.class)
                    .listJobExecuteRecord(jobInfo);
            }
            
            if (null == records)
            {
                records = new ArrayList<ThriftJobExecuteRecord>(0);
            }
            
            List<JobRecord> result = null;
            if (records.isEmpty())
            {
                result = new ArrayList<JobRecord>(0);
            }
            else
            {
                result = new ArrayList<JobRecord>(records.size());
                
                JobRecord r = null;
                
                for (ThriftJobExecuteRecord record : records)
                {
                    r = new JobRecord(record, locale);
                    
                    result.add(r);
                }
            }
            
            model.addAttribute("jobName", jobName);
            return new ResponseEntity(result, HttpStatus.OK);
        }
        catch (TException e)
        {
            LOGGER.error("stop job [ " + clusterId + ',' + jobName + "] failed.", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private JobInfo createNewJobInfo(int clusterId, String jobName)
    {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setJobName(jobName);
        jobInfo.setClusterId(clusterId);
        return jobInfo;
    }
    
    /**
     * 判断是否是isystem
     * 
     * @param clusterId
     * @return
     */
    private boolean isIsystem(int clusterId)
    {
        return ISYSTEM_CLUSTER_ID == clusterId;
    }
}
