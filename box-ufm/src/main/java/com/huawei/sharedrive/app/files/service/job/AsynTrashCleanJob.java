package com.huawei.sharedrive.app.files.service.job;

import java.nio.charset.Charset;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.files.service.TrashServiceV2;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.restv2.task.status.AsyncTaskStatus;
import com.huawei.sharedrive.app.spacestatistics.domain.ClearRecycleBinRecord;
import com.huawei.sharedrive.app.spacestatistics.service.RecordingClearTrashService;

public class AsynTrashCleanJob implements Runnable
{
    
    private static Logger logger = LoggerFactory.getLogger(AsynTrashCleanJob.class);
    
    private String fielCleanTaskPath;
    
    private long ownerId;
    
    private TrashServiceV2 trashServiceV2;
    
    private UserToken userToken;
    
    private CuratorFramework zkClient;
    
    private RecordingClearTrashService recordingClearTrashService;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public AsynTrashCleanJob(UserToken user, String taskPath, TrashServiceV2 trashServiceV2,
        RecordingClearTrashService recordingClearTrashService, CuratorFramework zkClient, long ownerId)
    {
        this.userToken = user;
        this.fielCleanTaskPath = taskPath;
        this.trashServiceV2 = trashServiceV2;
        this.recordingClearTrashService = recordingClearTrashService;
        this.zkClient = zkClient;
        this.ownerId = ownerId;
    }
    
    @Override
    public void run()
    {
        StringBuilder conflictIds = new StringBuilder();
        try
        {
            trashServiceV2.cleanTrash(userToken, ownerId);
            recordingClearTrashService.put(new ClearRecycleBinRecord(ownerId, new Date(),
                userToken.getAccountId()));
        }
        catch (ForbiddenException e)
        {
            conflictIds.append(AsyncTaskStatus.FORBBIDEN);
            logger.error("", e);
        }
        catch (Exception e)
        {
            conflictIds.append(AsyncTaskStatus.SYSTEM_EXCEPTION);
            logger.error("", e);
        }
        deleteOrUpdateZK(fielCleanTaskPath, conflictIds.toString());
        
    }
    
    private void deleteOrUpdateZK(final String taskPath, String errorIds)
    {
        try
        {
            if (StringUtils.isBlank(errorIds))
            {
                zkClient.delete().forPath(taskPath);
            }
            else
            {
                zkClient.setData().forPath(taskPath, errorIds.getBytes(Charset.defaultCharset()));
            }
        }
        catch (Exception e)
        {
            logger.error("update task status error!", e);
        }
    }
    
}
