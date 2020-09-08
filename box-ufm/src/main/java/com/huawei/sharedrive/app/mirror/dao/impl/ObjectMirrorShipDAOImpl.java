package com.huawei.sharedrive.app.mirror.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.mirror.dao.ObjectMirrorShipDAO;
import com.huawei.sharedrive.app.mirror.domain.ObjectMirrorShip;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.core.utils.HashTool;

@Component
public class ObjectMirrorShipDAOImpl extends AbstractDAOImpl implements ObjectMirrorShipDAO
{
    private static final int TABLE_COUNT = 500;
    
    @SuppressWarnings("deprecation")
    @Override
    public void create(ObjectMirrorShip ship)
    {
        ship.setTableSuffix(getTableSuffix(ship));
        sqlMapClientTemplate.insert("ObjectMirrorShip.insert", ship);
    }
    
    @Override
    public void delete(String id)
    {
        throw new BusinessException("delete by id failed:" + id);
    }
    
    @Override
    public ObjectMirrorShip get(String id)
    {
        throw new BusinessException("get by id failed:" + id);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void update(ObjectMirrorShip ship)
    {
        ship.setTableSuffix(getTableSuffix(ship));
        
        sqlMapClientTemplate.update("ObjectMirrorShip.update", ship);
        
    }
    
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<ObjectMirrorShip> listObjectMirrorShip(String parentObjectId)
    {
        ObjectMirrorShip ship = new ObjectMirrorShip();
        ship.setParentObjectId(parentObjectId);
        ship.setTableSuffix(getTableSuffix(ship));
        return sqlMapClientTemplate.queryForList("ObjectMirrorShip.getByParent", ship);
    }
    
    private int getTableSuffix(ObjectMirrorShip objectMirrorShip)
    {
        String objectParentId = objectMirrorShip.getParentObjectId();
        if (StringUtils.isBlank(objectParentId))
        {
            throw new IllegalArgumentException("illegal INodeSummary sha1 " + objectParentId);
        }
        
        int tableNum = (int) (HashTool.applySuffux(objectParentId) % TABLE_COUNT);
        return tableNum;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int delete(ObjectMirrorShip ship)
    {
        ship.setTableSuffix(getTableSuffix(ship));
        return sqlMapClientTemplate.delete("ObjectMirrorShip.delete", ship);
        
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public ObjectMirrorShip get(ObjectMirrorShip ship)
    {
        ship.setTableSuffix(getTableSuffix(ship));
        return (ObjectMirrorShip) sqlMapClientTemplate.queryForObject("ObjectMirrorShip.getByIdAndParentId",
            ship);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int deleteByParentObjectId(String parentObjectId)
    {
        
        ObjectMirrorShip ship = new ObjectMirrorShip();
        ship.setParentObjectId(parentObjectId);
        ship.setTableSuffix(getTableSuffix(ship));
        return sqlMapClientTemplate.delete("ObjectMirrorShip.deleteByParent", ship);
    }
}
