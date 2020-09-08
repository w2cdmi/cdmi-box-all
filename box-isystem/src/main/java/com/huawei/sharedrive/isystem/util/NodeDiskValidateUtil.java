package com.huawei.sharedrive.isystem.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.isystem.monitor.domain.NodeDisk;

public final class NodeDiskValidateUtil
{
    private static Logger logger = LoggerFactory.getLogger(NodeDiskValidateUtil.class);
    
    public static boolean validate(NodeDisk nodeDisk)
    {
        int min = 0;
        int max = 64;
        if (null == nodeDisk.getHostName())
        {
            logger.error("NodeDisk.HostName can't be null ");
            return false;
            
        }
        if (null == nodeDisk.getCatalogueName())
        {
            logger.error("NodeDisk.CatalogueName can't be null ");
            return false;
        }
        if (null != nodeDisk.getClusterName() && !isSatisfied(nodeDisk.getClusterName(), min, max))
        {
            logger.error("NodeDisk.ClusterName is blank or too long ");
            
            return false;
        }
        if (null != nodeDisk.getHostName() && !isSatisfied(nodeDisk.getHostName(), min, max))
        {
            logger.error("NodeDisk.HostName is blank or too long ");
            
            return false;
        }
        if (null != nodeDisk.getCatalogueName() && !isSatisfied(nodeDisk.getCatalogueName(), min, max))
        {
            logger.error("NodeDisk.CatalogueName is blank or too long ");
            
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
    
    private NodeDiskValidateUtil()
    {
        
    }
}
