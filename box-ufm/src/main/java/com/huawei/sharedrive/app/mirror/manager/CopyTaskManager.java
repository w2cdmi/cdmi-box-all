package com.huawei.sharedrive.app.mirror.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.manager.FilesInnerManager;
import com.huawei.sharedrive.app.mirror.appdatamigration.manager.AppDataMigrationManager;
import com.huawei.sharedrive.app.mirror.appdatamigration.manager.HandleForAppDataMigrationClearData;
import com.huawei.sharedrive.app.mirror.appdatamigration.manager.HandleForAppDataMigrationPersistedData;
import com.huawei.sharedrive.app.mirror.datamigration.manager.HandleDataMigration;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.CopyType;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.exception.CopyTaskErrorCode;
import com.huawei.sharedrive.app.mirror.exception.CopyTaskException;
import com.huawei.sharedrive.app.mirror.service.CopyTaskService;
import com.huawei.sharedrive.thrift.mirror.app2dc.CopyTaskExeResult;

import pw.cdmi.box.app.convertservice.domain.TaskBean;
import pw.cdmi.box.app.convertservice.service.ConvertService;
import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.MethodLogAble;

@Service("copyTaskManager")
@Lazy(false)
public class CopyTaskManager
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyTaskManager.class);
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    @Autowired
    private FilesInnerManager filesInnerManager;
    
    @Autowired
    private HandleMirrorCopy handleMirrorCopy;
    
    @Autowired
    private HandleDataMigration handleDataMigration;
    
    @Autowired
    private HandleForAppDataMigrationPersistedData handleForAppDataMigrationPersistedData;
    
    @Autowired
    private HandleForAppDataMigrationClearData handleForAppDataMigrationClearData;
    
    @Autowired
    private MirrorObjectManager mirrorObjectManager;
    
    @Autowired
    private AppDataMigrationManager appDataMigrationManager;
    
    @Autowired
    private ConvertService convertService;
    /**
     * 过滤掉相同的任务
     * 
     * @param lstCopyTask
     * @return
     */
    public List<CopyTask> filterSameTask(List<CopyTask> lstCopyTask)
    {
        List<CopyTask> relstCopyTask = new ArrayList<CopyTask>(1);
        
        CopyTask sameTask = null;
        for (CopyTask task : lstCopyTask)
        {
            sameTask = copyTaskService.checkSameMirrorTask(task);
            
            if (null == sameTask)
            {
                relstCopyTask.add(task);
            }
            else
            {
                LOGGER.info("task is existing ,srcObject:" + sameTask.getSrcObjectId() + ",resourceGroupID:"
                    + task.getDestResourceGroupId());
            }
        }
        
        return relstCopyTask;
    }
    
    /**
     * 处理重删事件的任务
     * 
     * @param srcNode
     * @param destNode
     */
    public void handleDedupMsg(INode srcNode, INode destNode)
    {
        List<CopyTask> lstTask = copyTaskService.getCopyTaskBySrcObjectId(destNode.getObjectId());
        if (null == lstTask || lstTask.isEmpty())
        {
            // 这里还应该去查询关系表中是否有记录使用了就得object
            mirrorObjectManager.handleDedupMsg(srcNode, destNode);
            return;
        }
        
        for (CopyTask task : lstTask)
        {
            
            if (!filesInnerManager.checkObjectExistingForINode(task.getSrcOwnedBy(), task.getSrcObjectId()))
            {
                // 发生 了重删事件。
                task.setSrcObjectId(srcNode.getObjectId());
                copyTaskService.updateCopyTask(task);
            }
        }
    }
    
    /**
     * 上报执行结果
     * 
     * @param result
     */
    @MethodLogAble
    public void reportCopyTaskExeResult(CopyTaskExeResult result)
    {
        // 检查任务执行执行接口
        CopyTask task = checkTaskResultValid(result);
        if (null == task)
        {
            LOGGER.warn("task exe error");
            return;
        }
        
        BaseHandleCopyTask getInstance = getHandleCopyTaskInstance(task);
        if (null == getInstance)
        {
            LOGGER.warn("The copy type do not support,copy type:" + task.getCopyType());
            return;
        }
        
        // 处理复制结果
        getInstance.handleCopyResult(task, result);
        
        //完成之后
        afterCopyTaskComplete(task);      
        
        addConvertFileTask(task.getSrcOwnedBy(),task.getSrcObjectId());
    }
    
    /**
     * 校验执行接口是否正确
     * 
     * @param result
     * @return
     */
    @MethodLogAble(Level.DEBUG)
    private CopyTask checkTaskResultValid(CopyTaskExeResult result)
    {
        if (null == result)
        {
            throw new CopyTaskException(CopyTaskErrorCode.PARAMTER_INVALID);
        }
        
        CopyTask task = copyTaskService.getCopyTask(result.getTaskId());
        
        // 判断任务是否存在，不检查状态
        if (null == task)
        {
            throw new CopyTaskException(CopyTaskErrorCode.TASK_ID_NOTFOUND);
        }
        
        if (!task.getSrcObjectId().equalsIgnoreCase(result.getSrcObjectId())
            || !task.getDestObjectId().equalsIgnoreCase(result.getDestObjectId()))
        {
            throw new CopyTaskException(CopyTaskErrorCode.TASK_ID_NOTFOUND);
        }
        
        if (result.getResult() != CopyTaskErrorCode.TASK_EXE_SUCCESSED.getErrCode())
        {
            handleFailedTask(result, task);
            return null;
        }
        handleSuccessedTask(result, task);
        return task;
        
    }
    
    /**
     * 处理返回错误码的任务
     * 
     * @param result
     * @param task
     */
    private void handleFailedTask(CopyTaskExeResult result, CopyTask task)
    {
        LOGGER.error("source Obejct:" + result.getSrcObjectId() + ",dest objectid:"
            + result.getDestObjectId() + ",exe result:" + result.getResult());
        
        if (task.getCopyType() == CopyType.COPY_TYPE_APP_MIGRATION_PERSISTED_OLD_DATA.getCopyType()
            || task.getCopyType() == CopyType.COPY_TYPE_APP_MIGRATION_CLEAR_OLD_DATA.getCopyType())
        {
            appDataMigrationManager.hasFailedFile(task, CopyTaskErrorCode.CONTENT_ERROR.getMsg()
                + " or others");
            copyTaskService.deleteCopyTask(task);
            return;
        }
        
        //
        if (CopyTaskErrorCode.CONTENT_ERROR.getErrCode() == result.getResult())
        {
            // 重新生成任务
            task.setPop(false);
            task.setModifiedAt(new Date());
            task.setState(MirrorCommonStatic.TASK_STATE_WAITTING);
            
            // 删除原来的旧任务
            copyTaskService.deleteCopyTask(task);
            
            // 处理执行异常的对象,dss已经存在垃圾数据，需要删除，但是防止误删除，因此先查询对象是否存在，不存在就删除
            ObjectReference objRef = filesInnerManager.getObjectReference(task.getDestObjectId());
            if (null == objRef)
            {
                // 只删除DSS的对象，不做任何事情
                filesInnerManager.deleteDSSObject(task.getDestResourceGroupId(), task.getDestObjectId());
            }
            
            task.setDestObjectId(filesInnerManager.buildObjectID());
            // 根据新的内容生成一个新的task（和更新相似）
            copyTaskService.saveSigleCopyTask(task);
        }
        else
        {
            // 更新数据库状态
            task.setState(MirrorCommonStatic.TASK_STATE_FAILED);
            task.setExeResult(result.getResult());
            task.setPop(false);
            task.setModifiedAt(new Date());
            
            // 更新任务状态
            copyTaskService.updateCopyTask(task);
        }
    }
    
    /**
     * 处理执行成功的任务
     * 
     * @param result
     * @param task
     */
    private void handleSuccessedTask(CopyTaskExeResult result, CopyTask task)
    {
        
        // 检验员原INode是否存在
        INode node = MirrorCommonStatic.getDestNode(task);
        INode srcNode = MirrorCommonStatic.getSrcNode(task);
        if (null == node || null == srcNode)
        {
            LOGGER.warn("node or srcNode is null ");
            return;
        }
        
        List<INode> lstNode = filesInnerManager.getObject(srcNode.getOwnedBy(), srcNode.getObjectId());
        
        if (null == lstNode || lstNode.isEmpty())
        {
            // 原对象不存在，删除任务
            LOGGER.error("It had happen major error, inode not existing" + ",it's owner:"
                + srcNode.getOwnedBy() + ",id:" + srcNode.getId() + ",objectid:" + srcNode.getObjectId()
                + ",need delete task:" + task.getTaskId());
            
            if (task.getCopyType() == CopyType.COPY_TYPE_APP_MIGRATION_PERSISTED_OLD_DATA.getCopyType()
                || task.getCopyType() == CopyType.COPY_TYPE_APP_MIGRATION_CLEAR_OLD_DATA.getCopyType())
            {
                appDataMigrationManager.hasFailedFile(task, CopyTaskErrorCode.OBJECT_NOTFOUND.getMsg());
            }
            
            copyTaskService.deleteCopyTask(task);
            
            throw new CopyTaskException(CopyTaskErrorCode.OBJECT_NOTFOUND);
        }
        
        // 数据一致性校验，校验数据大小，MD5值
        ObjectReference obj = filesInnerManager.getObjectReference(task.getSrcObjectId());
        
        // 判断是否被删除，如果删除则包对象找不到
        if (null == obj)
        {
            LOGGER.error("obj not found,may be had deleted,src objectId:" + task.getSrcObjectId());
            
            if (task.getCopyType() == CopyType.COPY_TYPE_APP_MIGRATION_PERSISTED_OLD_DATA.getCopyType()
                || task.getCopyType() == CopyType.COPY_TYPE_APP_MIGRATION_CLEAR_OLD_DATA.getCopyType())
            {
                appDataMigrationManager.hasFailedFile(task, CopyTaskErrorCode.OBJECT_NOTFOUND.getMsg());
            }
            
            copyTaskService.deleteCopyTask(task);
            
            throw new CopyTaskException(CopyTaskErrorCode.OBJECT_NOTFOUND);
        }
        
        if (MirrorCommonStatic.MD5_LENGTH == obj.getSha1().length())
        {
            // 新数据校验MD5
            if (!obj.getSha1().equalsIgnoreCase(result.getMd5()) || (obj.getSize() != result.getSize()))
            {
                LOGGER.error("obj SHA1:" + obj.getSha1() + ",result md5:" + result.getMd5() + ",size "
                    + obj.getSize() + ",result size;" + result.getSize());
                
                dealFailedTaskForMigration(task, CopyTaskErrorCode.CONTENT_ERROR.getMsg());
                throw new CopyTaskException(CopyTaskErrorCode.CONTENT_ERROR);
            }
            
            if (!StringUtils.isEmpty(obj.getBlockMD5())
                && !obj.getBlockMD5().equalsIgnoreCase(result.getDataBlockMd5()))
            {
                LOGGER.error("obj getBlockMD5:" + obj.getBlockMD5() + ",result getDataBlockMd5:"
                    + result.getDataBlockMd5());
                
                dealFailedTaskForMigration(task, CopyTaskErrorCode.CONTENT_ERROR.getMsg());
                throw new CopyTaskException(CopyTaskErrorCode.CONTENT_ERROR);
            }
        }
        else
        {
            // 老数据校验大小
            if (obj.getSize() != result.getSize())
            {
                dealFailedTaskForMigration(task, CopyTaskErrorCode.CONTENT_ERROR.getMsg());
                throw new CopyTaskException(CopyTaskErrorCode.CONTENT_ERROR);
            }
        }
    }
    
    private void dealFailedTaskForMigration(CopyTask task, String reason)
    {
        if (task.getCopyType() == CopyType.COPY_TYPE_APP_MIGRATION_PERSISTED_OLD_DATA.getCopyType()
            || task.getCopyType() == CopyType.COPY_TYPE_APP_MIGRATION_CLEAR_OLD_DATA.getCopyType())
        {
            appDataMigrationManager.hasFailedFile(task, reason);
            copyTaskService.deleteCopyTask(task);
        }
    }
    
    /**
     * 任务完成后，删除任务，记录日志
     * 
     * @param task
     */
    public void afterCopyTaskComplete(CopyTask task)
    {
        LOGGER.info("task ObjectId" + task.getSrcObjectId() + ",dest DestObjectId " + task.getDestObjectId());
        
        copyTaskService.deleteCopyTask(task);
        
        // 不记录操作日志，减轻压力
        // copyTaskLogger.sendCopyEvent(task);
        
    }
    
    /**
     * 
     * @param task
     * @return
     */
    
    public BaseHandleCopyTask getHandleCopyTaskInstance(CopyTask task)
    {
        if (CopyType.COPY_TYPE_NEAR.getCopyType() == task.getCopyType()
            || CopyType.COPY_TYPE_RECOVERY.getCopyType() == task.getCopyType())
        {
            return handleMirrorCopy;
        }
        else if (CopyType.COPY_TYPE_USER_DATA_MIGRATION.getCopyType() == task.getCopyType())
        {
            return handleDataMigration;
        }
        else if (CopyType.COPY_TYPE_APP_MIGRATION_PERSISTED_OLD_DATA.getCopyType() == task.getCopyType())
        {
            return handleForAppDataMigrationPersistedData;
        }
        else if (CopyType.COPY_TYPE_APP_MIGRATION_CLEAR_OLD_DATA.getCopyType() == task.getCopyType())
        {
            return handleForAppDataMigrationClearData;
        }
        return null;
    }
    
    public List<Integer> selectAllPolicyId()
    {
        return copyTaskService.selectAllPolicyId();
    }

	/**
	 * 新增转换文件任务
	 */
	private void addConvertFileTask(long srcOwnedBy,String srcObjectId) 
	{
		LOGGER.info("Enter addConvertFileTask!objectId="+srcObjectId+",ownerId="+srcOwnedBy);
		//给转换任务填充信息
		TaskBean taskBean = new TaskBean();
		taskBean.setOwneId(String.valueOf(srcOwnedBy));
		taskBean.setObjectId(srcObjectId);
		//调用转换文件任务新增方法
		convertService.addTask(taskBean);
		LOGGER.info("Exit the addConvertFileTask");
	}
}
