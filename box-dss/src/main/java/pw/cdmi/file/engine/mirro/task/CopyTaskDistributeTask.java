package pw.cdmi.file.engine.mirro.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.file.engine.manage.datacenter.domain.ClusterNode;
import pw.cdmi.file.engine.mirro.domain.CopyTask;
import pw.cdmi.file.engine.mirro.domain.CopyTaskRunInfo;
import pw.cdmi.file.engine.mirro.printer.MirrorPrinter;
import pw.cdmi.file.engine.mirro.service.CopyTaskService;

@Service("copyTaskDistributeTask")
public class CopyTaskDistributeTask extends QuartzJobTask
{
    @Autowired
    private ClusterNodeCache clusterNodeCache;
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    private static final int INT_ZERO = 0;
    
    private static final int INT_ONE = 1;
    
    private static final int INT_F_ZERO = -1;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        doJob(record);
    }
    
    private void doJob(JobExecuteRecord record)
    {
        
        List<ClusterNode> lstClusterNode = clusterNodeCache.getClusterlst();
        if (CollectionUtils.isEmpty(lstClusterNode))
        {
            MirrorPrinter.info("clusterNode is null ,dss status is wrong");
            return;
        }
        
        List<CopyTask> lstCopyTask = copyTaskService.listExeAgengtIsNullCopyTask();
        if (CollectionUtils.isEmpty(lstCopyTask))
        {
            MirrorPrinter.info("copytask is null");
            return;
        }
        
        List<CopyTaskRunInfo> lstCurrentData = copyTaskService.getTaskRunInfo();
        
        dynamicAllocation(lstCurrentData, lstClusterNode, lstCopyTask.size());
        
        setCopyTaskExeAgent(lstCurrentData);
        // updatePatchCopyTask(lstClusterNode, lstCopyTask.size());
        
        // disPatchTask(lstClusterNode, lstCopyTask);
        // updateCopyTask(lstCopyTask);
        
        record.setOutput("The CopyTaskDistributeTask dispatcher task count [" + lstCopyTask.size() + "]");
        record.setSuccess(true);
        
    }
    
    private void updatePatchCopyTask(List<ClusterNode> lstClusterNode, int copyTaskNum)
    {
        if (copyTaskNum < lstClusterNode.size())
        {
            copyTaskService.setExeAgentForCopyTask(lstClusterNode.get(0).getName(), copyTaskNum);
            return;
        }
        
        int oneSiteNum = copyTaskNum / lstClusterNode.size();
        for (ClusterNode clusterNode : lstClusterNode)
        {
            copyTaskService.setExeAgentForCopyTask(clusterNode.getName(), oneSiteNum);
        }
        
    }
    
    /**
     * 
     * @param lstCurrentData 各个节点当前执行任务数
     * @param lstClusterNode 节点列表（为保证某个节点无任务未被统计到）
     * @param copyTaskNum 待分配任务数
     * @return
     */
    private void dynamicAllocation(List<CopyTaskRunInfo> lstCurrentData, List<ClusterNode> lstClusterNode,
        int copyTaskNum)
    {
        int remainingTask = copyTaskNum;
        if (CollectionUtils.isEmpty(lstCurrentData))
        {
            updatePatchCopyTask(lstClusterNode, copyTaskNum);
            return;
        }
        
        int firstAvg = 0;
        int goalAvg = 0;
        int currentTaskNumber = avgNumber(lstCurrentData);
        
        firstAvg = currentTaskNumber / lstClusterNode.size();
        goalAvg = (currentTaskNumber + copyTaskNum) / lstClusterNode.size();
        
        // 如果有节点没有任务，则添加该节点
        if (lstCurrentData.size() < lstClusterNode.size())
        {
            getCompletedInfo(lstCurrentData, lstClusterNode);
        }
        
        // 给节点排序，根据各节点当前执行任务数从小到大排序
        SortClass.sortList(lstCurrentData);
        
        // 将任务分配给当前任务数较少的节点
        remainingTask = addTask(lstCurrentData, remainingTask, firstAvg);
        
        // 将任务较少节点任务补充道平均值后，还有任务剩余。将任务补充到新的平均值
        if (remainingTask <= 0)
        {
            return;
        }
        
        remainingTask = addTask(lstCurrentData, remainingTask, goalAvg);
        if (remainingTask <= 0)
        {
            MirrorPrinter.info("distribute task successed");
        }
    }
    
    private void getCompletedInfo(List<CopyTaskRunInfo> lstCurrentData, List<ClusterNode> lstClusterNode)
    {
        List<CopyTaskRunInfo> newCurrentData = new ArrayList<CopyTaskRunInfo>(10);
        boolean flag = false;
        for (ClusterNode clusterNode : lstClusterNode)
        {
            flag = false;
            for (CopyTaskRunInfo currentData : lstCurrentData)
            {
                if (clusterNode.getName().equalsIgnoreCase(currentData.getExeAgent()))
                {
                    flag = true;
                    break;
                }
            }
            
            if (!flag)
            {
                CopyTaskRunInfo newAgentInfo = new CopyTaskRunInfo();
                newAgentInfo.setAddNum(INT_ZERO);
                newAgentInfo.setCopystatus(INT_ONE);
                newAgentInfo.setExeAgent(clusterNode.getName());
                newAgentInfo.setTaskNum(INT_ZERO);
                newCurrentData.add(newAgentInfo);
            }
        }
        
        lstCurrentData.addAll(INT_ZERO, newCurrentData);
    }
    
    private int addTask(List<CopyTaskRunInfo> lstCurrentData, int lastTaskNum, Integer avg)
    {
        int reNum = lastTaskNum;
        int tempAddnum = 0;
        int tempCurrnum = 0;
        for (CopyTaskRunInfo taskRunInfo : lstCurrentData)
        {
            if (taskRunInfo.getTaskNum() < avg)
            {
                tempCurrnum = taskRunInfo.getTaskNum();
                tempAddnum = avg - tempCurrnum;
                taskRunInfo.setAddNum(tempAddnum + taskRunInfo.getAddNum());
                tempCurrnum = tempCurrnum + tempAddnum;
                taskRunInfo.setTaskNum(tempCurrnum);
                reNum = reNum - tempAddnum;
            }
            
            if (reNum <= 0)
            {
                break;
            }
        }
        return reNum;
    }
    
    private int avgNumber(List<CopyTaskRunInfo> lstCurrentData)
    {
        int re = 0;
        for (CopyTaskRunInfo copyTaskRunInfo : lstCurrentData)
        {
            re = re + copyTaskRunInfo.getTaskNum();
        }
        return re;
    }
    
    private static final class SortClass
    {
        private SortClass()
        {
            
        }
        
        public static void sortList(List<CopyTaskRunInfo> lstCurrentData)
        {
            Collections.sort(lstCurrentData, new Comparator<CopyTaskRunInfo>()
            {
                @Override
                public int compare(CopyTaskRunInfo o1, CopyTaskRunInfo o2)
                {
                    if (o1.getTaskNum().intValue() > o2.getTaskNum().intValue())
                    {
                        return INT_ONE;
                    }
                    else if (o1.getTaskNum().intValue() == o2.getTaskNum().intValue())
                    {
                        return INT_ZERO;
                    }
                    
                    return INT_F_ZERO;
                }
            });
        }
    }
    
    private void setCopyTaskExeAgent(List<CopyTaskRunInfo> lstCurrentData)
    {
        for (CopyTaskRunInfo copyTaskRunInfo : lstCurrentData)
        {
            if (copyTaskRunInfo.getAddNum() > 0)
            {
                copyTaskService.setExeAgentForCopyTask(copyTaskRunInfo.getExeAgent(),
                    copyTaskRunInfo.getAddNum());
            }
        }
    }
    
}
