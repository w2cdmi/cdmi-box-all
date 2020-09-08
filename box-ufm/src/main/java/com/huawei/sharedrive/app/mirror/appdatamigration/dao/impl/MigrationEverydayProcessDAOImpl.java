package com.huawei.sharedrive.app.mirror.appdatamigration.dao.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.mirror.appdatamigration.dao.MigrationEverydayProcessDAO;
import com.huawei.sharedrive.app.mirror.appdatamigration.domain.MigrationEverydayProcess;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Component
public class MigrationEverydayProcessDAOImpl extends AbstractDAOImpl implements MigrationEverydayProcessDAO
{

    @SuppressWarnings("deprecation")
    @Override
    public void create(MigrationEverydayProcess dayProcess)
    {
        sqlMapClientTemplate.insert("MigrationEverydayProcess.insert",dayProcess);
    }

    @Override
    public List<MigrationEverydayProcess> lstDayProcess(String migrationId, String beginDay, String endDay)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MigrationEverydayProcess> lstAllDayProcess(String migrationId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int deleteByMigrationId(String migrationId)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public MigrationEverydayProcess getUnCompleteDayProcess(String migrationId)
    {
        return (MigrationEverydayProcess)sqlMapClientTemplate.queryForObject("MigrationEverydayProcess.getUnCompleteDayProcess",migrationId);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void closeUnCompleteDayProcess(MigrationEverydayProcess migrationEverydayProcess)
    {
        sqlMapClientTemplate.update("MigrationEverydayProcess.closeUnCompleteDayProcess",migrationEverydayProcess);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void updateForCompleteAFile(MigrationEverydayProcess migrationEverydayProcess)
    {
        sqlMapClientTemplate.update("MigrationEverydayProcess.updateForCompleteAFile",migrationEverydayProcess);
    } 
   
}
