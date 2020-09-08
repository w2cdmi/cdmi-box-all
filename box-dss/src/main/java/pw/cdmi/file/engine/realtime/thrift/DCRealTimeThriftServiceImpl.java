/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.thrift;


import java.security.SecureRandom;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.thrift.realTime.dc2app.CopyTaskInfo;
import com.huawei.sharedrive.thrift.realTime.dc2app.DCRealTimeThriftService;
import com.huawei.sharedrive.thrift.realTime.dc2app.TBusinessException;

import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.file.engine.manage.datacenter.domain.ClusterNode;
import pw.cdmi.file.engine.mirro.task.ClusterNodeCache;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTask;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTaskStatus;
import pw.cdmi.file.engine.realtime.printer.RealTimePrinter;
import pw.cdmi.file.engine.realtime.service.RealTimeCopyTaskService;

@Service
public class DCRealTimeThriftServiceImpl implements DCRealTimeThriftService.Iface
{   
    @Autowired
    private RealTimeCopyTaskService realTimeCopyTaskService;
    
    @Autowired
    private ClusterNodeCache clusterNodeCache;
    
    private static final String EXEAGENT = EnvironmentUtils.getHostName();
    
    @Override
    public void addCopyTask(CopyTaskInfo task) throws TBusinessException
    {
        if(task == null)
        {
            RealTimePrinter.error("no task");
            return;
        }
        RealTimePrinter.info("Receive a copy task with id : " + task.getTaskId() + " srcObjectId:"
            + task.getSrcObjectId()+"now date:"+ System.currentTimeMillis());
        RealTimeCopyTask copyTask = this.transferTask(task);
        try
        {
            realTimeCopyTaskService.addCopyTask(copyTask);
        }
        catch(Exception e)
        {
            if(null != realTimeCopyTaskService.getTask(copyTask.getTaskId()))
            {
                RealTimePrinter.error(e.getMessage());
            }
            else
            {
                throw e;
            }
        }
    }

    @Override
    public void batchAddCopyTask(List<CopyTaskInfo> taskList) throws TBusinessException, TException
    {
           
    }

    

    @Override
    public Map<String, String> getTaskExeInfo() throws TBusinessException, TException
    {
        
        return null;
    }

    @Override
    public void manageCopyTask(int arg0, String arg1) throws TBusinessException, TException
    {
        
        
    }
    
    private RealTimeCopyTask transferTask(CopyTaskInfo task)
    {
       
        
        RealTimeCopyTask copyTask = new RealTimeCopyTask();
        copyTask.setTaskId(task.getTaskId());
        copyTask.setNodeSize(task.getSize());
        copyTask.setTriggerType(task.getTriggerType());
        copyTask.setSrcObjectId(task.getSrcObjectId());
        copyTask.setDestObjectId(task.getDestObjectId());
        copyTask.setCreateTime(Calendar.getInstance().getTime());
        copyTask.setModifyTime(copyTask.getCreateTime());
        copyTask.setStatus(RealTimeCopyTaskStatus.WAITING.getCode());
        copyTask.setErrorCode(0);
        
        List<ClusterNode> clusterNodeList = clusterNodeCache.getClusterlst();
        if (CollectionUtils.isEmpty(clusterNodeList))
        {
            RealTimePrinter.warn("clusterNodeList is empty"); 
            copyTask.setExeAgent(EXEAGENT);
        }
        else
        {
            SecureRandom secureRandom = new SecureRandom();
            int i = secureRandom.nextInt(clusterNodeList.size());
            copyTask.setExeAgent(clusterNodeList.get(i).getName());
        }
        
        return copyTask;
    }
}
