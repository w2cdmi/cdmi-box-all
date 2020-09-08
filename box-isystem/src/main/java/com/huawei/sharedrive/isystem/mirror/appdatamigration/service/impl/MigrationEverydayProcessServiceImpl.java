package com.huawei.sharedrive.isystem.mirror.appdatamigration.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.mirror.appdatamigration.dao.MigrationEverydayProcessDAO;
import com.huawei.sharedrive.isystem.mirror.appdatamigration.domain.MigrationEverydayProcess;
import com.huawei.sharedrive.isystem.mirror.appdatamigration.domain.MigrationProcessInfo;
import com.huawei.sharedrive.isystem.mirror.appdatamigration.service.MigrationEverydayProcessService;
import com.huawei.sharedrive.isystem.mirror.appdatamigration.service.MigrationProcessInfoService;

import pw.cdmi.core.utils.RandomGUID;

@Service("migrationEverydayProcessService")
public class MigrationEverydayProcessServiceImpl implements MigrationEverydayProcessService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationEverydayProcessServiceImpl.class);
    
    @Autowired
    private MigrationProcessInfoService migrationProcessInfoService;
    
    @Autowired
    private MigrationEverydayProcessDAO migrationEverydayProcessDAO;
    
    /**
     * 开始新一天的记录
     * 先将未完成的前一天置为完成
     * 再新建
     */
    @Override
    public void create(MigrationProcessInfo processInfo)
    {
        MigrationEverydayProcess migrationEverydayProcess = getUnCompleteDayProcess(processInfo.getId());
        if(migrationEverydayProcess != null)
        {
            closeUnCompleteDayProcess(migrationEverydayProcess);
        }
        
        MigrationProcessInfo migrationProcessInfo = migrationProcessInfoService.getLastUnDoneMigrationProcess(processInfo.getPolicyId());
        if(migrationProcessInfo == null)
        {
            LOGGER.error("create MigrationEverydayProcess error for getLastUnDoneMigrationProcess is null");
            return;
        }
        
        MigrationEverydayProcess newMigrationEverydayProcess = new MigrationEverydayProcess();
        RandomGUID randomGen = new RandomGUID(true);
        newMigrationEverydayProcess.setId(randomGen.getValueAfterMD5());
        newMigrationEverydayProcess.setParentId(migrationProcessInfo.getId());
        newMigrationEverydayProcess.setStartTime(new Date());
        newMigrationEverydayProcess.setPolicyId(migrationProcessInfo.getPolicyId());
        newMigrationEverydayProcess.setEndTime(null);
        newMigrationEverydayProcess.setNewAddFiles(0L);
        newMigrationEverydayProcess.setNewAddSizes(0L);
        
        migrationEverydayProcessDAO.create(newMigrationEverydayProcess);
        
    }

    /**
     * 得到不是一条完整记录【一天有开始和结束时间】的数据
     * 如果返回为null则表示每一天都是一个完整的区间
     * 不为空则一天正在进行
     */
    @Override
    public MigrationEverydayProcess getUnCompleteDayProcess(String id)
    {
        return migrationEverydayProcessDAO.getUnCompleteDayProcess(id);
    }

    @Override
    public void closeUnCompleteDayProcess(MigrationEverydayProcess migrationEverydayProcess)
    {
        migrationEverydayProcess.setEndTime(new Date());
        migrationEverydayProcessDAO.closeUnCompleteDayProcess(migrationEverydayProcess);
    }
    
    /**
     * 一个文件复制完成后调用该接口更新当天的数据
     * @param size
     */
    @Override
    public void updateForCompleteAFile(MigrationProcessInfo processInfo,long size)
    {
        MigrationEverydayProcess migrationEverydayProcess = getUnCompleteDayProcess(processInfo.getId());
        
        if(migrationEverydayProcess == null)
        {
            LOGGER.error("updateForCompleteAFile error for getUnCompleteDayProcess is null");
            return;
        }
        
        migrationEverydayProcess.setNewAddFiles(migrationEverydayProcess.getNewAddFiles()+1);
        migrationEverydayProcess.setNewAddSizes(migrationEverydayProcess.getNewAddSizes()+size);
        
        migrationEverydayProcessDAO.updateForCompleteAFile(migrationEverydayProcess);
    }

    @Override
    public void updateCompleteDayProcess(MigrationEverydayProcess migrationEverydayProcess)
    {
        migrationEverydayProcessDAO.updateForCompleteAFile(migrationEverydayProcess);
    }

    @Override
    public List<MigrationEverydayProcess> getMigrationEverydayProcessById(String parent)
    {
        return migrationEverydayProcessDAO.getMigrationEverydayProcessById(parent);
    }
}
