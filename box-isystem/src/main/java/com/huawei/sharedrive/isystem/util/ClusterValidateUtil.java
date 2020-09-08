package com.huawei.sharedrive.isystem.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.isystem.monitor.domain.Cluster;

public final class ClusterValidateUtil
{
    private static Logger logger = LoggerFactory.getLogger(ClusterValidateUtil.class);
    
    public static boolean validate(Cluster cluster)
    {
        int min = 1;
        int max = 64;
        if (null == cluster.getClusterName())
        {
            logger.error("Cluster.ClusterName can't be null ");
            return false;
        }
        
        if (null == cluster.getClusterName())
        {
            logger.error("Cluster.ClusterServiceName can't be null ");
            return false;
        }
        if (null != cluster.getClusterName() && !isSatisfied(cluster.getClusterName(), min, max))
        {
            logger.error("Cluster.ClusterName is blank or  too long");
            return false;
        }
        if (null != cluster.getClusterServiceName()
            && !isSatisfied(cluster.getClusterServiceName(), min, max))
        {
            logger.error("Cluster.ClusterServiceName is blank or too long");
            return false;
        }
        if (null != cluster.getType() && !isSatisfied(cluster.getType(), min, max))
        {
            logger.error("Cluster.Type is blank or too long");
            return false;
        }
        if (null != cluster.getReserve() && !isSatisfied(cluster.getReserve(), 1, 512))
        {
            logger.error("Cluster.Reserve is blank or too long");
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
    
    private ClusterValidateUtil()
    {
        
    }
}
