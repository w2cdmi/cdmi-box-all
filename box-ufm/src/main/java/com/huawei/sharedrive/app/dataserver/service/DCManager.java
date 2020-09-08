/**
 * 
 */
package com.huawei.sharedrive.app.dataserver.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.dataserver.domain.DataCenter;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroupNode;
import com.huawei.sharedrive.app.dataserver.exception.BusinessErrorCode;
import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.mirror.manager.CopyConfigLocalCache;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.thrift.dc2app.TBusinessException;

import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.common.monitor.domain.ServiceNode;
import pw.cdmi.common.monitor.manager.ZKConfigStatic;
import pw.cdmi.common.monitor.service.ServiceNodeService;
import pw.cdmi.common.monitor.thrift.client.SyncNodeClient;
import pw.cdmi.common.monitor.thrift.server.ThriftNodeUtils;

/**
 * 数据服务器管理接口，提供数据服务器的各种管理操作
 * 
 * @author s00108907
 * 
 */
@Service("dcManager")
public class DCManager implements ConfigListener
{
    public static final String CONFIG_ZOOKEEPER_KEY_DC_CHANGE = "config.zookeeper.key.dcchange";
    
    /** 保存所有资源组 regionid --> ResourceGroup，这个里面放的ResourceGroup，是所有启用状态的资源组 */
    private static final Map<Integer, List<ResourceGroup>> ALL_REGION_RESOURCE_GROUP_MAP = new HashMap<Integer, List<ResourceGroup>>(
        BusinessConstants.INITIAL_CAPACITIES);
    
    /** 保持所有读写状态为可读可写的资源组，是所有启用状态的资源组 */
    private static final Map<Integer, List<ResourceGroup>> ALL_REGION_RESOURCE_GROUP_MAP_RWNORMAL = new HashMap<Integer, List<ResourceGroup>>(
        BusinessConstants.INITIAL_CAPACITIES);
    
    /** 保存所有资源组 grouid --> ResourceGroup */
    private static final Map<Integer, ResourceGroup> ALL_RESOURCE_GROUP_MAP = new HashMap<Integer, ResourceGroup>(
        BusinessConstants.INITIAL_CAPACITIES);
    
    private boolean hasInited = false;
    
    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock(true);
    
    private static Logger logger = LoggerFactory.getLogger(DCManager.class);
    
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    
    /** 保存所有资源组 regionid --> ResourceGroup，这个里面放的ResourceGroup，只有正常状态的资源组 */
    private static final Map<Integer, List<ResourceGroup>> REGION_RESOURCE_GROUP_MAP = new HashMap<Integer, List<ResourceGroup>>(
        BusinessConstants.INITIAL_CAPACITIES);
    
    /** 保持所有读写状态为可读可写的资源组且状态为正常*/
    private static final Map<Integer, List<ResourceGroup>> REGION_RESOURCE_GROUP_MAP_RWNORMAL = new HashMap<Integer, List<ResourceGroup>>(
        BusinessConstants.INITIAL_CAPACITIES);
    
    // 超時時間
    private static final int THRIFT_TIMEOUT = 10000;
    
    /**
     * 释放锁，屏蔽异常
     * 
     * @param lock
     */
    private static void unlock(Lock lock)
    {
        try
        {
            lock.unlock();
        }
        catch (Exception e)
        {
            logger.warn("Unlock Failed.", e);
        }
    }
    
    
    @Autowired
    @Qualifier("appConfigManager")
    private ConfigManager configManager;
    
    
    @Autowired
    private DCService dcService;
    
    @Autowired
    private ResourceGroupService resourceGroupService;
    
    /** 资源组上报超时时长 */
    @Value("${cluster.resourcegroup.report.timeout}")
    private long resourceGroupTimeout = 60000L;
    
    @Autowired
    private ServiceNodeService serviceNodeService;
    
    @Autowired
    private CopyConfigLocalCache copyConfigLocalCache;
    
    /**
     * 启用DC
     * 
     * @param dcid
     */
    public void activeDataCenter(int dcid)
    {
        init();
        dcService.activeDataCenter(dcid);
        // 通知集群
        notifyCluster("");
    }
    
    /**
     * 添加DC
     * 
     * @param name
     * @param domainName
     * @param managerIP
     * @param managerPort
     * @param regionid
     * @return
     */
    public DataCenter addDataCenter(String name, ResourceGroup resourceGroup)
    {
        init();
        DataCenter dataCenter = dcService.addDataCenter(name, resourceGroup);
        
        // 發生節點信息消息到DSS
        syncUFMNodesToDSS(resourceGroup.getManageIp(), resourceGroup.getManagePort());
        
        // 通知集群
        notifyCluster("");
        
        return dataCenter;
    }
    
    /**
     * 檢查資源組是否存在
     * 
     * @param regionId
     * @param resourceGroupid
     */
    public boolean checkRegionAndResoureGroupExisting(Integer regionId, Integer resourceGroupid)
    {
        init();
        try
        {
            LOCK.readLock().lock();
            if (null != regionId)
            {
                
                if (null == ALL_REGION_RESOURCE_GROUP_MAP.get(regionId))
                {
                    return false;
                }
            }
            else
            {
                if (null != resourceGroupid)
                {
                    ResourceGroup group = ALL_RESOURCE_GROUP_MAP.get(resourceGroupid);
                    if (null == group)
                    {
                        return false;
                    }
                    
                }
            }
        }
        finally
        {
            unlock(LOCK.readLock());
        }
        
        return true;
    }
    
    /**
     * 节点状态是否存活
     * 
     * @param groupId
     * @return
     */
    public ResourceGroup checkResourceGroupIsActive(int groupId)
    {
        try
        {
            LOCK.readLock().lock();
            ResourceGroup group = ALL_RESOURCE_GROUP_MAP.get(Integer.valueOf(groupId));
            if(null == group)
            {
                return null;
            }
            List<ResourceGroup> groups = REGION_RESOURCE_GROUP_MAP.get(group.getRegionId());
            if (null == groups || groups.isEmpty())
            {
                return null;
            }
            for (ResourceGroup tmp : groups)
            {
                if (tmp.getId() == groupId)
                {
                    return tmp;
                }
            }
            return null;
        }
        finally
        {
            unlock(LOCK.readLock());
        }
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        LoggerUtil.regiestThreadLocalLog();
        if (!CONFIG_ZOOKEEPER_KEY_DC_CHANGE.equals(key))
        {
            return;
        }
        
        logger.info("Reload DC Cache Cause By Cluster Notify.");
        reloadCache();
    }
    
    /**
     * 删除DC
     * 
     * @param dcid
     */
    public void deleteDataCenter(int dcid)
    {
        dcService.deleteDataCenter(dcid);
        // 通知集群
        notifyCluster("");
    }
    
    public Map<Integer, List<ResourceGroup>> getAliveResourceGroup()
    {
        return REGION_RESOURCE_GROUP_MAP;
    }
    
    public Map<Integer, ResourceGroup> getAllResourceGroup()
    {
        return ALL_RESOURCE_GROUP_MAP;
    }
    
    /**
     * 获取缓存中的资源组
     * 
     * @param groupID
     * @return
     */
    public ResourceGroup getCacheResourceGroup(int groupID)
    {
        try
        {
            init();
            LOCK.readLock().lock();
            return ALL_RESOURCE_GROUP_MAP.get(groupID);
        }
        finally
        {
            unlock(LOCK.readLock());
        }
    }
    
    public List<ResourceGroupNode> getNodeList(int resourceGroupId)
    {
        init();
        return dcService.getNodeList(resourceGroupId);
    }
    
    /**
     * 
     * @param groupId
     * @return
     */
    public ResourceGroup getResourceGroup(int groupId)
    {
        try
        {
            init();
            LOCK.readLock().lock();
            ResourceGroup group = ALL_RESOURCE_GROUP_MAP.get(Integer.valueOf(groupId));
            return group;
        }
        finally
        {
            unlock(LOCK.readLock());
        }
    }
    
    /**
     * 
     * @param groupId
     * @return
     */
    public List<ResourceGroup> getResourceGroupByDcId(int dcId)
    {
        init();
        List<ResourceGroup> groups = resourceGroupService.listGroupsByDC(dcId);
        return groups;
    }
    
    public void init()
    {
        if(hasInited)
        {
            return;
        }
        try
        {
            reloadCache();
            configManager.registListener(this);
            hasInited = true;
        }
        catch(Exception e)
        {
            logger.error("Can not reload Cache");
        }
        
    }
    
    public void notifyCluster(Serializable object)
    {
        // 通知集群
        try
        {
            configManager.setConfig(DCManager.CONFIG_ZOOKEEPER_KEY_DC_CHANGE, object);
            
            // 通知監控，節點狀態發生改變
            configManager.setConfig(ZKConfigStatic.ZK_MONITOR_NODE_CHANGE, object);
        }
        catch (Exception e)
        {
            String message = "Notify to Cluster Failed.";
            logger.warn(message, e);
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, message, e);
        }
    }
    
    
    
    /**
     * 重新初始化缓存
     */
    public void reloadCache()
    {
        List<ResourceGroup> list = resourceGroupService.listAllGroups();
        logger.info("All ResourceGroup IS [ " + ReflectionToStringBuilder.toString(list) + " ] ");
        reloadCache(list);
        
        //将异地复制状态控制下发到dss，保证dss后于ufm启动时，dss异地复制状态能同步 
        copyConfigLocalCache.sendMirrorControlStatusToDss();
    }
    
    /**
     * 重新初始化缓存
     */
    public void reloadCache(List<ResourceGroup> list)
    {
        if (null == list)
        {
            return;
        }
        
        logger.info("Start ReloadCache.");
        try
        {
            LOCK.writeLock().lock();
            
            // 保存所有资源组 regionid --> ResourceGroup，这个里面放的ResourceGroup，只有正常状态的资源组
            REGION_RESOURCE_GROUP_MAP.clear();
            
            REGION_RESOURCE_GROUP_MAP_RWNORMAL.clear();
            
            // 保存所有资源组 regionid --> ResourceGroup，这个里面放的ResourceGroup，是所有启用状态的资源组
            ALL_REGION_RESOURCE_GROUP_MAP.clear();
            
            ALL_REGION_RESOURCE_GROUP_MAP_RWNORMAL.clear();
            
            ALL_RESOURCE_GROUP_MAP.clear();
            
            List<ResourceGroup> groupLists = null;
            List<ResourceGroup> allGroupListsForRegion = null;
            List<ResourceGroup> groupListsRwNormal = null;
            List<ResourceGroup> allGroupListsForRegionRwNormal = null;
            for (ResourceGroup group : list)
            {
                ALL_RESOURCE_GROUP_MAP.put(group.getId(), group);
                
                // 手动置为下线状态的资源组不加入缓存中
                if(ResourceGroup.Status.Disable == group.getStatus())
                {
                    logger.warn("ResourceGroup [ " + group.getId() + " ] is set to Disable .");
                    continue;
                }
               
                if (ResourceGroup.RuntimeStatus.Offline != group.getRuntimeStatus()
                    && System.currentTimeMillis() - group.getLastReportTime() > resourceGroupTimeout)
                {
                    // 如果超时，则设置为离线状态
                    logger.warn("ResourceGroup [ " + group.getId() + " ] Offline.");
                    group.setRuntimeStatus(com.huawei.sharedrive.app.dataserver.domain.ResourceGroup.RuntimeStatus.Offline);
                }
                
                if (com.huawei.sharedrive.app.dataserver.domain.ResourceGroup.Status.Initial == group.getStatus())
                {
                    logger.warn("ResourceGroup [ " + group.getId() + " ] Is Not Enable.");
                    continue;
                }
                
                // 将所有已初始化的资源组，放到ALL_REGION_RESOURCE_GROUP_MAP中
                allGroupListsForRegion = ALL_REGION_RESOURCE_GROUP_MAP.get(group.getRegionId());
                if (null == allGroupListsForRegion)
                {
                    allGroupListsForRegion = new ArrayList<ResourceGroup>(
                        BusinessConstants.INITIAL_CAPACITIES);
                }
                ALL_REGION_RESOURCE_GROUP_MAP.put(group.getRegionId(), allGroupListsForRegion);
                allGroupListsForRegion.add(group);
                
                // 将读写状态为可读可写的放到ALL_REGION_RESOURCE_GROUP_MAP_RWNORMAL中，忽略资源组的状态
                if(com.huawei.sharedrive.app.dataserver.domain.ResourceGroup.RWStatus.Normal == group.getRwStatus())
                {
                    allGroupListsForRegionRwNormal = ALL_REGION_RESOURCE_GROUP_MAP_RWNORMAL.get(group.getRegionId());
                    if (null == allGroupListsForRegionRwNormal)
                    {
                        allGroupListsForRegionRwNormal = new ArrayList<ResourceGroup>(
                            BusinessConstants.INITIAL_CAPACITIES);
                    }
                    ALL_REGION_RESOURCE_GROUP_MAP_RWNORMAL.put(group.getRegionId(), allGroupListsForRegionRwNormal);
                    allGroupListsForRegionRwNormal.add(group);
                }
                
                // 如果状态不正常，则不放到缓存
                if (com.huawei.sharedrive.app.dataserver.domain.ResourceGroup.RuntimeStatus.Offline == group.getRuntimeStatus())
                {
                    logger.warn("ResourceGroup [ " + group.getId() + " ] Is Offline.");
                    continue;
                }
                
                groupLists = REGION_RESOURCE_GROUP_MAP.get(group.getRegionId());
                if (null == groupLists)
                {
                    groupLists = new ArrayList<ResourceGroup>(BusinessConstants.INITIAL_CAPACITIES);
                }
                REGION_RESOURCE_GROUP_MAP.put(group.getRegionId(), groupLists);
                groupLists.add(group);
                
                // 如果读写状态为只读，则不放到缓存，资源组状态为正常
                if (com.huawei.sharedrive.app.dataserver.domain.ResourceGroup.RWStatus.Normal == group.getRwStatus())
                {
                    groupListsRwNormal = REGION_RESOURCE_GROUP_MAP_RWNORMAL.get(group.getRegionId());
                    if (null == groupListsRwNormal)
                    {
                        groupListsRwNormal = new ArrayList<ResourceGroup>(BusinessConstants.INITIAL_CAPACITIES);
                    }
                    REGION_RESOURCE_GROUP_MAP_RWNORMAL.put(group.getRegionId(), groupListsRwNormal);
                    groupListsRwNormal.add(group);
                }
            }
        }
        finally
        {
            unlock(LOCK.writeLock());
        }
    }
    
    /**
     * 查找一个可用的资源组
     * 
     * @param rgroup
     * @return
     */
    public ResourceGroup selectBestGroup(int region)
    {
        try
        {
            init();
            LOCK.readLock().lock();
            List<ResourceGroup> groups = REGION_RESOURCE_GROUP_MAP.get(region);
            if (null != groups && !groups.isEmpty())
            {
                logger.info("select group for region [ {} ] from normal groups", region);
                return groups.get(RANDOM.nextInt(groups.size()));
            }
            
            groups = ALL_REGION_RESOURCE_GROUP_MAP.get(region);
            if (null != groups && !groups.isEmpty())
            {
                logger.warn("select group for region [ {} ] from abnormal groups", region);
                return groups.get(RANDOM.nextInt(groups.size()));
            }
            
            logger.warn("no group for region [ {} ]", region);
            return null;
        }
        finally
        {
            unlock(LOCK.readLock());
        }
    }
    
    /*
     * 选择读写状态为可读可写的资源组
     */
    public ResourceGroup selectBestGroupRwNormal(int region)
    {
        try
        {
            init();
            LOCK.readLock().lock();
            List<ResourceGroup> groups = REGION_RESOURCE_GROUP_MAP_RWNORMAL.get(region);
            if (null != groups && !groups.isEmpty())
            {
                logger.info("select group for region [ {} ] from normal groups", region);
                return groups.get(RANDOM.nextInt(groups.size()));
            }
            
            groups = ALL_REGION_RESOURCE_GROUP_MAP_RWNORMAL.get(region);
            if (null != groups && !groups.isEmpty())
            {
                logger.warn("select group for region [ {} ] from abnormal groups", region);
                return groups.get(RANDOM.nextInt(groups.size()));
            }
            
            logger.warn("no group for region [ {} ]", region);
            return null;
        }
        finally
        {
            unlock(LOCK.readLock());
        }
    }
    
    /**
     * 同步UFM節點到DSS中
     * 
     * @param managerIP
     * @param managerPort
     */
    public void syncUFMNodesToDSS(String managerIP, int managerPort)
    {
        init();
        SyncNodeClient syncclient = null;
        try
        {
            syncclient = new SyncNodeClient(managerIP, managerPort, THRIFT_TIMEOUT);
            List<ServiceNode> lstNodes = serviceNodeService.getAllByClusterType(ServiceNode.CLUSTER_TYPE_UFM);
            if (null == lstNodes || lstNodes.isEmpty())
            {
                logger.error("UFM not service node,version not match.");
                return;
            }
            List<com.huawei.sharedrive.thrift.syncnode.ServiceNode> thriftNodes = new ArrayList<com.huawei.sharedrive.thrift.syncnode.ServiceNode>(
                16);
            for (ServiceNode node : lstNodes)
            {
                thriftNodes.add(ThriftNodeUtils.convertToThriftNode(node));
            }
            syncclient.syncNode(thriftNodes);
        }
        catch (TBusinessException e)
        {
            logger.warn("addDataCenter Failed.", e);
            throw new BusinessException(e.getStatus(), e.getMessage(), e);
        }
        catch (TException e)
        {
            logger.warn("addDataCenter Failed.", e);
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
        finally
        {
            if (null != syncclient)
            {
                syncclient.close();
            }
        }
        
    }
    
    /**
     * 更新DC域名
     * 
     * @param dcid
     * @param domainName
     */
    public void updateDomainNameByDc(int dcid, String domainName)
    {
        resourceGroupService.updateDomainNameByDc(dcid, domainName);
        notifyCluster("");
    }
}
