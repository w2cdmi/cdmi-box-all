/* 
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.service.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.thrift.app2dc.TBusinessException;

import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.core.job.RetryJob;
import pw.cdmi.file.engine.core.job.RetryJob.Executor;
import pw.cdmi.file.engine.manage.InnerLoadBalanceManager;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;
import pw.cdmi.file.engine.mirro.service.CopyTaskService;
import pw.cdmi.file.engine.mirro.util.CopyTaskTool;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.MultipartFileObject;
import pw.cdmi.file.engine.object.exception.CallbackFailedException;
import pw.cdmi.file.engine.object.service.CallBackService;
import pw.cdmi.file.engine.object.thrift.FileObjectThriftClient;
import pw.cdmi.file.engine.realtime.service.RealTimeCopyTaskService;
import pw.cdmi.file.engine.realtime.util.RealTimeCopyTaskTool;

/**
 * 
 * @author s90006125
 * 
 */
@Service("callBackService")
public class CallBackServiceImpl implements CallBackService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CallBackServiceImpl.class);
    
    private static final String UAS_REGION_NAME_KEY = "uasRegionName";
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    @Autowired
    private RealTimeCopyTaskService realTimeCopyTaskService;
    
    @Autowired
    private ThriftClientProxyFactory ufmThriftClientProxyFactory;
    
    @Autowired
    private ThriftClientProxyFactory szufmThriftClientProxyFactory;
    
    @Autowired
    private ThriftClientProxyFactory dgufmThriftClientProxyFactory;
    
    @Override
    public void abortUpload(String objectID, String callBackKey)
    {
        // 定义可重复执行的JOB
        RetryJob job = createJob(createAbortExecutor(objectID, callBackKey));
        
        // 同步执行JOB
        if (job.execute())
        {
            return;
        }
        String message = "Callback for abort upload Failed [ " + objectID + " ]";
        LOGGER.warn(message, job.getException());
        if (job.getException() instanceof TBusinessException)
        {
            TBusinessException callBackException = (TBusinessException) job.getException();
            throw new CallbackFailedException(HttpStatus.valueOf(callBackException.getStatus()), message);
        }
        
        throw new CallbackFailedException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
    
    @Override
    public void updateFileObject(FileObject fileObject)
    {
        if(RealTimeCopyTaskTool.isRealTimeCallbackKey(fileObject.getCallBackKey()))
        {
            // 回填MD5到Copy任务
            if (fileObject instanceof MultipartFileObject && StringUtils.isNotEmpty(fileObject.getSha1()))
            {
                realTimeCopyTaskService.updateMD5ByObjectId(fileObject);
            }
            
            return;
        }
        LOGGER.info("CallBack For Object " + fileObject.logFormat());
        // 如果是属于Mirro类型的多片对象则放弃Callback,只回填Sha1
        if (CopyTaskTool.isMirrorCallbackKey(fileObject.getCallBackKey()))
        {
            // 回填MD5到Copy任务
            if (fileObject instanceof MultipartFileObject && StringUtils.isNotEmpty(fileObject.getSha1()))
            {
                copyTaskService.updateMD5ByObjectId(fileObject);
            }
            
            return;
        }
        
        // 定义可重复执行的JOB
        RetryJob job = createJob(createUpdateExecutor(fileObject));
        
        // 同步执行JOB
        if (job.execute())
        {
            return;
        }
        
        String message = "Callback for update object " + fileObject.logFormat() + " Failed.";
        LOGGER.warn(message, job.getException());
        
        if (job.getException() instanceof TBusinessException)
        {
            TBusinessException callBackException = (TBusinessException) job.getException();
            throw new CallbackFailedException(HttpStatus.valueOf(callBackException.getStatus()), message);
        }
        
        throw new CallbackFailedException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
    
    private Executor createAbortExecutor(final String objectID, final String callBackKey)
    {
        return new Executor()
        {
            @Override
            public Throwable execute()
            {
                Throwable th = null;
                for (int i = 0; i < InnerLoadBalanceManager.getTryCounts(); i++)
                {
                    th = tryUpload(objectID, callBackKey);
                    if (null == th)
                    {
                        break;
                    }
                }
                return th;
            }
            
            private Throwable tryUpload(final String objectID, final String callBackKey)
            {
                Throwable th = null;
                try
                {
                    
                    FileObjectThriftClient client = getClient(callBackKey);
                    
                    client.abortUpload(objectID, callBackKey);
                }
                catch (TTransportException e)
                {
                    th = e;
                }
                catch (Exception e)
                {
                    th = e;
                }
                return th;
            }
        };
    }
    
    private RetryJob createJob(Executor executor)
    {
        return new RetryJob(
            SystemConfigContainer.getInteger(SystemConfigKeys.OBJECT_CALLBACK_RETRY_TIMES, 3),
            SystemConfigContainer.getLong(SystemConfigKeys.OBJECT_CALLBACK_RETRY_INTERVAL, 2000L), executor);
    }
    
    private Executor createUpdateExecutor(final FileObject fileObject)
    {
        return new Executor()
        {
            @Override
            public Throwable execute()
            {
                int size = InnerLoadBalanceManager.getTryCounts();
                Throwable th = null;
                for (int i = 0; i < size; i++)
                {
                    th = tryUpdateFileObj(fileObject);
                    if (th == null)
                    {
                        break;
                    }
                }
                return th;
            }
            
            private Throwable tryUpdateFileObj(final FileObject fileObject)
            {
                Throwable th = null;
                try
                {
                    com.huawei.sharedrive.thrift.app2dc.FileObject thriftObject = new com.huawei.sharedrive.thrift.app2dc.FileObject();
                    thriftObject.setObjectID(fileObject.getObjectID());
                    thriftObject.setSha1(fileObject.getSha1());
                    thriftObject.setLength(fileObject.getObjectLength());
                    thriftObject.setStoragePath(fileObject.getStoragePath());
                    
                    FileObjectThriftClient client = getClient(fileObject.getCallBackKey());
                    
                    client.updateFileObject(thriftObject, fileObject.getCallBackKey());
                }
                catch (TTransportException e)
                {
                    th = e;
                }
                catch (Exception e)
                {
                    th = e;
                }
                return th;
            }
        };
    }
    
    private FileObjectThriftClient getClient(final String callBackKey) throws TTransportException
    {
        String uasRegionName = "";
        Map<String, Object> result = JsonUtils.stringToMap(callBackKey);
        if (null != result)
        {
            uasRegionName = (String) result.get(UAS_REGION_NAME_KEY);
        }
        if (null == uasRegionName)
        {
            uasRegionName = "";
        }
        FileObjectThriftClient client = null;
        switch (uasRegionName)
        {
            case SystemConfigKeys.SZ_UAS_REGION_NAME:
            {
                LOGGER.info("The abortUpload uas region name is " + SystemConfigKeys.SZ_UAS_REGION_NAME);
                client = szufmThriftClientProxyFactory.getProxy(FileObjectThriftClient.class);
                break;
            }
            case SystemConfigKeys.DG_UAS_REGION_NAME:
            {
                LOGGER.info("The abortUpload uas region name is " + SystemConfigKeys.DG_UAS_REGION_NAME);
                client = dgufmThriftClientProxyFactory.getProxy(FileObjectThriftClient.class);
                break;
            }
            default:
            {
                LOGGER.info("The abortUpload uas region name is default");
                client = ufmThriftClientProxyFactory.getProxy(FileObjectThriftClient.class);
                break;
            }
        }
        LOGGER.info("The abortUpload uas client Ip: " + client.getServerIp());
        
        return client;
    }
    
}
