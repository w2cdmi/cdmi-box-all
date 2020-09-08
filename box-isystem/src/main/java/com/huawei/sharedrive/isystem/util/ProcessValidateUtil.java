package com.huawei.sharedrive.isystem.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.isystem.monitor.domain.ProcessInfo;

public final class ProcessValidateUtil
{
    private static Logger logger = LoggerFactory.getLogger(ProcessValidateUtil.class);
    
    public static boolean validate(ProcessInfo processInfo)
    {
        int min = 0;
        int max = 64;
        if (!isClusterNameValid(processInfo.getClusterName(), min, max))
        {
            return false;
        }
        
        if (!isHostNameValid(processInfo.getHostName(), min, max))
        {
            return false;
        }
        
        if(!isProcessNameValidate(processInfo.getProcessName(), min, max))
        {
            return false;
        }
        
        if (!isManagerIpValid(processInfo.getManagerIp(), min, max))
        {
            logger.error("ProcessInfo.ManagerIp is blank or too long");
            return false;
        }
    
        if(!validateOthers(processInfo, min, max))
        {
            return false;
        }
        
        return true;
        
    }

    private static boolean validateOthers(ProcessInfo processInfo, int min, int max)
    {
        if (null != processInfo.getRole() && !isSatisfied(processInfo.getRole(), min, max))
        {
            logger.error("ProcessInfo.Role is blank or too long");
            return false;
        }
        if (null != processInfo.getSyn() && !isSatisfied(processInfo.getSyn(), min, max))
        {
            logger.error("ProcessInfo.Syn is blank or too long");
            return false;
        }
        if (null != processInfo.getType() && !isSatisfied(processInfo.getType(), min, max))
        {
            logger.error("ProcessInfo.Type is blank or too long");
            return false;
        }
        if (null != processInfo.getVip() && !isSatisfied(processInfo.getVip(), min, max))
        {
            logger.error("ProcessInfo.Vip is blank or too long");
            return false;
        }
        if (null != processInfo.getReserve() && !isSatisfied(processInfo.getReserve(), 1, 512))
        {
            logger.error("ProcessInfo.Reserve is blank or too long");
            return false;
        }
        return true;
    }

    private static boolean  isProcessNameValidate(String processName, int min, int max)
    {
        if (null == processName)
        {
            logger.error("ProcessInfo.ProcessName can't be null ");
            return false;
        }
    
        if (!isSatisfied(processName, min, max))
        {
            logger.error("ProcessInfo.ProcessName is blank or too long");
            return false;
        }
        return true;
    }

    private static boolean isManagerIpValid(String managerIp, int min, int max)
    {
        return null == managerIp || isSatisfied(managerIp, min, max);
    }

    private static boolean isClusterNameValid(String clusterName, int min, int max)
    {
        if (null == clusterName)
        {
            logger.error("ProcessInfo.ClustereName can't be null ");
            return false;
        }
        
        if (!isSatisfied(clusterName, min, max))
        {
            logger.error("ProcessInfo.ClustereName is blank or too long");
            return false;
        }
        return true;
    }
    
    
    private static boolean isHostNameValid(String hostName, int min, int max)
    {
        if (null == hostName)
        {
            logger.error("ProcessInfo.HostName can't be null ");
            return false;
        }
        
        if (!isSatisfied(hostName, min, max))
        {
            logger.error("ProcessInfo.HostName is blank or too long");
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
    
    private ProcessValidateUtil()
    {
        
    }
}
