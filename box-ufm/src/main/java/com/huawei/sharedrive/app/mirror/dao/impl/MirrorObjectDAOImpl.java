package com.huawei.sharedrive.app.mirror.dao.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.mirror.dao.MirrorObjectDAO;
import com.huawei.sharedrive.app.mirror.domain.MirrorObject;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.core.utils.HashTool;

@Component
public class MirrorObjectDAOImpl extends AbstractDAOImpl implements MirrorObjectDAO
{
    private static final int TABLE_COUNT = 500;
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<MirrorObject> getBySrcObjectId(String objectId)
    {
        MirrorObject mirrorObject = new MirrorObject();
        mirrorObject.setTableSuffix(getTableSuffix(mirrorObject.getOwnedBy()));
        return sqlMapClientTemplate.queryForList("MirrorObject.getBySrcObjectId", mirrorObject);
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<MirrorObject> getBySrcObjectIds(String objectId,long ownedBy)
    {
    	MirrorObject mirrorObject = new MirrorObject();
    	mirrorObject.setOwnedBy(ownedBy);
        mirrorObject.setTableSuffix(getTableSuffix(mirrorObject.getOwnedBy()));
        mirrorObject.setSrcObjectId(objectId);
        return sqlMapClientTemplate.queryForList("MirrorObject.getBySrcObjectId", mirrorObject);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void insert(MirrorObject mirrorObject)
    {
        mirrorObject.setTableSuffix(getTableSuffix(mirrorObject.getOwnedBy()));
        sqlMapClientTemplate.insert("MirrorObject.insert", mirrorObject);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int deleteBySrcObjectidAndDestObjectidAndOwnedBy(MirrorObject mirrorObject)
    {
        mirrorObject.setTableSuffix(getTableSuffix(mirrorObject.getOwnedBy()));
        
        return sqlMapClientTemplate.delete("MirrorObject.deleteBySrcObjectidAndDestObjectidAndOwnedBy",
            mirrorObject);
    }
    
    private int getTableSuffix(long ownerId)
    {
        if (ownerId <= 0)
        {
            throw new InvalidParamException("illegal owner id " + ownerId);
        }
        int table = (int) (HashTool.apply(String.valueOf(ownerId)) % TABLE_COUNT);
        return table;
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<MirrorObject> getByOwnedByAndSrcObjectId(MirrorObject mirrorObject)
    {
        mirrorObject.setTableSuffix(getTableSuffix(mirrorObject.getOwnedBy()));
        
        return sqlMapClientTemplate.queryForList("MirrorObject.getByOwnedByAndSrcObjectId", mirrorObject);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int deleteBySrcObjectId(String objectId)
    {
        MirrorObject mirrorObject = new MirrorObject();
        mirrorObject.setTableSuffix(getTableSuffix(mirrorObject.getOwnedBy()));
        return sqlMapClientTemplate.delete("MirrorObject.deleteBySrcObjectId", mirrorObject);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public MirrorObject getBySrcObjectIdAndDestObjectIdAndOwnedBy(MirrorObject mirrorObject)
    {
        mirrorObject.setTableSuffix(getTableSuffix(mirrorObject.getOwnedBy()));
        return (MirrorObject) sqlMapClientTemplate
            .queryForObject("MirrorObject.getBySrcObjectIdAndDestObjectIdAndOwnedBy", mirrorObject);
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<MirrorObject> getMirrorObjectByOwnedByAndSrcObjectId(long ownedBy, String objectId)
    {
        MirrorObject mirrorObject = new MirrorObject();
        mirrorObject.setOwnedBy(ownedBy);
        mirrorObject.setSrcObjectId(objectId);
        mirrorObject.setTableSuffix(getTableSuffix(mirrorObject.getOwnedBy()));
        return sqlMapClientTemplate.queryForList("MirrorObject.getByOwnedByAndSrcObjectId", mirrorObject);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int deleteBySrcObjectIdAndOwnedBy(String objectId, long ownedBy)
    {
        MirrorObject mirrorObject = new MirrorObject();
        mirrorObject.setOwnedBy(ownedBy);
        mirrorObject.setSrcObjectId(objectId);
        mirrorObject.setTableSuffix(getTableSuffix(mirrorObject.getOwnedBy()));
        return sqlMapClientTemplate.delete("MirrorObject.deleteBySrcObjectIdAndOwnedBy", mirrorObject);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int changeUsersSrcObjectId(MirrorObject mirrorObject, String newObjectId)
    {
        /**
         * 在这里将新objectid和我们需要的ownedby，旧objectid都通过mirrorObject传递过去
         * 在取值使用时注意
         */
        mirrorObject.setDestObjectId(newObjectId);
        mirrorObject.setTableSuffix(getTableSuffix(mirrorObject.getOwnedBy()));
        return sqlMapClientTemplate.update("MirrorObject.changeUsersSrcObjectId",mirrorObject);
    }
}
