package com.huawei.sharedrive.app.mirror.appdatamigration.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.mirror.appdatamigration.dao.NeverCopyContentDAO;
import com.huawei.sharedrive.app.mirror.appdatamigration.domain.MigrationEverydayProcess;
import com.huawei.sharedrive.app.mirror.appdatamigration.domain.NeverCopyContent;
import com.huawei.sharedrive.app.mirror.appdatamigration.service.NeverCopyContentService;
import com.huawei.sharedrive.app.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;

import pw.cdmi.core.utils.RandomGUID;

@Service("neverCopyContentService")
public class NeverCopyContentServiceImpl implements NeverCopyContentService
{
    @Autowired
    private NeverCopyContentDAO neverCopyContentDAO;
    
    @Override
    public void insert(CopyPolicy copyPolicy,MigrationEverydayProcess migrationEverydayProcess,CopyTask copyTask,String reason)
    {
        NeverCopyContent neverCopyContent = new NeverCopyContent();
        neverCopyContent.setAppId(copyPolicy.getAppId());
        neverCopyContent.setBlockMD5("");
        neverCopyContent.setFileName(copyTask.getFileName());
        neverCopyContent.setId(new RandomGUID().getValueAfterMD5());
        neverCopyContent.setMd5("");
        neverCopyContent.setNodeId(copyTask.getSrcINodeId());
        neverCopyContent.setObjectId(copyTask.getSrcObjectId());
        neverCopyContent.setOwnedBy(copyTask.getSrcOwnedBy());
        neverCopyContent.setParentId(migrationEverydayProcess.getId());
        neverCopyContent.setPolicyId(copyTask.getPolicyId());
        neverCopyContent.setReason(reason);
        neverCopyContent.setSize(copyTask.getSize());
        
        neverCopyContentDAO.create(neverCopyContent);
    }

}
