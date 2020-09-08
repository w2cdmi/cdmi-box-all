package com.huawei.sharedrive.app.mirror.thrift.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.mirror.exception.CopyTaskErrorCode;
import com.huawei.sharedrive.app.mirror.exception.CopyTaskException;
import com.huawei.sharedrive.app.mirror.manager.CopyTaskManager;
import com.huawei.sharedrive.app.mirror.manager.CopyTaskUrlManager;
import com.huawei.sharedrive.thrift.mirror.app2dc.CopyTaskExeResult;
import com.huawei.sharedrive.thrift.mirror.app2dc.CopyTaskMinor;
import com.huawei.sharedrive.thrift.mirror.app2dc.MirrorThriftService;
import com.huawei.sharedrive.thrift.mirror.app2dc.ObjectDownloadURL;
import com.huawei.sharedrive.thrift.mirror.app2dc.ReportResultHandleInfo;
import com.huawei.sharedrive.thrift.mirror.app2dc.TBusinessException;

import pw.cdmi.core.exception.InnerException;

/**
 * Mirror需要实现的thrift实例
 * 
 * @author c00287749
 * 
 */
public class MirrorThriftServiceImpl implements MirrorThriftService.Iface
{
    
    @Autowired
    private CopyTaskManager copyTaskManager;
    
    @Autowired
    private CopyTaskUrlManager copyTaskUrlManager;
    
    /**
     * 获取下载地址
     */
    @Override
    public ObjectDownloadURL getDownloadUrl(String taskId, String srcObject) throws TBusinessException
    {
        return copyTaskUrlManager.getDownloadUrl(taskId, srcObject);
    }
    
    /**
     * 上报执行结果
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    @Override
    public void reportCopyTaskExeResult(CopyTaskExeResult result) throws TBusinessException
    {
        try
        {
            copyTaskManager.reportCopyTaskExeResult(result);
        }
        catch (CopyTaskException e1)
        {
            throw new TBusinessException(e1.getCode(),e1.getMessage());
        }
        catch (Exception e)
        {
            throw new InnerException(e);
        }
        
    }
    
    /**
     * 批量获取下载地址
     */
    
    @Override
    public List<ObjectDownloadURL> batchGetDownloadUrl(List<CopyTaskMinor> tasks) throws TBusinessException
    {
        return copyTaskUrlManager.batchGetDownloadUrl(tasks);
    }
    
    @Override
    public List<ReportResultHandleInfo> batchReportCopyTaskExeResult(List<CopyTaskExeResult> results)
    {
        
        if (null == results || results.isEmpty())
        {
            return null;
        }
        
        List<ReportResultHandleInfo> relstInfo = new ArrayList<ReportResultHandleInfo>(10);
        ReportResultHandleInfo info = null;
        for (CopyTaskExeResult result : results)
        {
            try
            {
                copyTaskManager.reportCopyTaskExeResult(result);
            }
            catch (CopyTaskException e)
            {
                info = new ReportResultHandleInfo();
                info.setTaskId(result.getTaskId());
                info.setErrorCode(e.getCode());
                info.setMsg(e.getMessage());
                relstInfo.add(info);
            }
            catch (BaseRunException e)
            {
                info = new ReportResultHandleInfo();
                info.setTaskId(result.getTaskId());
                info.setErrorCode(e.getHttpcode().value());
                info.setMsg(e.getMessage());
                relstInfo.add(info);
            }
            catch (Exception e)
            {
                info = new ReportResultHandleInfo();
                info.setTaskId(result.getTaskId());
                info.setErrorCode(CopyTaskErrorCode.INTERNAL_SERVER_ERROR.getErrCode());
                info.setMsg(CopyTaskErrorCode.INTERNAL_SERVER_ERROR.getMsg());
                relstInfo.add(info);
            }
        }
        
        return relstInfo;
        
    }
    
}
