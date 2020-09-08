package com.huawei.sharedrive.app.mirror.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.exception.NodeException;
import com.huawei.sharedrive.app.files.manager.FilesInnerManager;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.CopyType;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.service.CopyTaskService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.restv2.object.domain.StoreLocation;

import pw.cdmi.core.utils.RandomGUID;

@Service("objectManager")
public class ObjectManager
{
    
    @Autowired
    private FilesInnerManager filesInnerManager;
    
    @Autowired
    private CopyTaskManager copyTaskManager;
    
    @Autowired
    private DCManager dcManager;
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    
    @Autowired
    private HandleMirrorCopy handleMirrorCopy;
    
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectManager.class);
    
    private static final int DEFAULT_VALUE = 0;
    
    // MIRROR已經存在
    public static final int MIRROR_EXISTING = 200;
    
    // 任務已經嘗過
    public static final int MIRROR_TASK_CREATE_SUCCESSED = 202;

    /**
     * storerRequest 參數檢查
     * 
     * @param storerRequest
     */
    private void checkStorerRequest(List<StoreLocation> storerRequest)
    {
        if (null == storerRequest || storerRequest.isEmpty())
        {
            LOGGER.info("storerRequest  is null.");
            throw new BadRequestException();
        }
        
        for (StoreLocation location : storerRequest)
        {
            if (location.getResourceGroupId() == DEFAULT_VALUE)
            {
                if (!dcManager.checkRegionAndResoureGroupExisting(Integer.valueOf(location.getRegionId()),
                    null))
                {
                    LOGGER.info("region or group error,region:" + location.getRegionId() + ",group"
                        + location.getResourceGroupId());
                    throw new BadRequestException();
                }
            }
            else
            {
                if (!dcManager.checkRegionAndResoureGroupExisting(Integer.valueOf(location.getRegionId()),
                    Integer.valueOf(location.getResourceGroupId())))
                {
                    LOGGER.info("region or group error,region:" + location.getRegionId() + ",group"
                        + location.getResourceGroupId());
                    throw new BadRequestException();
                }
            }
            
        }
    }
    
    /**
     * 创建对象Mirror任务
     * 
     * @param userToken
     * @param ownerId
     * @param objectId
     * @param storerRequest
     */
    public int createObjectMiror(UserToken userToken, Long ownerId, String objectId,
        List<StoreLocation> storerRequest)
    {
        if (null == ownerId || null == userToken || null == storerRequest || storerRequest.isEmpty())
        {
            LOGGER.info("ownerId or userToken  is null.");
            throw new BadRequestException();
        }
        
        // 參數檢查
        checkStorerRequest(storerRequest);
        
        /**
         * 治允許自己操作
         */
        if (ownerId != userToken.getId())
        {
            LOGGER.info("oper id not owner,owner id:" + ownerId + ",oper id:" + userToken.getId());
            throw new ForbiddenException();
        }
        
        List<INode> lstNode = filesInnerManager.getObject(ownerId, objectId);
        
        if (null == lstNode || !lstNode.isEmpty())
        {
            LOGGER.info("user " + ownerId + " not existing object ,the objectId" + objectId);
            throw new NodeException(objectId);
        }
        
        List<CopyTask> lstTasks = new ArrayList<CopyTask>(10);
        CopyTask task = null;
        for (StoreLocation stor : storerRequest)
        {
            task = bulidObjectMirrorTask(lstNode.get(0), stor);
            if (null != task)
            {
                lstTasks.add(task);
            }
        }
        
        if (lstTasks.isEmpty())
        {
            return MIRROR_EXISTING;
        }
        
        lstTasks = copyTaskManager.filterSameTask(lstTasks);
        // 批量写数据库
        copyTaskService.saveCopyTask(lstTasks);
        
        return MIRROR_TASK_CREATE_SUCCESSED;
        
    }
    
    private CopyTask bulidObjectMirrorTask(INode node, StoreLocation stor)
    
    {
        
        CopyTask task = new CopyTask();
        task.setTaskId(new RandomGUID(true).getValueAfterMD5());
        task.setCopyType(CopyType.COPY_TYPE_NEAR.getCopyType());
        task.setDestRegionId(stor.getRegionId());
        
        // 选择合适的resorucegroup,如果没有合适的，先填写默认值，等后续执行任务的时候在选择一个合适的。
        
        task.setSize(node.getSize());
        task.setSrcINodeId(node.getId());
        task.setSrcObjectId(node.getObjectId());
        task.setSrcRegionId(dcManager.getCacheResourceGroup(node.getResourceGroupId()).getRegionId());
        task.setSrcResourceGroupId(node.getResourceGroupId());
        task.setDestINodeId(node.getId());
        task.setDestObjectId(filesInnerManager.buildObjectID());
        task.setExeType(MirrorCommonStatic.EXE_TYPE_NOW);
        
        task.setState(MirrorCommonStatic.TASK_STATE_WAITTING);
        
        task.setPriority(MirrorCommonStatic.PRIORITY_TYPE_HIGH);
        
        task.setCreatedAt(new Date());
        
        /**
         * 检查需要复制的内容是否已经存在的了
         */
        if (handleMirrorCopy.handleDataIsExistingBeforeCopy(task))
        {
            copyTaskManager.afterCopyTaskComplete(task);
            
            return null;
        }
        
        return task;
    }
}
