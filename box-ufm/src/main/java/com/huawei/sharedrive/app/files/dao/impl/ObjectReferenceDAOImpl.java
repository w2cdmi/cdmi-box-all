package com.huawei.sharedrive.app.files.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.files.dao.ObjectReferenceDAO;
import com.huawei.sharedrive.app.files.domain.ObjectReference;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.core.utils.HashTool;

@Service("objectReferenceDAO")
@SuppressWarnings("deprecation")
public class ObjectReferenceDAOImpl extends AbstractDAOImpl implements ObjectReferenceDAO
{
    private static final int TABLE_COUNT = 500;
    
    @Override
    public void create(ObjectReference objRef)
    {
        objRef.setTableSuffix(getTableSuffix(objRef));
        sqlMapClientTemplate.update("ObjectReference.insert", objRef);
    }
    
    @Override
    public int decreaseRefCount(ObjectReference objRef)
    {
        objRef.setTableSuffix(getTableSuffix(objRef));
        return sqlMapClientTemplate.update("ObjectReference.decreaseRefCount", objRef);
    }
    
    @Override
    public int deleteCheckRef(ObjectReference objRef)
    {
        objRef.setTableSuffix(getTableSuffix(objRef));
        return sqlMapClientTemplate.delete("ObjectReference.deleteCheckRef", objRef);
    }
    
    @Override
    public ObjectReference get(String objectId)
    {
        ObjectReference objRef = new ObjectReference();
        objRef.setId(objectId);
        objRef.setTableSuffix(getTableSuffix(objRef));
        return (ObjectReference) sqlMapClientTemplate.queryForObject("ObjectReference.get", objRef);
    }
    
    @Override
    public Integer getSecurityLabelForUpdate(String objectId)
    {
        ObjectReference objRef = new ObjectReference();
        objRef.setId(objectId);
        objRef.setTableSuffix(getTableSuffix(objRef));
        return (Integer) sqlMapClientTemplate.queryForObject("ObjectReference.getSecurityLabelForUpdate",
            objRef);
    }
    
    @Override
    public int increaseRefCount(ObjectReference objRef)
    {
        objRef.setTableSuffix(getTableSuffix(objRef));
        return sqlMapClientTemplate.update("ObjectReference.increaseRefCount", objRef);
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectReference> lstNeedDeleteObjects(int userdbNumber, int tableNumber, Limit limit)
    {
        ObjectReference filter = new ObjectReference();
        filter.setId(userdbNumber + "");
        filter.setTableSuffix(tableNumber);
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("limit", limit);
        map.put("partitionNum", userdbNumber);
        return sqlMapClientTemplate.queryForList("ObjectReference.lstNeedDeleteObjects", map);
    }
    
    @Override
    public int updateFingerprintAndSize(String id, long size, String sha1, String blockMD5)
    {
        ObjectReference objRef = new ObjectReference();
        objRef.setId(id);
        objRef.setSize(size);
        objRef.setSha1(sha1);
        objRef.setBlockMD5(blockMD5);
        objRef.setTableSuffix(getTableSuffix(objRef));
        return sqlMapClientTemplate.update("ObjectReference.updateFingerprintAndSize", objRef);
    }
    
    @Override
    public void updateLastDeleteTime(ObjectReference objRef)
    {
        objRef.setTableSuffix(getTableSuffix(objRef));
        sqlMapClientTemplate.update("ObjectReference.updateLastDeleteTime", objRef);
    }
    
    @Override
    public int updateSecurityLabel(int securityLabel, String securityVersion, String objectId)
    {
        ObjectReference objRef = new ObjectReference();
        objRef.setSecurityLabel(securityLabel);
        objRef.setSecurityVersion(securityVersion);
        objRef.setId(objectId);
        objRef.setTableSuffix(getTableSuffix(objRef));
        return sqlMapClientTemplate.update("ObjectReference.updateSecurityLabel", objRef);
    }
    
    private int getTableSuffix(ObjectReference objectReference)
    {
        String objectId = objectReference.getId();
        if (StringUtils.isBlank(objectId))
        {
            throw new IllegalArgumentException("illegal object id " + objectId);
        }
        
        int database = (int) (HashTool.applySuffux(objectId) % TABLE_COUNT);
        return database;
    }
    
}
