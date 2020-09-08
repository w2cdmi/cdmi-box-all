package com.huawei.sharedrive.app.mirror.dao;

import java.util.List;

import com.huawei.sharedrive.app.mirror.domain.ObjectMirrorShip;

import pw.cdmi.box.dao.BaseDAO;

public interface ObjectMirrorShipDAO extends BaseDAO<ObjectMirrorShip, String>
{
    /**
     * 获取某对象的镜像关系
     * @param parentObjectId
     * @return
     */
    List<ObjectMirrorShip> listObjectMirrorShip(String parentObjectId);
  
    
    
    /**
     * 删除SHIP
     * @param ship
     * @return
     */
    int delete(ObjectMirrorShip ship);
    
    
    /**
     * 获取对象
     * @param ship
     * @return
     */
    ObjectMirrorShip get(ObjectMirrorShip ship);
    
    
    /**
     * 删除对象关系
     * @param parentObjectId
     * @return
     */
    int deleteByParentObjectId(String parentObjectId);
    
}
