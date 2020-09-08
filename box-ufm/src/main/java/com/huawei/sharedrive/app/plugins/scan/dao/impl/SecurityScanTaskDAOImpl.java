package com.huawei.sharedrive.app.plugins.scan.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.app.plugins.scan.dao.SecurityScanTaskDAO;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityScanTask;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@SuppressWarnings("deprecation")
@Repository
public class SecurityScanTaskDAOImpl extends AbstractDAOImpl implements SecurityScanTaskDAO
{
    
    @Override
    public void create(SecurityScanTask securityScanTask)
    {
        sqlMapClientTemplate.insert("SecurityScanTask.create", securityScanTask);
    }
    
    @Override
    public int delete(String taskId)
    {
        SecurityScanTask securityScanTask = new SecurityScanTask();
        securityScanTask.setTaskId(taskId);
        return sqlMapClientTemplate.delete("SecurityScanTask.delete", securityScanTask);
    }
    
    @Override
    public int deleteByObjectId(String objectId)
    {
        SecurityScanTask securityScanTask = new SecurityScanTask();
        securityScanTask.setObjectId(objectId);
        return sqlMapClientTemplate.delete("SecurityScanTask.deleteByObjectId", securityScanTask);
    }
    
    @Override
    public int deleteCreatedBefore(Date date)
    {
        SecurityScanTask securityScanTask = new SecurityScanTask();
        securityScanTask.setCreatedAt(date);
        return sqlMapClientTemplate.delete("SecurityScanTask.deleteCreatedBefore", securityScanTask);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<SecurityScanTask> getByObjectIdAndDSSId(String objectId, int dssId)
    {
        SecurityScanTask securityScanTask = new SecurityScanTask();
        securityScanTask.setObjectId(objectId);
        securityScanTask.setDssId(dssId);
        return sqlMapClientTemplate.queryForList("SecurityScanTask.getByObjectIdAndDSSId", securityScanTask);
    }
    
    @Override
    public int getTotalTasks(byte status)
    {
        SecurityScanTask filter = new SecurityScanTask();
        filter.setStatus(status);
        return (int) sqlMapClientTemplate.queryForObject("SecurityScanTask.getTotalTasks", filter);
    }
    
    @Override
    public int updateStatus(Byte status, Date modifiedAt, String taskId)
    {
        SecurityScanTask securityScanTask = new SecurityScanTask();
        securityScanTask.setStatus(status);
        securityScanTask.setModifiedAt(modifiedAt);
        securityScanTask.setTaskId(taskId);
        return sqlMapClientTemplate.update("SecurityScanTask.updateStatus", securityScanTask);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> getOwnedByByObjectId(String objectId)
    {
        
        return sqlMapClientTemplate.queryForList("SecurityScanTask.getOwnedByByObjectId", objectId);
    }
}
