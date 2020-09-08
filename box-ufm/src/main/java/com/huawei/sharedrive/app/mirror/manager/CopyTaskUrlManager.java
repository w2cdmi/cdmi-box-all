package com.huawei.sharedrive.app.mirror.manager;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.manager.FilesInnerManager;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.exception.CopyTaskErrorCode;
import com.huawei.sharedrive.app.mirror.exception.CopyTaskException;
import com.huawei.sharedrive.app.mirror.service.CopyTaskService;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.thrift.mirror.app2dc.CopyTaskMinor;
import com.huawei.sharedrive.thrift.mirror.app2dc.ObjectDownloadURL;

import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.MethodLogAble;

@Service("copyTaskUrlManager")
@Lazy(false)
public class CopyTaskUrlManager
{
    
    @Autowired
    private CopyTaskService copyTaskService;

    
    @Autowired
    private FilesInnerManager filesInnerManager;
    
    
    @Autowired
    private DCManager dcManager;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyTaskUrlManager.class);
    
    /**
     * 批量获取下载对象
     * 
     * @param tasks
     * @return
     */
    public List<ObjectDownloadURL> batchGetDownloadUrl(List<CopyTaskMinor> tasks)
    {
        List<ObjectDownloadURL> lst = new ArrayList<ObjectDownloadURL>(5);
        ObjectDownloadURL url = null;
        for (CopyTaskMinor task : tasks)
        {
            url = getDownloadUrl(task.getTaskId(), task.getSrcObjectId());
            lst.add(url);
            
        }
        return lst;
        
    }
    
    /**
     * 获取下载地址
     * 
     * @param taskId
     * @param srcObject
     * @return
     */
    @MethodLogAble
    public ObjectDownloadURL getDownloadUrl(String taskId, String srcObject)
    {
        if (StringUtils.isEmpty(taskId) || StringUtils.isEmpty(srcObject))
        {
            LOGGER.info("The taskID or object is null ");
            return null;
        }
        ObjectDownloadURL reTask = new ObjectDownloadURL();
        reTask.setTaskId(taskId);
        reTask.setSrcObjectId(srcObject);
        try
        {
            CopyTask task = checkTaskValidAndACL(taskId, srcObject);
            INode iNode = MirrorCommonStatic.getSrcNode(task);
            ResourceGroup group = dcManager.getResourceGroup(task.getDestResourceGroupId());
            if (iNode == null || group == null)
            {
                LOGGER.info("The iNode or group is null ");
                return null;
            }
            DataAccessURLInfo urlInfo = filesInnerManager.getDownloadURL(iNode,
                User.SYSTEM_USER_ID,
                group.getRegionId());
            
            //内部下载不记录日志
            
            reTask.setUrl(urlInfo.getDownloadUrl());
            
        }
        catch (NoSuchItemsException e)
        {
            LOGGER.error(e.getMessage(), e);
            reTask.setErrorCode(CopyTaskErrorCode.OBJECT_NOTFOUND.getErrCode());
            reTask.setMsg(CopyTaskErrorCode.OBJECT_NOTFOUND.getMsg());
            reTask.setUrl(null);
        }
        catch (CopyTaskException e)
        {
            LOGGER.error(e.getMessage(), e);
            reTask.setErrorCode(e.getCode());
            reTask.setMsg(e.getMessage());
            reTask.setUrl(null);
        }
        catch (BaseRunException e)
        {
            LOGGER.error(e.getMessage(), e);
            reTask.setErrorCode(e.getHttpcode().value());
            reTask.setMsg(e.getCode());
            reTask.setUrl(null);
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            reTask.setErrorCode(CopyTaskErrorCode.INTERNAL_SERVER_ERROR.getErrCode());
            reTask.setMsg(CopyTaskErrorCode.INTERNAL_SERVER_ERROR.getMsg());
            reTask.setUrl(null);
        }
        
        return reTask;
    }
    
    
    
    
    /**
     * 检查任务的合法性
     * 
     * @param taskId
     * @param srcObject
     */
    @MethodLogAble(Level.INFO)
    private CopyTask checkTaskValidAndACL(String taskId, String srcObject)
    {
        if (null == taskId || taskId.isEmpty() || null == srcObject || srcObject.isEmpty())
        {
            throw new CopyTaskException(CopyTaskErrorCode.PARAMTER_INVALID);
        }
        CopyTask task = copyTaskService.getCopyTask(taskId);
        if (null == task || !srcObject.equalsIgnoreCase(task.getSrcObjectId()))
        {
            throw new CopyTaskException(CopyTaskErrorCode.TASK_ID_NOTFOUND);
        }
        return task;
    }
    
}
