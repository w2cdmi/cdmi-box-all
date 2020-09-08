package com.huawei.sharedrive.isystem.mirror.appdatamigration.dao.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.isystem.mirror.appdatamigration.dao.MigrationProcessInfoDAO;
import com.huawei.sharedrive.isystem.mirror.appdatamigration.domain.MigrationProcessInfo;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Component
public class MigrationProcessInfoDAOImpl extends AbstractDAOImpl implements MigrationProcessInfoDAO
{
    
    @SuppressWarnings("deprecation")
    @Override
    public void create(MigrationProcessInfo processInfo)
    {
        sqlMapClientTemplate.insert("MigrationProcessInfo.insert", processInfo);
    }
    
    @Override
    public int update(MigrationProcessInfo processInfo, long filesSize, long filesNumber)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public MigrationProcessInfo getLast(int policyId)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<MigrationProcessInfo> lstProcessInfo(int policyId)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int delete(MigrationProcessInfo processInfo)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public MigrationProcessInfo getLastUnDoneMigrationProcess(MigrationProcessInfo processInfo)
    {
        return (MigrationProcessInfo) sqlMapClientTemplate.queryForObject("MigrationProcessInfo.getLastUnDoneMigrationProcess",
            processInfo);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void updateMigrationProcessForCompleteFile(MigrationProcessInfo migrationProcessInfo)
    {
        sqlMapClientTemplate.update("MigrationProcessInfo.updateMigrationProcessForCompleteFile",
            migrationProcessInfo);
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<MigrationProcessInfo> lstAllMigrationProcessInfoByPolicyId(int policyId)
    {
        return sqlMapClientTemplate.queryForList("MigrationProcessInfo.lstAllMigrationProcessInfoByPolicyId",
            policyId);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void endMigrationProcess(MigrationProcessInfo migrationProcessInfo)
    {
        sqlMapClientTemplate.update("MigrationProcessInfo.endMigrationProcess", migrationProcessInfo);
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<MigrationProcessInfo> getMigrationProcessInfoByPolicyId(int policyId)
    {
        return sqlMapClientTemplate.queryForList("MigrationProcessInfo.getByPolicyId", policyId);
    }
    @SuppressWarnings({"deprecation"})
    @Override
    public MigrationProcessInfo getMigrationProcessInfo(String id)
    {
        return (MigrationProcessInfo) sqlMapClientTemplate.queryForObject("MigrationProcessInfo.get", id);
    }
    
}
