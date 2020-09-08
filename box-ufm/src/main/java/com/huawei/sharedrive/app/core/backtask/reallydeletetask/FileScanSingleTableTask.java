package com.huawei.sharedrive.app.core.backtask.reallydeletetask;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

import com.huawei.sharedrive.app.common.systemtask.domain.ScanTableInfo;
import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.common.systemtask.service.SystemTaskService;
import com.huawei.sharedrive.app.core.backtask.reallydeletetask.service.ReallyDeleteService;
import com.huawei.sharedrive.app.core.job.Task;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.mirror.manager.MirrorObjectManager;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.core.utils.SpringContextUtil;

public class FileScanSingleTableTask extends Task
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileScanSingleTableTask.class);
    
    private SystemTask scanFileTask = null;
    
    private ScanTableInfo scanTableInfo = null;
    
    private ReallyDeleteService reallyDeleteService;
    
    private SystemTaskService systemTaskService;
    
    private FileBaseService fileBaseService;
    
    private MirrorObjectManager mirrorObjectManager;
    
    private final static int LENGTH = 1000;
    
    public FileScanSingleTableTask(SystemTask task)
    {
        scanFileTask = task;
        reallyDeleteService = (ReallyDeleteService) SpringContextUtil.getBean("reallyDeleteService");
        fileBaseService = (FileBaseService) SpringContextUtil.getBean("fileBaseService");
        systemTaskService = (SystemTaskService) SpringContextUtil.getBean("systemTaskService");
        mirrorObjectManager = (MirrorObjectManager)SpringContextUtil.getBean("mirrorObjectManager");
        
    }
    
    private void pasreTask()
    {
        scanTableInfo = ScanTableInfo.toObject(scanFileTask.getTaskInfo());
    }
    
    /**
     * 彻底删除文件夹
     * 
     * @return
     */
    private int deleteFolderNodeForTable()
    {
        return reallyDeleteService.reallyDeleteFolderNode(scanTableInfo.getDbNumber(), scanTableInfo.getTableNumber(), scanTableInfo.getLastModfied());
    }
    
    /**
     * 删除文件
     * 
     * @param lstDeleteNode
     */
    private void dealNeedDeleteSingleFileNode(List<INode> lstDeleteNode)
    {
        for (INode node : lstDeleteNode)
        {
            // 状态不是彻底删除状态，或者对象不是文件或者版本
            if (node.getStatus() != INode.STATUS_DELETE || (node.getType() != INode.TYPE_FILE && node.getType() != INode.TYPE_VERSION))
            {
                LOGGER.info("node not delete,getId:" + node.getId() + ",getOwnedBy:" + node.getOwnedBy());
                continue;
            }
            // 只允许删除状态，并是文件或者版本的对象去删除
            
            try
            {
                // 删除文件，判断返回值是否为1,不要求做数据一致性，因为没有办法保证
                if (1 == reallyDeleteService.deleteINode(node))
                {
                    // 对象引起计数减1
                    ObjectReference objRf = new ObjectReference();
                    objRf.setId(node.getObjectId());
                    fileBaseService.decreaseRefObjectCount(objRf);
                    
                    LOGGER.info("node not delete,getObjectId:" + node.getObjectId() + ",getOwnedBy:" + node.getOwnedBy());
                    mirrorObjectManager.deleteBySrcObjectIdAndOwnedBy( node.getObjectId(), node.getOwnedBy());
                }
            }
            catch (DataAccessException e)
            {
                LOGGER.error(e.getMessage(), e);
                break;
            }
            
        }
    }
    
    /**
     * 循环删除一个表的数据
     */
    private void deleteFileNodeForTable()
    {
        Limit limit = new Limit();
        limit.setLength(LENGTH);
        limit.setOffset(0L);
        limit.checkInnerParameter();
        List<INode> lstDeleteNode = null;
        for (;;)
        {
            lstDeleteNode = reallyDeleteService.lstDeleteNode(scanTableInfo.getDbNumber(), scanTableInfo.getTableNumber(), scanTableInfo.getLastModfied(), limit);
            
            if (null == lstDeleteNode || lstDeleteNode.isEmpty())
            {
                LOGGER.info("lst delete node 0,table:" + ScanTableInfo.toJsonStr(scanTableInfo));
                return;
            }
            
            // 处理等待删除对象
            dealNeedDeleteSingleFileNode(lstDeleteNode);
            
            // 判断是否退出
            if (lstDeleteNode.size() < LENGTH)
            {
                break;
            }
            
            // 设置新的变化量
            limit.setOffset(limit.getOffset() + lstDeleteNode.size());
            limit.checkInnerParameter();
            
        }
    }
    
    @Override
    public void execute()
    {
        
        try
        {
            LOGGER.info("FileScanSingleTableTask begin:" + scanFileTask.getTaskInfo());
            
            // 解释任务
            pasreTask();
            if (null == scanTableInfo)
            {
                return;
            }
            
            try
            {
                // 更新运行状态为执行中
                systemTaskService.updateExecuteState(SystemTask.TASK_STATE_RUNING, new Date(), scanFileTask.getTaskId());
            }
            catch (BaseRunException e)
            {
                LOGGER.error(e.getMessage(), e);
                return;
            }
            
            // 删除文件夹
            deleteFolderNodeForTable();
            
            // 列举对象
            deleteFileNodeForTable();
            
            scanFileTask.setState(SystemTask.TASK_STATE_END);
            reallyDeleteService.updateTaskState(scanFileTask);
            
            LOGGER.info("FileScanSingleTableTask end:" + scanFileTask.getTaskInfo());
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        
    }
    
    @Override
    public String getName()
    {
        
        return FileScanSingleTableTask.class.getCanonicalName();
    }
    
}
