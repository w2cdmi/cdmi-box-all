package com.huawei.sharedrive.isystem.plugin.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.authapp.service.AuthAppService;
import com.huawei.sharedrive.isystem.cluster.dao.DCDao;
import com.huawei.sharedrive.isystem.cluster.dao.RegionDao;
import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;
import com.huawei.sharedrive.isystem.cluster.domain.Region;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.plugin.domain.DCTreeNode;
import com.huawei.sharedrive.isystem.plugin.domain.PluginServerInstance;
import com.huawei.sharedrive.isystem.plugin.domain.PluginServerView;
import com.huawei.sharedrive.isystem.plugin.manager.PluginManager;
import com.huawei.sharedrive.isystem.plugin.service.PluginService;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServerCluster;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServerInstance;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServiceRouter;
import com.huawei.sharedrive.thrift.pluginserver.TWorkerNode;

import pw.cdmi.uam.domain.AuthApp;

@Service("pluginManager")
public class PluginManagerImpl implements PluginManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginManagerImpl.class);
    
    private static final String[] APP_PREVIEW = {"PreviewPlugin"};
    
    private static final String[] APP_KIA = {"SecurityScan"};
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private PluginService pluginService;
    
    @Autowired
    private DCDao dcDao;
    
    @Autowired
    private RegionDao regionDao;
    
    @Override
    public List<AuthApp> listAuthApp()
    {
        List<AuthApp> apps = new ArrayList<AuthApp>(APP_PREVIEW.length + APP_KIA.length);
        apps.addAll(listAppPreview());
        apps.addAll(listAppKia());
        return apps;
    }
    
    @Override
    public AuthApp getAuthApp(String appId)
    {
        if (APP_PREVIEW[0].equals(appId))
        {
            return listAppPreview().get(0);
        }
        if (APP_KIA[0].equals(appId))
        {
            return listAppKia().get(0);
        }
        return null;
    }
    
    private List<AuthApp> listAppPreview()
    {
        List<AuthApp> apps = new ArrayList<AuthApp>(APP_PREVIEW.length);
        AuthApp app;
        for (String s : APP_PREVIEW)
        {
            app = authAppService.getByAuthAppID(s);
            if (null != app)
            {
                app.setAuthUrl("p");
                apps.add(app);
            }
        }
        return apps;
    }
    
    private List<AuthApp> listAppKia()
    {
        List<AuthApp> apps = new ArrayList<AuthApp>(APP_KIA.length);
        AuthApp app;
        for (String s : APP_KIA)
        {
            app = authAppService.getByAuthAppID(s);
            if (null != app)
            {
                app.setAuthUrl("k");
                apps.add(app);
            }
        }
        return apps;
    }
    
    @Override
    public void addPreview(TPluginServerCluster pluginServerCluster, List<TPluginServiceRouter> addRouter,
        List<TPluginServiceRouter> delRouter) throws TException
    {
        pluginService.setPluginServerCluster(pluginServerCluster, addRouter, delRouter);
    }
    
    @Override
    public List<PluginServerView> getListClusterView(String appId) throws TException
    {
        
        List<TPluginServerCluster> list = getListCluster(appId);
        PluginServerView pluginServerView;
        List<PluginServerView> views = new ArrayList<PluginServerView>(list.size());
        for (TPluginServerCluster pc : list)
        {
            pluginServerView = changePluginServerClusterToView(pc);
            if (null != pluginServerView)
            {
                views.add(pluginServerView);
            }
        }
        return views;
    }
    
    private List<TPluginServerCluster> getListCluster(String appId) throws TException
    {
        isAppKIA(appId);
        TPluginServerCluster pluginServerCluster = new TPluginServerCluster();
        pluginServerCluster.setAppId(appId);
        List<TPluginServerCluster> list = pluginService.listPluginServerCluster(pluginServerCluster);
        return list;
    }
    
    private PluginServerView changePluginServerClusterToView(TPluginServerCluster pluginServerCluster)
    {
        PluginServerView pluginServerView = null;
        if (null != pluginServerCluster)
        {
            pluginServerView = new PluginServerView(pluginServerCluster);
            DataCenter dc = dcDao.get(pluginServerCluster.getDssId());
            if (null != dc)
            {
                pluginServerView.setDssName(dc.getName());
            }
        }
        return pluginServerView;
    }
    
    @Override
    public List<TPluginServiceRouter> getListRouter(List<TPluginServerCluster> clusters) throws TException
    {
        List<TPluginServiceRouter> list = new ArrayList<TPluginServiceRouter>(0);
        List<TPluginServiceRouter> routers;
        for (TPluginServerCluster cluster : clusters)
        {
            routers = pluginService.getPluginServiceRouter(cluster);
            list.addAll(routers);
        }
        return list;
    }
    
    @Override
    public List<DCTreeNode> getListRegionDao()
    {
        List<Region> regions = regionDao.getAll();
        List<DCTreeNode> nodes = new ArrayList<DCTreeNode>(regions.size());
        DCTreeNode node;
        for (Region region : regions)
        {
            node = new DCTreeNode();
            node.setChecked(false);
            node.setIsParent(true);
            node.setName(region.getCode());
            node.setId(region.getId());
            nodes.add(node);
        }
        return nodes;
    }
    
    @Override
    public List<DCTreeNode> getListDCRouter(int regionId, TPluginServerCluster cluster) throws TException
    {
        List<DataCenter> dces = dcDao.getAllByRegion(regionId);
        List<DCTreeNode> nodes = new ArrayList<DCTreeNode>(0);
        List<TPluginServerCluster> allCluster = getListCluster(cluster.getAppId());
        TPluginServerCluster c;
        int size = allCluster.size();
        for (int i = 0; i < size; i++)
        {
            c = allCluster.get(i);
            if (c.getClusterId() == cluster.getClusterId())
            {
                allCluster.remove(i);
                break;
            }
        }
        List<DataCenter> canUseData = removeOtherChose(dces, allCluster);
        if (cluster.getClusterId() == 0)
        {
            cluster.setClusterId(-1L);
        }
        List<TPluginServiceRouter> myRouters = pluginService.getPluginServiceRouter(cluster);
        DCTreeNode node;
        for (DataCenter dc : canUseData)
        {
            node = new DCTreeNode(dc.getId(), dc.getName());
            for (TPluginServiceRouter router : myRouters)
            {
                if (router.getDssId() == dc.getId())
                {
                    node.setChecked(true);
                    break;
                }
                
            }
            nodes.add(node);
        }
        return nodes;
    }
    
    @Override
    public void deletePluginServcie(long clusterId) throws TException
    {
        TPluginServerCluster pluginServerCluster = new TPluginServerCluster();
        pluginServerCluster.setClusterId(clusterId);
        pluginService.delPluginServerCluster(pluginServerCluster);
    }
    
    @Override
    public List<PluginServerInstance> getListPluginServerInstance(long clusterId) throws TException
    {
        List<TPluginServerInstance> instances = pluginService.getListPluginServerInstance(clusterId);
        List<PluginServerInstance> views = new ArrayList<PluginServerInstance>(0);
        PluginServerInstance instance;
        for (TPluginServerInstance instan : instances)
        {
            instance = new PluginServerInstance(instan);
            views.add(instance);
        }
        return views;
    }
    
    @Override
    public PluginServerView getPluginServerView(TPluginServerCluster tPluginServerCluster) throws TException
    {
        List<TPluginServerCluster> listClusters = pluginService.listPluginServerCluster(tPluginServerCluster);
        TPluginServerCluster tempCluster = null;
        PluginServerView pServerView = null;
        for (TPluginServerCluster cluster : listClusters)
        {
            if (cluster.getClusterId() == tPluginServerCluster.getClusterId())
            {
                tempCluster = cluster;
            }
        }
        if (null != tempCluster)
        {
            pServerView = new PluginServerView(tempCluster);
            DataCenter dc = dcDao.get(tempCluster.getDssId());
            if (null != dc)
            {
                pServerView.setDssName(dc.getName());
            }
        }
        return pServerView;
    }
    
    private List<DataCenter> removeOtherChose(List<DataCenter> dces, List<TPluginServerCluster> otherCluster)
        throws TException
    {
        List<TPluginServiceRouter> routers = getListRouter(otherCluster);
        List<DataCenter> list = new ArrayList<DataCenter>(0);
        
        boolean temp;
        for (DataCenter dataCenter : dces)
        {
            temp = false;
            for (TPluginServiceRouter router : routers)
            {
                temp = (router.getDssId() == dataCenter.getId());
                if (temp)
                {
                    break;
                }
            }
            if (!temp)
            {
                list.add(dataCenter);
            }
        }
        return list;
    }
    
    public static Boolean isAppKIA(String appId) throws BusinessException
    {
        if (StringUtils.isNotEmpty(appId))
        {
            for (String s : APP_KIA)
            {
                LOGGER.info("appId equals in KIA,KIA Name[" + s + "],appId name [" + appId + "] is"
                    + appId.equals(s));
                if (appId.equals(s))
                {
                    return true;
                }
            }
            for (String s : APP_PREVIEW)
            {
                LOGGER.info("appId equals in PREVIEW,PREVIEW Name[" + s + "],appId name [" + appId + "] is"
                    + appId.equals(s));
                if (appId.equals(s))
                {
                    return false;
                }
            }
        }
        LOGGER.info("AppId is :[" + appId + "], is not plugin");
        throw new BusinessException("AppId is :" + appId + ", is not plugin");
    }
    
    @Override
    public List<TWorkerNode> getWrokerList(Long clusterId, String appId) throws TException
    {
        TPluginServerCluster tPluginServerCluster = new TPluginServerCluster();
        tPluginServerCluster.setAppId(appId);
        tPluginServerCluster.setClusterId(clusterId);
        return pluginService.getWrokerList(tPluginServerCluster);
    }
    
}
