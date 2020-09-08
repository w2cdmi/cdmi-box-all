package com.huawei.sharedrive.app.mirror.service;

import java.util.List;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.mirror.domain.ObjectMirrorShip;

import pw.cdmi.box.domain.Limit;

public interface MirrorStatisticService
{
    
    List<INode> lstFileAndVersionNode(long userId, Limit limit);
    
    List<ObjectMirrorShip> listObjectMirrorShip(String parentObjectId);
}
