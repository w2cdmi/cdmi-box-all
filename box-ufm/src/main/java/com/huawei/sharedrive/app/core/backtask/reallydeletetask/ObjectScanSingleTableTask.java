package com.huawei.sharedrive.app.core.backtask.reallydeletetask;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.common.systemtask.domain.ScanTableInfo;
import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.core.backtask.reallydeletetask.service.ReallyDeleteService;
import com.huawei.sharedrive.app.core.job.Task;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.dataserver.thrift.client.FileObjectThriftServiceClient;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.core.utils.SpringContextUtil;

@Component("ObjectScanSingleTableTask")
public class ObjectScanSingleTableTask extends Task
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectScanSingleTableTask.class);
    
    private final static int LENGTH = 1000;
    
    private DCManager dcManager;
    
    private DssDomainService dssDomainService;
    
    private ReallyDeleteService reallyDeleteService;
    
    private SystemTask scanObjecttask = null;
    
    private ScanTableInfo scanTableInfo = null;
    
    private String selfPrivateIp;
    
    public ObjectScanSingleTableTask(SystemTask task)
    {
        scanObjecttask = task;
        selfPrivateIp = PropertiesUtils.getProperty("self.privateAddr", "127.0.0.1");
        reallyDeleteService = (ReallyDeleteService) SpringContextUtil.getBean("reallyDeleteService");
        dcManager = (DCManager) SpringContextUtil.getBean("dcManager");
        dssDomainService = (DssDomainService) SpringContextUtil.getBean("dssDomainService");
    }
    
    @Override
    public void execute()
    {
        
        try
        {
            LOGGER.info("ObjectScanSingleTableTask begin:" + scanObjecttask.getTaskInfo());
            // 解释任务
            pasreTask();
            if (null == scanTableInfo)
            {
                return;
            }
            
            try
            {
                scanObjecttask.setExeAgent(selfPrivateIp);
                scanObjecttask.setExeUpdateTime(new Date());
                scanObjecttask.setState(SystemTask.TASK_STATE_RUNING);
                reallyDeleteService.updateExeAgent(scanObjecttask);
            }
            catch (BaseRunException e)
            {
                LOGGER.error(e.getMessage(), e);
                return;
            }
            
            // 清除对象
            deleteObjectForTable();
            
            scanObjecttask.setState(SystemTask.TASK_STATE_END);
            reallyDeleteService.updateTaskState(scanObjecttask);
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        
    }
    
    @Override
    public String getName()
    {
        
        return ObjectScanSingleTableTask.class.getCanonicalName();
    }
    
    /**
     * 
     * @param lstObjectRef
     */
    private void dealNeedDeleteSingleObject(List<ObjectReference> lstObjectRef)
    {
        FileObjectThriftServiceClient client = null;
        ResourceGroup group = null;
        String domain = null;
        for (ObjectReference objectRef : lstObjectRef)
        {
            // 如果还存在引起计数就不删除
            if (objectRef.getRefCount() > 0)
            {
                LOGGER.error("refcount>0 :" + objectRef.getId() + ',' + objectRef.getRefCount());
                continue;
            }
            try
            {
                // 删除thirif对象
                // 如果執行了重刪，則刪除老對象
                // 删除对象
                group = dcManager.getCacheResourceGroup(objectRef.getResourceGroupId());
                domain = dssDomainService.getDomainByDssId(group);
                client = new FileObjectThriftServiceClient(domain, group.getManagePort());
                if (!client.deleteFileObject(objectRef.getId()))
                {
                    // 删除失败添加到任务中
                    LOGGER.warn("deleteFileObject failed" + objectRef.getId());
                    continue;
                }
                // 删除文件，判断返回值是否为1
                if (1 != reallyDeleteService.deleteObjectReferenceCheckRef(objectRef))
                {
                    LOGGER.warn("delete deleteObjectReferenceCheckRef error");
                }
                else
                {
                    // 删除SHA1指纹
                    reallyDeleteService.deleteObjectFingerprintIndex(objectRef);
                }
            }
            catch (DataAccessException e)
            {
                LOGGER.error(e.getMessage(), e);
                break;
            }
            catch (Exception e)
            {
                // 其他异常则继续执行
                LOGGER.warn(e.getMessage(), e);
                continue;
            }
            finally
            {
                if (null != client)
                {
                    client.close();
                }
            }
        }
    }
    
    /**
     * 循环删除一个表的数据
     */
    private void deleteObjectForTable()
    {
        Limit limit = new Limit();
        limit.setLength(LENGTH);
        limit.setOffset(0L);
        limit.checkInnerParameter();
        List<ObjectReference> lstDeleteObjectS = null;
        for (;;)
        {
            lstDeleteObjectS = reallyDeleteService.lstDeleteObject(scanTableInfo.getDbNumber(),
                scanTableInfo.getTableNumber(),
                limit);
            
            if (null == lstDeleteObjectS || lstDeleteObjectS.isEmpty())
            {
                LOGGER.info("not need delete ,dbnumber" + scanTableInfo.getDbNumber() + ",tablenum"
                    + scanTableInfo.getTableNumber());
                break;
            }
            
            // 处理等待删除对象
            dealNeedDeleteSingleObject(lstDeleteObjectS);
            
            // 判断是否退出
            if (lstDeleteObjectS.size() < LENGTH)
            {
                break;
            }
            
            // 设置新的变化量
            limit.setOffset(limit.getOffset() + lstDeleteObjectS.size());
            limit.checkInnerParameter();
        }
    }
    
    private void pasreTask()
    {
        scanTableInfo = ScanTableInfo.toObject(scanObjecttask.getTaskInfo());
    }
    
}
