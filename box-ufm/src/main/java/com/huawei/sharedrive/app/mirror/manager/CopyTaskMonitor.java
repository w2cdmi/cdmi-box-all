package com.huawei.sharedrive.app.mirror.manager;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.SerializationUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.mirror.domain.DcCopyTaskStatus;
import com.huawei.sharedrive.app.mirror.thrift.client.DCMirrorThriftServiceClient;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.core.utils.ZookeeperUtil;
import pw.cdmi.core.zk.ZookeeperServer;

@Component("CopyTaskMonitor")
public class CopyTaskMonitor extends QuartzJobTask
{
    private static final String CONFIG_PATH = "/config_copyTaskMonitor";
    
    private static final String DC = "dc_";
    
    private static final String TOTAL= "total";
    private static final String INPUT = "input";
    
    private static final String RUNNING ="running";
    
    private static final String SUCCESSD ="successd";
    private static final String FAILED = "failed";
    
    private static final String NOTEXIST = "notexist";
    private static final String CALLBACKSUCCESS = "callbacksuccess";
    private static final String CALLBACKFAILED = "callbackfailed";
    private static final String OTHERS = "others";
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    private CuratorFramework zkClient;
    
    @Autowired
    private DssDomainService dssDomainService;
    
    @Autowired
    private DCManager dcManager;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyTaskMonitor.class);
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        Map<Integer, ResourceGroup> allResourceGroup = dcManager.getAllResourceGroup();
        ResourceGroup resourceGroup = null;
        for (Map.Entry<Integer, ResourceGroup> map : allResourceGroup.entrySet())
        {
            resourceGroup = map.getValue();
            if (resourceGroup.getRuntimeStatus() != ResourceGroup.RuntimeStatus.Offline)
            {
                getCopyTaskStatusFromRg(resourceGroup);
            }
            else
            {
                deleteErrorResourceGroupNode(resourceGroup);
            }
        }
        
    }
    
    @PostConstruct
    public void init()
    {
        zkClient = zookeeperServer.getClient();
        try
        {
            ZookeeperUtil.safeCreateNode(zkClient,CONFIG_PATH,new byte[0]);
        }
        catch (Exception e)
        {
            LOGGER.error("create zk path error",e);
        }
    }
    
    private void getCopyTaskStatusFromRg(ResourceGroup resourceGroup)
    {
        DCMirrorThriftServiceClient client = null;
        String domain = dssDomainService.getDomainByDssId(resourceGroup);
        try
        {
            client = new DCMirrorThriftServiceClient(domain, resourceGroup.getManagePort());
            Map<String, String> map = client.getTaskExeInfo();
            saveToZk(map, resourceGroup.getId());
        }
        catch (RuntimeException e)
        {
            LOGGER.warn("connect to resourcegroup "+resourceGroup.getId()+ " failed", e);
        }
        catch (Exception e)
        {
            LOGGER.warn("connect to resourcegroup "+resourceGroup.getId()+ " failed", e);
        }
        finally
        {
            if (client != null)
            {
                client.close();
            }
        }
    }
    private int getValueFromMap(Map<String, String> map, String key)
    {
        String value = map.get(key);
        int ret = 0;
        try
        {
            ret = Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            LOGGER.error("get value from map error",e);
        }
        return ret;
    }
    public void saveToZk(DcCopyTaskStatus dcCopyTaskStatus, int groupId)
    {
        if(dcCopyTaskStatus==null)
        {
            LOGGER.info("params is null,do nothing");
            return;
        }
        
        Stat stat = null;
        try
        {
            stat = zkClient.checkExists().forPath(CONFIG_PATH + "/" + DC + groupId);
            if (stat == null)
            {
                zkClient.create().forPath(CONFIG_PATH + "/" + DC + groupId);
            }
            
            zkClient.setData().forPath(CONFIG_PATH + "/" + DC + groupId,
                SerializationUtils.serialize(dcCopyTaskStatus));
            LOGGER.info("write data to zk success." + "totalTask is:" + dcCopyTaskStatus.getTotalTask()
                + " runingTask is:" + dcCopyTaskStatus.getRuningTask()+" waitingTask is:"+dcCopyTaskStatus.getInput());
        }
        catch (Exception e)
        {
            LOGGER.error("write data to zk error", e);
        }
    }
    private void saveToZk(Map<String, String> map, int groupId)
    {
        if (map == null)
        {
            LOGGER.error("get copytaskstatus error, its value is null");
            return;
        }
        DcCopyTaskStatus dcCopyTaskStatus = new DcCopyTaskStatus();
        dcCopyTaskStatus.setResourceGroup(groupId);
        dcCopyTaskStatus.setTotalTask(getValueFromMap(map, TOTAL));
        dcCopyTaskStatus.setInput(getValueFromMap(map, INPUT));
        dcCopyTaskStatus.setRuningTask(getValueFromMap(map, RUNNING));
        dcCopyTaskStatus.setFailedTask(getValueFromMap(map, SUCCESSD));
        dcCopyTaskStatus.setFailedTask(getValueFromMap(map, FAILED));
        dcCopyTaskStatus.setNotexist(getValueFromMap(map, NOTEXIST));
        dcCopyTaskStatus.setCallbacksuccess(getValueFromMap(map, CALLBACKSUCCESS));
        dcCopyTaskStatus.setCallbackfailed(getValueFromMap(map, CALLBACKFAILED));
        dcCopyTaskStatus.setOthers(getValueFromMap(map, OTHERS));
        
        saveToZk(dcCopyTaskStatus,groupId);
    }
    
    private void deleteErrorResourceGroupNode(ResourceGroup resourceGroup)
    {
        int groupId = resourceGroup.getId();
        Stat stat = null;
        try
        {
            stat = zkClient.checkExists().forPath(CONFIG_PATH + "/" + DC + groupId);
            if (stat == null)
            {
                return;
            }
            zkClient.delete().forPath(CONFIG_PATH + "/" + DC + groupId);
            LOGGER.info("delete node " + CONFIG_PATH + "/" + DC + groupId + " success");
        }
        catch (Exception e)
        {
            LOGGER.error("delete node " + CONFIG_PATH + "/" + DC + groupId + " error", e);
        }
    }
}
