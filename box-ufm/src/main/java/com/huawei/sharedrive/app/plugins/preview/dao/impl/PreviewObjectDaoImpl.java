package com.huawei.sharedrive.app.plugins.preview.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.plugins.preview.dao.PreviewObjectDao;
import com.huawei.sharedrive.app.plugins.preview.domain.PreviewObject;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.core.utils.HashTool;

@Service("previewObjectDao")
@SuppressWarnings({"unchecked", "deprecation"})
public class PreviewObjectDaoImpl extends AbstractDAOImpl implements PreviewObjectDao
{
    private static final int TABLE_COUNT = 100;
    
    @Override
    public void create(PreviewObject obj)
    {
        obj.setTableSuffix(getTableSuffix(obj));
        sqlMapClientTemplate.update("PreviewObject.insert", obj);
    }
    
    @Override
    public PreviewObject get(String sourceObjectId, long accountId)
    {
        PreviewObject obj = new PreviewObject();
        obj.setSourceObjectId(sourceObjectId);
        obj.setAccountId(accountId);
        obj.setTableSuffix(getTableSuffix(obj));
        return (PreviewObject) sqlMapClientTemplate.queryForObject("PreviewObject.get", obj);
    }
    
    @Override
    public PreviewObject selectForUpdate(String sourceObjectId, long accountId)
    {
        PreviewObject obj = new PreviewObject();
        obj.setSourceObjectId(sourceObjectId);
        obj.setAccountId(accountId);
        obj.setTableSuffix(getTableSuffix(obj));
        return (PreviewObject) sqlMapClientTemplate.queryForObject("PreviewObject.selectForUpdate", obj);
    }
    
    @Override
    public List<PreviewObject> getAllBySourceObjectId(String sourceObjectId)
    {
        PreviewObject obj = new PreviewObject();
        obj.setSourceObjectId(sourceObjectId);
        obj.setTableSuffix(getTableSuffix(obj));
        return sqlMapClientTemplate.queryForList("PreviewObject.getAll", obj);
    }
    
    @Override
    public int delete(String sourceObjectId, long accountId)
    {
        PreviewObject obj = new PreviewObject();
        obj.setSourceObjectId(sourceObjectId);
        obj.setAccountId(accountId);
        obj.setTableSuffix(getTableSuffix(obj));
        return sqlMapClientTemplate.delete("PreviewObject.delete", obj);
    }
    
    private int getTableSuffix(PreviewObject previewObject)
    {
        String objectId = previewObject.getSourceObjectId();
        if (StringUtils.isBlank(objectId))
        {
            throw new IllegalArgumentException("illegal INodeSummary sha1 " + objectId);
        }
        
        int table = (int) (HashTool.applySuffux(objectId) % TABLE_COUNT);
        return table;
    }
    
    @Override
    public int updateConvertStartTime(PreviewObject obj)
    {
        obj.setTableSuffix(getTableSuffix(obj));
        return sqlMapClientTemplate.update("PreviewObject.updateConvertStartTime", obj);
    }
    
    @Override
    public int updateConvertResult(PreviewObject obj)
    {
        obj.setTableSuffix(getTableSuffix(obj));
        return sqlMapClientTemplate.update("PreviewObject.updateConvertResult", obj);
    }
    
    @Override
    public int updateConvertRestart(PreviewObject obj)
    {
        obj.setTableSuffix(getTableSuffix(obj));
        return sqlMapClientTemplate.update("PreviewObject.updateConvertRestart", obj);
    }
}
