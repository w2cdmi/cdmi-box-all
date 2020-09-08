/**
 * 
 */
package pw.cdmi.file.engine.mirro.thirft;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.thrift.mirror.dc2app.CopyTaskInfo;
import com.huawei.sharedrive.thrift.mirror.dc2app.DCMirrorThriftService;
import com.huawei.sharedrive.thrift.mirror.dc2app.TBusinessException;

import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.file.engine.manage.config.service.SystemConfigManager;
import pw.cdmi.file.engine.mirro.domain.CopyTask;
import pw.cdmi.file.engine.mirro.domain.CopyTaskStatus;
import pw.cdmi.file.engine.mirro.service.CopyTaskService;
import pw.cdmi.file.engine.mirro.task.CopyTaskRecevier;

/**
 * @author w00186884
 * 
 */
@Service
public class DCMirrorThriftServiceImpl implements DCMirrorThriftService.Iface
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DCMirrorThriftServiceImpl.class);
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    @Autowired
    private CopyTaskRecevier copyTaskRecevier;
    
    @Autowired
    private SystemConfigManager sysConfigManager;
    
    @MethodLogAble
    @Override
    public void addCopyTask(CopyTaskInfo task) throws TBusinessException
    {
        // 如果接受任务线程没启动，侧接受任务失败
        if (!copyTaskRecevier.isStart())
        {
            LOGGER.warn("The Receive a copy task does't started,can't receive any task.");
            throw new TBusinessException();
        }
        LOGGER.debug("Receive a copy task with id : " + task.getTaskId() + " srcObjectId:"
            + task.getSrcObjectId());
        CopyTask copyTask = this.transferTask(task);
        boolean result = copyTaskRecevier.addCopyTask(copyTask);
        if (!result)
        {
            throw new TBusinessException();
        }
    }
    
    private CopyTask transferTask(CopyTaskInfo task)
    {
        CopyTask copyTask = new CopyTask();
        copyTask.setTaskId(task.getTaskId());
        copyTask.setSize(task.getSize());
        copyTask.setSrcObjectId(task.getSrcObjectId());
        copyTask.setDestObjectId(task.getDestObjectId());
        copyTask.setPriority(task.getPriority());
        copyTask.setExeAgent(EnvironmentUtils.getHostName());
        copyTask.setCreatedAt(Calendar.getInstance().getTime());
        copyTask.setModifiedAt(copyTask.getCreatedAt());
        copyTask.setCopyStatus(CopyTaskStatus.INPUT.getCode());
        copyTask.setErrorCode(0);
        return copyTask;
    }
    
    @Override
    public void batchAddCopyTask(List<CopyTaskInfo> lstTask) throws TBusinessException
    {
        if (!copyTaskRecevier.isStart())
        {
            LOGGER.warn("The Receive a copy task does't started,can't receive any task.");
            throw new TBusinessException();
        }
        
        boolean allSuccess = true;
        CopyTask copyTask = null;
        boolean result = false;
        for (CopyTaskInfo taskInfo : lstTask)
        {
            copyTask = this.transferTask(taskInfo);
            result = copyTaskRecevier.addCopyTask(copyTask);
            if (!result)
            {
                allSuccess = false;
                break;
            }
        }
        // 如果部分失败如何处理,尽可能避免，但真发生了就先抛出异常，下次重新来过。
        if (!allSuccess)
        {
            throw new TBusinessException();
        }
    }
    
    @Override
    public void managerCopyTask(int state, String taskId) throws TBusinessException
    {
        if (null == taskId || "".equals(taskId))
        {
            copyTaskService.pauseOrGoTask(state);
            return;
        }
        
        if (SystemConfigManager.MIRROR_GLOBAL_TASK_STATE.equals(taskId))
        {
            copyTaskService.pauseOrGoTask(state);
        }
        // 改变dss系统配置
        sysConfigManager.changeMirrorConfig(taskId, String.valueOf(state));
    }
    
    @Override
    public Map<String, String> getTaskExeInfo() throws TBusinessException
    {
        return copyTaskService.getTaskExeInfo();
    }
}
