package com.huawei.sharedrive.uam.statistics.job;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.exception.InternalServerErrorException;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Component("clearTempStatisChartJob")
public class ClearTempStatisChartJob extends QuartzJobTask
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ClearTempStatisChartJob.class);
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record) 
    {
        File directory = new File(getChartPath());
        if (directory.exists())
        {
            try
            {
                FileUtils.cleanDirectory(directory);
            }
            catch (IOException e)
            {
                String message = "clean directory failed";
                LOGGER.warn(message, e);
                record.setSuccess(false);
                record.setOutput(message);
            }
        }
    }
    
    private String getChartPath()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getRootPath());
        sb.append("/static/statisticsTemp");
        if(sb.charAt(sb.length() - 1) != '/')
        {
            sb.append('/');
        }
        return sb.toString();
    }
    
    private String getRootPath()
    {
        URL resource = this.getClass().getClassLoader().getResource("");
        if (null == resource)
        {
            throw new InternalServerErrorException("Can not find the resource");
        }
        String classPath = resource.getPath();
        String rootPath;
        // windows
        if ("\\".equals(File.separator))
        {
            boolean isWebApp = classPath.indexOf("/WEB-INF/classes") != -1;
            if (isWebApp)
            {
                rootPath = classPath.substring(1, classPath.indexOf("/WEB-INF/classes"));
                return rootPath;
            }
            throw new InternalServerErrorException("is not webApp");
        }
        // linux
        else if ("/".equals(File.separator))
        {
            rootPath = classPath.substring(0, classPath.indexOf("/WEB-INF/classes"));
            rootPath = rootPath.replace("\\", "/");
            return rootPath;
        }
        else
        {
            throw new InternalServerErrorException("unsupport OS");
        }
    }
}
