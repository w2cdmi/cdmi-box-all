package com.huawei.sharedrive.isystem.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.isystem.monitor.domain.ClusterInstance;

public final class ClusterInstanceValidateUtil
{
    private static Logger logger = LoggerFactory.getLogger(ClusterInstanceValidateUtil.class);
    
    public static boolean validate(ClusterInstance clusterInstance)
    {
        int min = 1;
        int max = 64;
        
        if(!isClusterServiceName(clusterInstance.getClusterServiceName(), min, max))
        {
            return false;
        }
        
        if(!isHostNameValidate(clusterInstance.getHostName(), min, max)){
            return false;
        }
        
        if (0 == clusterInstance.getReportTime())
        {
            logger.error("ClusterInstance.ReportTime can't be 0 ");
            return false;
        }
        if(!validateOthers(clusterInstance, min, max))
        {
            return false;
        }
        return true;
        
    }
    private static boolean validateOthers(ClusterInstance clusterInstance, int min, int max)
    {
        if (null != clusterInstance.getClusterName()
            && !isSatisfied(clusterInstance.getClusterName(), min, max))
        {
            logger.error("ClusterInstance.ClusterName is blank or too long");
            return false;
        }
        
        if (null != clusterInstance.getDataStatus()
            && !isSatisfied(clusterInstance.getDataStatus(), min, max))
        {
            logger.error("ClusterInstance.DataStatus is blank or too long");
            return false;
        }
       
        if (null != clusterInstance.getRunRole() && !isSatisfied(clusterInstance.getRunRole(), min, max))
        {
            logger.error("ClusterInstance.RunRole is blank or too long");
            return false;
        }
        if (null != clusterInstance.getInnerIP() && !isSatisfied(clusterInstance.getInnerIP(), min, max))
        {
            logger.error("ClusterInstance.InnerIP is blank or too long");
            return false;
        }
        return true;
    }
    private static boolean isClusterServiceName(String clusterServiceName, int min, int max)
    {
        if (null == clusterServiceName)
        {
            logger.error("ClusterInstance.ClusterServiceName can't be null ");
            return false;
        }
        if (!isSatisfied(clusterServiceName, min, max))
        {
            logger.error("ClusterInstance.ClusterServiceName is blank or too long");
            return false;
        }
        return true;
    }
    private static boolean isHostNameValidate(String hostName, int min, int max)
    {
        if (null == hostName)
        {
            logger.error("NodeRunningInfo.HostName can't be null ");
            return false;
        }
        if ( !isSatisfied(hostName, min, max))
        {
            logger.error("NodeRunningInfo.HostName is blank or too long ");
            return false;
        }
        return true;
    }
    private static boolean isSatisfied(String name, int min, int max)
    {
        if (name.length() >= min && name.length() <= max)
        {
            return true;
        }
        
        return false;
        
    }
    
    private ClusterInstanceValidateUtil()
    {
        
    }
    
}
