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
package pw.cdmi.box.uam.job.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.job.domain.JobDomain;
import pw.cdmi.box.uam.job.domain.JobRecord;
import pw.cdmi.common.job.JobDefinition;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.JobRuntimeInfo;
import pw.cdmi.common.job.manage.service.JobService;

@Controller
@RequestMapping(value = "/job")
public class JobController extends AbstractCommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(JobController.class);
    
    private static final int UAM_CLUSTER_ID = -3;
    
    @Autowired
    private JobService jobService;
    
    @RequestMapping(value = "", method = {RequestMethod.GET})
    public String enter()
    {
        return "job/jobManageMain";
    }
    
    @RequestMapping(value = "enterList", method = {RequestMethod.GET})
    public String enterList()
    {
        return "job/jobList";
    }
    
    @RequestMapping(value = "recordPage/{clusterId}/{jobName}/{modelName}", method = {RequestMethod.GET})
    public String enterRecordList(@PathVariable("clusterId") int clusterId,
        @PathVariable("jobName") String jobName, @PathVariable("modelName") String modelName, Model model)
    {
        model.addAttribute("clusterId", clusterId);
        model.addAttribute("jobName", jobName);
        model.addAttribute("modelName", modelName);
        return "job/jobRecordList";
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "list", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<?> listAllJob(Locale locale)
    {
        try
        {
            List<JobDefinition> jobDefinitions = jobService.listAllJobDefinition("uam");
            List<JobDefinition> bmsList = jobService.listAllJobDefinition("bms");
            List<JobDefinition> cloudappList = jobService.listAllJobDefinition("cloudapp");
            bmsList.addAll(cloudappList);
            jobDefinitions.addAll(bmsList);
            List<JobDomain> jobDomains = transJobDomains(locale, jobDefinitions);
            return new ResponseEntity(jobDomains, HttpStatus.OK);
        }
        catch (Exception e)
        {
            LOGGER.error("list all job failed.", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "record/{clusterId}/{jobName}/{modelName}", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<?> listJobExecuteRecord(@PathVariable("clusterId") int clusterId,
        @PathVariable("jobName") String jobName, @PathVariable("modelName") String modelName, Locale locale,
        Model model)
    {
        try
        {
            List<JobExecuteRecord> records = jobService.listJobExecuteRecord(modelName, jobName);
            if (null == records)
            {
                records = new ArrayList<JobExecuteRecord>(0);
            }
            List<JobRecord> result;
            if (records.isEmpty())
            {
                result = new ArrayList<JobRecord>(0);
            }
            else
            {
                result = new ArrayList<JobRecord>(records.size());
                JobRecord r;
                for (JobExecuteRecord record : records)
                {
                    r = new JobRecord(record, locale);
                    
                    result.add(r);
                }
            }
            
            model.addAttribute("jobName", jobName);
            return new ResponseEntity(result, HttpStatus.OK);
        }
        catch (Exception e)
        {
            LOGGER.error("stop job [ " + clusterId + "," + jobName + "] failed.", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private List<JobDomain> transJobDomains(Locale locale, List<JobDefinition> jobDefinitions)
    {
        List<JobDomain> jobDomains;
        if (null == jobDefinitions || jobDefinitions.isEmpty())
        {
            jobDomains = new ArrayList<JobDomain>(0);
        }
        else
        {
            jobDomains = new ArrayList<JobDomain>(jobDefinitions.size());
            JobRuntimeInfo jobRuntimeInfo;
            JobDomain jobDomain = null;
            for (JobDefinition jobDefinition : jobDefinitions)
            {
                jobRuntimeInfo = jobService.selectJobRuntimeInfo(jobDefinition.getModel(),
                    jobDefinition.getJobName());
                if (null == jobRuntimeInfo)
                {
                    LOGGER.warn("JobRuntimeInfo is null for job [ " + jobDefinition.getJobName() + " ]");
                    jobRuntimeInfo = new JobRuntimeInfo();
                }
                jobDomain = new JobDomain(jobDefinition.getModel(), UAM_CLUSTER_ID, jobDefinition,
                    jobRuntimeInfo, locale);
                jobDomains.add(jobDomain);
            }
        }
        return jobDomains;
    }
}
