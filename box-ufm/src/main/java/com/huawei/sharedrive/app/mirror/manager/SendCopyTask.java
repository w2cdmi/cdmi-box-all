package com.huawei.sharedrive.app.mirror.manager;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.core.job.Task;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.files.dao.ObjectReferenceDAO;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.mirror.domain.CopyPolicySiteInfo;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.service.CopyTaskService;
import com.huawei.sharedrive.app.mirror.thrift.client.DCMirrorThriftServiceClient;
import com.huawei.sharedrive.app.utils.PropertiesUtils;
import com.huawei.sharedrive.thrift.mirror.dc2app.CopyTaskInfo;

import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.core.utils.SpringContextUtil;

/**
 * 发送任务到DSS
 * 
 * @author c00287749
 * 
 */
public class SendCopyTask extends Task
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SendCopyTask.class);
    
    private List<CopyTask> lstCopyTask;
    
    private DCManager dcManager;
    
    private DssDomainService dssDomainService;
    
    private CopyTaskService copyTaskService;
    
    // 复制任务管理
    private CopyTaskManager copyTaskManager;
    
    private ObjectReferenceDAO objectReferenceDAO;
    
    public static final String CLASS_NAME = "SendCopyTask";
    
    private static final String  UAS_REGION_NAME = PropertiesUtils.getProperty("uas.region.name", null, PropertiesUtils.BundleName.HWIT);
    
    public SendCopyTask(List<CopyTask> lst)
    {
        this.lstCopyTask = lst;
    }
    
    /**
     * 处理复制数据是否在目的端已经存在，存在则不复制。
     * 
     * @param task
     * @return
     */
    private boolean filterDataExisting(CopyTask task)
    {
        try
        {
            BaseHandleCopyTask instance = copyTaskManager.getHandleCopyTaskInstance(task);
            return instance == null ? false : instance.handleDataIsExistingBeforeCopy(task);
        }
        catch (Exception e)
        {
            LOGGER.info(e.getMessage(), e);
        }
        
        return false;
    }
    
    /**
     * 复制任务下发到DSS中
     * 
     * @param task
     */
    private void sendTaskToDSS(CopyTask task)
    {
        
        if (task.getDestResourceGroupId() == CopyPolicySiteInfo.DEFAULT_VALUE)
        {
            ResourceGroup group = dcManager.selectBestGroup(task.getDestRegionId());
            if (group == null)
            {
                LOGGER.warn("selectBestGroup is null getDestRegionId:" + task.getDestRegionId());
                return;
            }
            task.setDestResourceGroupId(group.getId());
        }
        
        ResourceGroup group = dcManager.getCacheResourceGroup(task.getDestResourceGroupId());
        String domain = dssDomainService.getDomainByDssId(group);
        DCMirrorThriftServiceClient client = null;
        try
        {
            client = new DCMirrorThriftServiceClient(domain, group.getManagePort());
            CopyTaskInfo taskInfo = new CopyTaskInfo();
            taskInfo.setTaskId(task.getTaskId());
            
            if(StringUtils.isNotBlank(UAS_REGION_NAME))
            {
                taskInfo.setSrcObjectId(UAS_REGION_NAME + task.getSrcObjectId());
            }
            else
            {
                taskInfo.setSrcObjectId(task.getSrcObjectId());
            }
            
            taskInfo.setSize(task.getSize());
            taskInfo.setDestObjectId(task.getDestObjectId());
            taskInfo.setPriority(task.getPriority());
            client.addCopyTask(taskInfo);
            task.setDestResourceGroupId(group.getId());
            // 修改任务的数据库状态和资源组
            updateCopyTaskState(task);
            LOGGER.info("send task successed ,object:" + task.getSrcObjectId() + ",dest object:"
                + task.getDestOwnedBy() + ",resourceGroup :" + task.getDestResourceGroupId());
        }
        catch (RuntimeException e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
        finally
        {
            if (client != null)
            {
                client.close();
            }
        }
    }
    
    private void updateCopyTaskState(CopyTask task)
    {
        task.setState(MirrorCommonStatic.TASK_STATE_EXEING);
        // 执行时间
        task.setModifiedAt(new Date());
        
        copyTaskService.updateCopyTask(task);
    }
    
    @MethodLogAble
    @Override
    public void execute()
    {
        LOGGER.debug("SendCopyTask begin");
        initBean();
        
        for (CopyTask task : lstCopyTask)
        {
            // 处理数据是否存在，如果存在则不发送复制任务
            if(!checkSha1ValueNotNull(task))
            {
                continue;
            }
            if (filterDataExisting(task))
            {
                LOGGER.info("handleDataIsExisting is true,getSrcObjectId:" + task.getSrcObjectId()
                    + ",region" + task.getDestRegionId() + " .and delete this copytask " + task.getTaskId());
                // 删除不需要复制的任务
                copyTaskService.deleteCopyTask(task);
                continue;
            }
            
            sendTaskToDSS(task);
        }
        LOGGER.debug("SendCopyTask end");
        
    }
    
    private boolean checkSha1ValueNotNull(CopyTask task)
    {
        if(null == task)
        {
            return false;
        }
        ObjectReference obj = objectReferenceDAO.get(task.getSrcObjectId());
        if(obj == null)
        {
            //如果obj为空，表示源文件object已不存在，可以将任务删除
            copyTaskService.deleteCopyTask(task);
            LOGGER.info("the task's objectreference is null,so delete this task ,the task id is:"+task.getTaskId());
            return false;
        }
        if(StringUtils.isBlank(obj.getSha1()))
        {
            LOGGER.info("the sha1 value of the task's objectreference is blank,so this task will not to send,the task id is:"+task.getTaskId());
            task.setPop(false);
            task.setModifiedAt(new Date());
            copyTaskService.updateCopyTask(task);
            return false;
        }
        return true;
    }
    
    @Override
    public String getName()
    {
        return CLASS_NAME;
    }
    
    /**
     * 初始化Bean
     */
    private void initBean()
    {
        dcManager = (DCManager) SpringContextUtil.getBean("dcManager");
        
        dssDomainService = (DssDomainService) SpringContextUtil.getBean("dssDomainService");
        
        copyTaskService = (CopyTaskService) SpringContextUtil.getBean("copyTaskService");
        
        copyTaskManager = (CopyTaskManager) SpringContextUtil.getBean("copyTaskManager");
        
        objectReferenceDAO = (ObjectReferenceDAO) SpringContextUtil.getBean("objectReferenceDAO");
    }
}
