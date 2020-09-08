package com.huawei.sharedrive.app.plugins.cluster.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.files.dao.ObjectReferenceDAO;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.plugins.cluster.dao.PluginServiceClusterDAO;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceCluster;
import com.huawei.sharedrive.app.plugins.cluster.service.PluginServiceClusterService;

@Service("pluginServiceClusterService")
public class PluginServiceClusterServiceImpl implements PluginServiceClusterService
{
    
    @Autowired
    private PluginServiceClusterDAO pluginServiceClusterDAO;
    
    @Autowired
    private ObjectReferenceDAO objectReferenceDAO;
    
    @Override
    public PluginServiceCluster getClusterByObjectId(String objectId, String appId)
    {
        ObjectReference objectReferece = objectReferenceDAO.get(objectId);
        if(objectReferece == null)
        {
            return null;
        }
        PluginServiceCluster cluster = pluginServiceClusterDAO.getByAppIdAndRouteInfo(objectReferece.getResourceGroupId(), appId);
        return cluster;
    }
    
}
