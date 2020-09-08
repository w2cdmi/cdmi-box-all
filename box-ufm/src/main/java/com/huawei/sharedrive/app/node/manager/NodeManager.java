package com.huawei.sharedrive.app.node.manager;

import java.util.List;

import com.huawei.sharedrive.app.openapi.domain.node.RestBaseObject;

public interface NodeManager
{
    List<RestBaseObject> getNodePath(long ownerId, long nodeId);
}
