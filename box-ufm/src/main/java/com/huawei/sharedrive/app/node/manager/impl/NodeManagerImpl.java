package com.huawei.sharedrive.app.node.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.NodeService;
import com.huawei.sharedrive.app.node.manager.NodeManager;
import com.huawei.sharedrive.app.openapi.domain.node.RestBaseObject;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.utils.BusinessConstants;

@Service("nodeManager")
public class NodeManagerImpl implements NodeManager
{
    @Autowired
    private NodeService nodeService;
    
    @Override
    public List<RestBaseObject> getNodePath(long ownerId, long nodeId)
    {
        List<INode> nodeList = nodeService.getNodePath(ownerId, nodeId);
        
        List<RestBaseObject> list = new ArrayList<RestBaseObject>(BusinessConstants.INITIAL_CAPACITIES);
        RestBaseObject obj = null;
        for (INode node : nodeList)
        {
            obj = new RestFolderInfo(node);
            list.add(obj);
        }
        return list;
    }
}
