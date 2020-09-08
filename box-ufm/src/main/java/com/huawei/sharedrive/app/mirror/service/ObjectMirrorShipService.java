package com.huawei.sharedrive.app.mirror.service;

import java.util.List;

import com.huawei.sharedrive.app.mirror.domain.ObjectMirrorShip;

public interface ObjectMirrorShipService
{
    
    /**
     * 创建对象镜像关系
     * 
     * @param ship
     */
    void createObjectMirrorShip(ObjectMirrorShip ship);
    
    /**
     * 获取对象镜像关系
     * 
     * @param parentObjectId
     * @return
     */
    List<ObjectMirrorShip> lstObjectMirrorShip(String parentObjectId);
    
    /**
     * 
     * @param ship
     * @param copyType
     */
    void updateCopyType(ObjectMirrorShip ship, int copyType);
    
    /**
     * 获取镜像关系
     * 
     * @param objectId
     * @param parentId
     * @return
     */
    ObjectMirrorShip getObjectMirrorShip(String objectId, String parentObjectId);
    
    /**
     * 删除对象的mirror关系
     * 
     * @param parentObjectId
     */
    void deleteObjectMirrorShip(String parentObjectId);
    
}
