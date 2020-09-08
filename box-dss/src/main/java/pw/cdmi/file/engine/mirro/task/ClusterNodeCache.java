package pw.cdmi.file.engine.mirro.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import pw.cdmi.file.engine.manage.datacenter.domain.ClusterNode;
import pw.cdmi.file.engine.manage.datacenter.service.DCService;
import pw.cdmi.file.engine.mirro.printer.MirrorPrinter;

@Service("clusterNodeCache")
@Lazy(false)
public class ClusterNodeCache
{
    private static List<ClusterNode> lstClusterNode = new ArrayList<ClusterNode>(10);
    
    public static final int DEFAULT_REFRESH_CACHE_TIME = 600000;
    
    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock(true);
    
    @Autowired
    private DCService dcService;
    
    public List<ClusterNode> getClusterlst()
    {
        if (CollectionUtils.isEmpty(lstClusterNode))
        {
            reFreshClusterNode();
            return lstClusterNode;
        }
        
        ClusterNode clusterNode = lstClusterNode.get(0);
        if ((System.currentTimeMillis() - clusterNode.getLastReportTime()) > DEFAULT_REFRESH_CACHE_TIME)
        {
            reFreshClusterNode();
            return lstClusterNode;
        }
        
        return lstClusterNode;
    }
    
    private synchronized void reFreshClusterNode()
    {
        try
        {
            LOCK.writeLock().lock();
            lstClusterNode.clear();
            lstClusterNode = dcService.getClusterNodeList();
        }
        finally
        {
            unlock(LOCK.writeLock());
        }
    }
    
    private static void unlock(Lock lock)
    {
        try
        {
            lock.unlock();
        }
        catch (Exception e)
        {
            MirrorPrinter.warn("Unlock Failed.", e);
        }
    }
    
}
