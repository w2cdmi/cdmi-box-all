package com.huawei.sharedrive.app.plugins.cluster.manager.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.ResourceGroupService;
import com.huawei.sharedrive.app.dataserver.thrift.client.PluginServiceClusterThriftServiceClient;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceCluster;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceInstance;
import com.huawei.sharedrive.app.plugins.cluster.service.PluginClusterService;
import com.huawei.sharedrive.thrift.plugins.agent.TWorkerNode;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Service("syncPluginServiceClusterJob")
public class SyncPluginServiceClusterJob extends QuartzJobTask
{
    
    public static final Logger LOGGER = LoggerFactory.getLogger(SyncPluginServiceClusterJob.class);
    
    private long temp = 0;
    
    private long crentTime = System.currentTimeMillis();
    
    @Autowired
    private ResourceGroupService resourceGroupService;
    
    @Autowired
    private PluginClusterService pluginClusterService;
    
    @Autowired
    private DssDomainService dssDomainService;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        long t = System.currentTimeMillis();
        temp = t - crentTime;
        LOGGER.info(t + "------" + crentTime + "----------------------" + (temp / 1000) + "");
        crentTime = t;
        String jobName = context.getJobDefinition().getJobName();
        jobName = jobName.substring(jobName.lastIndexOf(PluginServerJobManagerImpl.JOB_CLUSTER),
            jobName.length());
        int clusterId = Integer.parseInt(jobName.trim().replace(PluginServerJobManagerImpl.JOB_CLUSTER, ""));
        try
        {
            updateInstances(clusterId);
        }
        catch (Exception e)
        {
            LOGGER.error("do task failed", e);
        }
        
    }
    
    private void updateInstances(int clusterId) throws BaseRunException
    {
        
        PluginServiceCluster pluginServiceCluster = pluginClusterService.getClusterbyId(clusterId);
        if (null == pluginServiceCluster)
        {
            LOGGER.info("PluginServiceCluster is null ,clusterId =[ " + clusterId + " ]");
            return;
        }
        List<PluginServiceInstance> instances = pluginClusterService.listPluginServiceInstance(clusterId);
        
        PluginServiceInstance instance = null;
        List<TWorkerNode> list = getWorkList(pluginServiceCluster);
        LOGGER.info("wrokerList is null?" + (list == null));
        if (list != null && !list.isEmpty())
        {
            LOGGER.info(" instances size" + instances.size() + "workList size" + list.size());
            Date now = null;
            for (TWorkerNode node : list)
            {
                instance = new PluginServiceInstance(node.getIp(), clusterId, node.getName());
                now = new Date();
                instance.setLastMonitorTime(now);
                instance.setStatus(node.getStatus());
                LOGGER.info(" ip[{}] name[{}] status[{}]", node.getIp(), node.getName(), node.getStatus());
                if (instances.contains(instance))
                {
                    pluginClusterService.updatePluginServiceInstance(instance);
                    instances.remove(instance);
                }
                else
                {
                    pluginClusterService.createPluginServiceInstance(instance);
                }
            }
            if (!instances.isEmpty())
            {
                pluginServiceCluster.setStatus(PluginServiceCluster.STATUS_ABNORMAL);
            }
            else
            {
                pluginServiceCluster.setStatus(PluginServiceCluster.STATUS_NORMAL);
            }
            
        }
        else
        {
            pluginServiceCluster.setStatus(PluginServiceCluster.STATUS_OFFLINE);
        }
        Iterator<PluginServiceInstance> iter = instances.iterator();
        Date now = null;
        while (iter.hasNext())
        {
            instance = iter.next();
            // 设置状态是离线
            instance.setStatus((byte) -1);
            now = new Date();
            instance.setLastMonitorTime(now);
            LOGGER.info("change instance is status -1" + instance.getClusterId() + "--" + instance.getIp());
            pluginClusterService.updatePluginServiceInstance(instance);
            
        }
        pluginServiceCluster.setLastMonitorTime(new Date());
        pluginClusterService.updatePluginServiceCluster(pluginServiceCluster, null, null);
    }
    
    private List<TWorkerNode> getWorkList(PluginServiceCluster pluginServiceCluster)
    {
        ResourceGroup resourceGroup = resourceGroupService.getResourceGroup(pluginServiceCluster.getDssId());
        
        List<TWorkerNode> list = null;
        PluginServiceClusterThriftServiceClient client = null;
        try
        {
            String domain = dssDomainService.getDomainByDssId(resourceGroup);
            LOGGER.info("[{}:{}] entry key:{}",
                domain,
                resourceGroup.getManagePort(),
                PluginServiceClusterThriftServiceClient.isKiA(pluginServiceCluster));
            client = new PluginServiceClusterThriftServiceClient(domain, resourceGroup.getManagePort(),
                PluginServiceClusterThriftServiceClient.isKiA(pluginServiceCluster));
            list = client.getWrokerList();
        }
        catch (RuntimeException e)
        {
            LOGGER.info("DSS getTWrokerListException", e);
        }
        catch (Exception e)
        {
            LOGGER.info("DSS getTWrokerListException", e);
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
        return list;
    }
}
