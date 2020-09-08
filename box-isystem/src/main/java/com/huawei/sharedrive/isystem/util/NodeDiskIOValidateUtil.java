package com.huawei.sharedrive.isystem.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.isystem.monitor.domain.NodeDiskIO;

public final class NodeDiskIOValidateUtil
{
    private static Logger logger = LoggerFactory.getLogger(NodeDiskIOValidateUtil.class);
    
    public static boolean validate(NodeDiskIO nodeDiskIO)
    {
        int min = 0;
        int max = 64;
        if (null == nodeDiskIO.getHostName())
        {
            logger.error("NodeDiskIO.HostName can't be null ");
            return false;
            
        }
        if (null == nodeDiskIO.getDiskName())
        {
            logger.error("NodeDiskIO.DiskName can't be null ");
            return false;
        }
        if (null != nodeDiskIO.getClusterName() && !isSatisfied(nodeDiskIO.getClusterName(), min, max))
        {
            logger.error("NodeDiskIO.ClusterName is blank or too long ");
            return false;
        }
        if (null != nodeDiskIO.getHostName() && !isSatisfied(nodeDiskIO.getHostName(), min, max))
        {
            logger.error("NodeDiskIO.HostName is blank or too long ");
            return false;
        }
        if (null != nodeDiskIO.getDiskName() && !isSatisfied(nodeDiskIO.getDiskName(), min, max))
        {
            logger.error("NodeDiskIO.DiskName is blank or too long ");
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
    
    private NodeDiskIOValidateUtil()
    {
        
    }
}
