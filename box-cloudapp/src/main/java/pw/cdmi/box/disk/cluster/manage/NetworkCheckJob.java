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
package pw.cdmi.box.disk.cluster.manage;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pw.cdmi.box.disk.utils.PropertiesUtils;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.core.utils.NetCheckUtils;

/**
 * 
 * @author s90006125
 *
 */
@Service("cloudappNetworkCheckJob")
public class NetworkCheckJob extends QuartzJobTask
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkCheckJob.class);
    
    private static final String ADDRESS_SPLIT = ",";
    
    private static String[] netCheckAddress = null;
    
    private static int retryTimes = -1;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        if (NetCheckUtils.ipV4AddressCheck(getRetryTimes(), getNetCheckAddress()))
        {
            NetworkStatusCache.setReachable(true);
        }
        else
        {
            LOGGER.warn("network check failed, checkAddress is [ {} ]", Arrays.toString(getNetCheckAddress()));
            record.setSuccess(Boolean.FALSE);
            record.setOutput("network check failed.");
            
            NetworkStatusCache.setReachable(false);
        }
    }
    
    private String[] getNetCheckAddress()
    {
        if(null != netCheckAddress)
        {
            return netCheckAddress;
        }
        
        String temp = StringUtils.trimToEmpty(PropertiesUtils.getProperty("system.network.check.address"));
        
        netCheckAddress = temp.split(ADDRESS_SPLIT);
        
        return netCheckAddress;
    }
    
    private int getRetryTimes()
    {
        if(retryTimes > 0)
        {
            return retryTimes;
        }
        
        String arg = this.getParameter();
        if(StringUtils.isBlank(arg))
        {
            retryTimes = 5;
        }
        else
        {
            retryTimes = Integer.parseInt(arg);
        }
        
        return retryTimes;
    }
}
