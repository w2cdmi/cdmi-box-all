package com.huawei.sharedrive.app.mirror.datamigration.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.manager.FilesInnerManager;
import com.huawei.sharedrive.app.files.manager.ReplaceObjectStatus;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.CopyType;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.exception.CopyTaskErrorCode;
import com.huawei.sharedrive.app.mirror.exception.CopyTaskException;
import com.huawei.sharedrive.app.mirror.manager.BaseHandleCopyTask;
import com.huawei.sharedrive.thrift.mirror.app2dc.CopyTaskExeResult;

@Service("handleDataMigration")
public class HandleDataMigration implements BaseHandleCopyTask
{
    @Autowired
    private FilesInnerManager filesInnerManager;
    
    @Autowired
    private UserDataMigrationTaskManager uerDataMigrationTaskManager;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HandleDataMigration.class);
    
    
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
            throw new CopyTaskException(CopyTaskErrorCode.INTERNAL_SERVER_ERROR);
        }
        
        ObjectReference oldObject = filesInnerManager.getObjectReference(task.getSrcObjectId());
        if(null == oldObject)
        {
            LOGGER.warn(task.getSrcObjectId()+" not existing");
            throw new CopyTaskException(CopyTaskErrorCode.INTERNAL_SERVER_ERROR);
        }

        //相同的存储资源，目标一致,SHA1相同
        if(objRef.getResourceGroupId() == task.getDestResourceGroupId() && oldObject.getSha1().equalsIgnoreCase(objRef.getSha1()))
        { 
            LOGGER.info("handleObjectConflict happen src objectId:"+srcNode.getObjectId()+", dest objectId: "+ task.getDestObjectId());
            
            // 创建新对象，然后替换srcNode的object
            ReplaceObjectStatus status =filesInnerManager.changeNodeObject(task,srcNode, objRef, false);
            
            if(ReplaceObjectStatus.FINISH_STATUS == status)
            {
                LOGGER.info("changeNodeObject true:src objectId:"+srcNode.getObjectId()+", dest objectId: "+ task.getDestObjectId());
                // 更新迁移任务进度
                updateDataMigrationProcess(task);
            }
            else
            {
                LOGGER.info("changeNodeObject false src objectId:"+srcNode.getObjectId()+", dest objectId: "+ task.getDestObjectId()+",status:"+status);
            }

        }
        else
        {
            LOGGER.warn(task.getDestObjectId()+"not same dss");
            throw new CopyTaskException(CopyTaskErrorCode.CONTENT_ERROR);
        }
        
    }
    /**
     * 用户数据迁移的异步复制
     * 
     * @param task
     * @param result
     */
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
        ReplaceObjectStatus status = filesInnerManager.changeNodeObject(task,srcNode, obj, true);
        if(ReplaceObjectStatus.FINISH_STATUS == status)
        {
            // 更新迁移任务进度
            updateDataMigrationProcess(task);
        }
        else
        {
            LOGGER.warn("changeNodeObject failed,status:"+status);
        }

    }
    
    private void updateDataMigrationProcess(CopyTask task)
    {
        LOGGER.info("task.getSrcOwnedBy()"+task.getSrcOwnedBy() +",src "+task.getSrcObjectId()+", dest objectid:"+task.getDestObjectId()+ "size:"+task.getSize());
        uerDataMigrationTaskManager.updateDataMigrationProcess(task.getSrcOwnedBy(), task.getSize());
    }
    
    /**
     * 如果存在相同的对象，则替换object,不在需要迁移了，返回true;否则返回false
     * 
     * @param node
     * @param regionId
     * @return
     */
    public boolean replaceObject(INode node, int regionId)
    {
        List<ObjectReference> objectRefs = filesInnerManager.getSameContentObject(node.getObjectId(),
            regionId);
        
        if (null == objectRefs || objectRefs.isEmpty())
        {
            return false;
        }
        
        try
        {
            // 替换Object
            CopyTask task=new CopyTask();
            task.setCopyType(CopyType.COPY_TYPE_USER_DATA_MIGRATION.getCopyType());
            task.setPolicyId(MirrorCommonStatic.DEFAULT_POLICY_ID);
            ReplaceObjectStatus status =  filesInnerManager.changeNodeObject(task,node, objectRefs.get(0), false);
            
            if(ReplaceObjectStatus.FINISH_STATUS == status || ReplaceObjectStatus.SRC_NOT_EXIST_STATUS == status)
            {
                LOGGER.info("change node's object succeed or src node exsiting ,status:"+status);
                return true;
            }
            
            return false;
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 对整个任务是否已经存在相同的数据
     * 
     * @param task
     * @return
     */
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
    
}
