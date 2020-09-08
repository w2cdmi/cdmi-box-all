package com.huawei.sharedrive.isystem.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.isystem.monitor.domain.NodeRunningInfo;

public final class NodeValidateUtil
{
    private static Logger logger = LoggerFactory.getLogger(NodeValidateUtil.class);
    
    public static boolean validate(NodeRunningInfo nodeRunningInfo)
    {
        int min = 0;
        int max = 64;
        if(!isClusterNameValidate(nodeRunningInfo.getClusterName(), min, max)){
            return false;
        }
        
        if(!isHostNameValidate(nodeRunningInfo.getHostName(), min, max)){
            return false;
        }
        
        if (null != nodeRunningInfo.getManagerIp() && !isSatisfied(nodeRunningInfo.getManagerIp(), min, max))
        {
            logger.error("NodeRunningInfo.ManagerIp is blank or too long ");
            return false;
        }
        if(!isNetIPValidate(nodeRunningInfo, min, max))
        {
            return false;
        }
        if (null != nodeRunningInfo.getTopInfo() && !isSatisfied(nodeRunningInfo.getTopInfo(), 1, 512))
        {
            logger.error("NodeRunningInfo.TopInfo is blank or too long ");
            return false;
        }
        
        return true;
        
    }

    private static boolean isNetIPValidate(NodeRunningInfo nodeRunningInfo, int min, int max)
    {
        if (null != nodeRunningInfo.getServiceIp()
            && !isSatisfied(nodeRunningInfo.getServiceIp(), min, max))
        {
            logger.error("NodeRunningInfo.getServiceNet().ip is blank or too long ");
            return false;
        }
        if (null != nodeRunningInfo.getManageIp()
            && !isSatisfied(nodeRunningInfo.getManageIp(), min, max))
        {
            logger.error("NodeRunningInfo.getManageNet().ip is blank or too long ");
            return false;
        }
        if (null != nodeRunningInfo.getPrivateIp()
            && !isSatisfied(nodeRunningInfo.getPrivateIp(), min, max))
        {
            logger.error("NodeRunningInfo.getPrivateNet().ip is blank or too long ");
            return false;
        }
        return true;
    }

    private static boolean isClusterNameValidate(String clusterName, int min, int max)
    {
        if (null == clusterName)
        {
            logger.error("NodeRunningInfo.ClustereName can't be null ");
            return false;
        }
        if ( !isSatisfied(clusterName, min, max))
        {
            logger.error("NodeRunningInfo.ClusterName is blank or too long ");
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
    
    private NodeValidateUtil()
    {
        
    }
}
