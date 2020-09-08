package com.huawei.sharedrive.app.mirror.appdatamigration.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.manager.FilesInnerManager;
import com.huawei.sharedrive.app.files.manager.ReplaceObjectStatus;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.exception.CopyTaskErrorCode;
import com.huawei.sharedrive.app.mirror.exception.CopyTaskException;
import com.huawei.sharedrive.app.mirror.manager.BaseHandleCopyTask;
import com.huawei.sharedrive.app.mirror.service.CopyTaskService;
import com.huawei.sharedrive.thrift.mirror.app2dc.CopyTaskExeResult;

/**
 * 保留老数据的迁移，但是不保留老数据的镜像对象和镜像关系
 * @author c00287749
 *
 */
@Service("handleForAppDataMigrationPersistedData")
public class HandleForAppDataMigrationPersistedData implements BaseHandleCopyTask
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HandleForAppDataMigrationPersistedData.class);
    
    @Autowired
    private FilesInnerManager filesInnerManager;
    
    @Autowired
    private AppDataMigrationManager appDataMigrationManager;
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    @Override
    public boolean handleDataIsExistingBeforeCopy(CopyTask task)
    {
        if (null == task)
        {
            return false;
        }
        
        // 如果没有相同的，则看是否重删数据相同的,获取是否存在相同内容的对象。
        List<ObjectReference> lstObjects = filesInnerManager.getSameContentObject(task.getSrcObjectId(),
            task.getDestRegionId());
        if (null == lstObjects || lstObjects.isEmpty())
        {
            LOGGER.info("not find same content object");
            return false;
        }
        
        try
        {
            // 替换Object
            ReplaceObjectStatus status = filesInnerManager.changeNodeObject(task,MirrorCommonStatic.getSrcNode(task),
                lstObjects.get(0),
                false,
                false);
            if(ReplaceObjectStatus.FINISH_STATUS == status)
            {
                // 更新迁移任务进度
                updateDataMigrationProcess(task);
                return true;
            }
            else if(ReplaceObjectStatus.SRC_NOT_EXIST_STATUS == status)
            {
                LOGGER.info("src object not existing ,not need copy");
                return true;
            }
            else
            {
                return false;
            }
        
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 处理对象冲突情况
     * @param task
     * @param result
     */
    private void handleObjectConflict(INode srcNode,CopyTask task)   
    {
        ObjectReference objRef = filesInnerManager.getObjectReference(task.getDestObjectId());
        if(null == objRef)
        {
            LOGGER.warn(task.getDestObjectId()+" not existing");
            
            appDataMigrationManager.hasFailedFile(task,CopyTaskErrorCode.INTERNAL_SERVER_ERROR.getMsg()+" ,"+task.getDestObjectId()+" not existing");
            copyTaskService.deleteCopyTask(task);
            
            throw new CopyTaskException(CopyTaskErrorCode.INTERNAL_SERVER_ERROR);
        }
        
        ObjectReference oldObject = filesInnerManager.getObjectReference(task.getSrcObjectId());
        if(null == oldObject)
        {
            LOGGER.warn(task.getSrcObjectId()+" not existing");
            
            appDataMigrationManager.hasFailedFile(task,CopyTaskErrorCode.INTERNAL_SERVER_ERROR.getMsg()+" ,"+task.getSrcObjectId()+" not existing");
            copyTaskService.deleteCopyTask(task);
            
            throw new CopyTaskException(CopyTaskErrorCode.INTERNAL_SERVER_ERROR);
        }

        //相同的存储资源，目标一致,SHA1相同
        if(objRef.getResourceGroupId() == task.getDestResourceGroupId() && oldObject.getSha1().equalsIgnoreCase(objRef.getSha1()))
        { 
            LOGGER.info("handleObjectConflict happen src objectId:"+srcNode.getObjectId()+", dest objectId: "+ task.getDestObjectId());
            
            // 创建新对象，然后替换srcNode的object
            ReplaceObjectStatus status = null;
            
            try{
                status =filesInnerManager.changeNodeObject(task,srcNode, objRef, false, false);
            }
            catch(Exception e)
            {
                LOGGER.warn("changeNodeObject error");
                appDataMigrationManager.hasFailedFile(task,"changeNodeObject error");
                copyTaskService.deleteCopyTask(task);
                
                throw new InternalServerErrorException(e);
            }
            
            if(ReplaceObjectStatus.FINISH_STATUS == status)
            {
                LOGGER.info("changeNodeObject true:src objectId:"+srcNode.getObjectId()+", dest objectId: "+ task.getDestObjectId());
                // 更新迁移任务进度
                updateDataMigrationProcess(task);
            }
            else
            {
                //不抛异常则不手动删除任务，抛异常则手动删除
                appDataMigrationManager.hasFailedFile(task,"changeNodeObject false src objectId:"+srcNode.getObjectId()+", dest objectId: "+ task.getDestObjectId()+",status:"+status);
                
                LOGGER.info("changeNodeObject false src objectId:"+srcNode.getObjectId()+", dest objectId: "+ task.getDestObjectId()+",status:"+status);
            }

        }
        else
        {
            LOGGER.warn(task.getDestObjectId()+"not same dss");
            
            appDataMigrationManager.hasFailedFile(task,CopyTaskErrorCode.CONTENT_ERROR.getMsg()+" ,"+task.getSrcObjectId()+" not same dss");
            copyTaskService.deleteCopyTask(task);
            
            throw new CopyTaskException(CopyTaskErrorCode.CONTENT_ERROR);
        }
        
    }
    
    @Override
    public void handleCopyResult(CopyTask task, CopyTaskExeResult result)
    {
        // 创建新对象
        INode srcNode = MirrorCommonStatic.getSrcNode(task);    
        
        ObjectReference obj = null;
        
        
        try
        {
            // 创建对象
            INode node = MirrorCommonStatic.getDestNode(task);
            if (node == null)
            {
                LOGGER.warn("node is null");
                return;
            }

            node.setBlockMD5(result.dataBlockMd5);
            node.setMd5(result.md5);
 
            
            obj = filesInnerManager.createObject(node);
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
            handleObjectConflict(srcNode,task);
            return;
        }
        
        // 创建新对象，然后替换srcNode的object
        ReplaceObjectStatus status = filesInnerManager.changeNodeObject(task,srcNode, obj, true,false);
        if(ReplaceObjectStatus.FINISH_STATUS == status)
        {
            // 更新迁移任务进度
            updateDataMigrationProcess(task);
        }
        else
        {
            LOGGER.warn("changeNodeObject failed,status:"+status);
            
            appDataMigrationManager.hasFailedFile(task,"changeNodeObject failed,status:"+status);
        }
    }
    
    private void updateDataMigrationProcess(CopyTask task)
    {
        appDataMigrationManager.updateDataMigrationProcess(task);
    }
}
