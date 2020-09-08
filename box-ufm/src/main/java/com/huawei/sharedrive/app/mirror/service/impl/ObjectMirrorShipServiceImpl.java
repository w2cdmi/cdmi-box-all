package com.huawei.sharedrive.app.mirror.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.mirror.dao.ObjectMirrorShipDAO;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.domain.ObjectMirrorShip;
import com.huawei.sharedrive.app.mirror.service.ObjectMirrorShipService;

@Service("objectMirrorShipService")
public class ObjectMirrorShipServiceImpl implements ObjectMirrorShipService
{
    @Autowired
    private ObjectMirrorShipDAO objectMirrorShipDAO;
    
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void createObjectMirrorShip(ObjectMirrorShip ship)
    {
        objectMirrorShipDAO.create(ship);
    }
    
    @Override
    public List<ObjectMirrorShip> lstObjectMirrorShip(String parentObjectId)
    {
       
        return objectMirrorShipDAO.listObjectMirrorShip(parentObjectId);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateCopyType(ObjectMirrorShip ship, int copyType)
    {
        ship.setType(MirrorCommonStatic.combCopyType(ship.getType(), copyType));
        objectMirrorShipDAO.update(ship);
    }

    @Override
    public ObjectMirrorShip getObjectMirrorShip(String objectId, String parentObjectId)
    {
        ObjectMirrorShip ship  = new ObjectMirrorShip();
        ship.setObjectId(objectId);
        ship.setParentObjectId(parentObjectId);
        return objectMirrorShipDAO.get(ship);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteObjectMirrorShip(String parentObjectId)
    {
        objectMirrorShipDAO.deleteByParentObjectId(parentObjectId);
    }
    
}
