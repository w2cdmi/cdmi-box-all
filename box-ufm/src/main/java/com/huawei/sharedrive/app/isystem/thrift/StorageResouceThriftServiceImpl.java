/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.app.isystem.thrift;

import java.util.List;

import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.dataserver.domain.DataCenter;
import com.huawei.sharedrive.app.dataserver.exception.BusinessErrorCode;
import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.dataserver.service.DCService;
import com.huawei.sharedrive.app.dataserver.thrift.client.StorageResourceServiceClient;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.thrift.app2isystem.TBusinessException;
import com.huawei.sharedrive.thrift.filesystem.StorageInfo;
import com.huawei.sharedrive.thrift.filesystem.StorageResouceThriftServiceOnUfm;

import pw.cdmi.core.log.Level;
import pw.cdmi.core.utils.MethodLogAble;

/**
 * 
 * @author d00199602
 * 
 */
public class StorageResouceThriftServiceImpl implements StorageResouceThriftServiceOnUfm.Iface
{
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageResouceThriftServiceImpl.class);
    
    @Autowired
    private DCService dcService;
    
    @Autowired
    private DssDomainService dssDomainService;
    
    @Override
    @MethodLogAble(value = Level.INFO, newLogId = true, isPrintArgs=false)
    public String addStorageResource(int dcId, StorageInfo storageInfo) throws TBusinessException
    {
        StorageResourceServiceClient client = null;
        try
        {
            client = getClient(dcId);
            String result = client.addStorageResource(storageInfo);
            return result;
        }
        catch (TBusinessException e)
        {
            LOGGER.error("addStorageResource Failed.", e);
            throw e;
        }
        catch (Exception e)
        {
            LOGGER.error("addStorageResource Failed.", e);
            throw new TBusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR.getCode(), "" + e);
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
    }
    
    @Override
    @MethodLogAble(value = Level.INFO, newLogId = true, isPrintArgs=false)
    public void changeStorageResource(int dcId, StorageInfo storageInfo) throws TBusinessException
    {
        StorageResourceServiceClient client = null;
        try
        {
            client = getClient(dcId);
            client.changeStorageResource(storageInfo);
        }
        catch (Exception e)
        {
            LOGGER.error("changeStorageResource Failed.", e);
            throw new TBusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR.getCode(), "" + e);
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
    }
    
    @Override
    @MethodLogAble(value = Level.INFO, newLogId = true)
    public void deleteStorageResource(int dcId, String storageResId) throws TBusinessException
    {
        StorageResourceServiceClient client = null;
        try
        {
            client = getClient(dcId);
            client.deleteStorageResource(storageResId);
        }
        catch (Exception e)
        {
            LOGGER.error("deleteStorageResource Failed.", e);
            throw new TBusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR.getCode(), "" + e);
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
    }
    
    @Override
    @MethodLogAble(value = Level.INFO, newLogId = true)
    public void disableStorageResource(int dcId, String storageResId) throws TBusinessException
    {
        LOGGER.info("Start Disable StorageResource  [ " + dcId + " , " + storageResId + " ] ");
        StorageResourceServiceClient client = null;
        try
        {
            client = getClient(dcId);
            client.disableStorageResource(storageResId);
        }
        catch (Exception e)
        {
            LOGGER.error("disableStorageResource Failed.", e);
            throw new TBusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR.getCode(), "" + e);
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
    }
    
    @Override
    @MethodLogAble(value = Level.INFO, newLogId = true)
    public void enableStorageResource(int dcId, String storageResId) throws TBusinessException
    {
        StorageResourceServiceClient client = null;
        try
        {
            client = getClient(dcId);
            client.enableStorageResource(storageResId);
        }
        catch (Exception e)
        {
            LOGGER.error("enableStorageResource Failed.", e);
            throw new TBusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR.getCode(), "" + e);
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
    }
    
    @Override
    @MethodLogAble(value = Level.INFO, newLogId = true, isPrintResult=false)
    public List<StorageInfo> getAllStorageResource(int dcId) throws TBusinessException
    {
        StorageResourceServiceClient client = null;
        try
        {
            client = getClient(dcId);
            return client.getAllStorageResource();
        }
        catch (Exception e)
        {
            LOGGER.error("getAllStorageResource Failed.", e);
            throw new TBusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR.getCode(), "" + e);
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
    }
    
    @Override
    @MethodLogAble(value = Level.INFO, newLogId = true, isPrintResult=false)
    public StorageInfo getStorageResource(int dcId, String storageResId) throws TBusinessException
    {
        StorageResourceServiceClient client = null;
        try
        {
            client = getClient(dcId);
            return client.getStorageResource(storageResId);
        }
        catch (Exception e)
        {
            LOGGER.error("getStorageResource Failed.", e);
            throw new TBusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR.getCode(), "" + e);
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
    }
    
    private StorageResourceServiceClient getClient(int dcId) throws TTransportException
    {
        DataCenter dataCenter = dcService.getDataCenter(dcId);
        if (dataCenter == null)
        {
            throw new BusinessException("The datacenter is not exist");
        }
        com.huawei.sharedrive.app.dataserver.domain.ResourceGroup group = dataCenter.getResourceGroup();
        
        // 选择一个可用的节点，发送激活请求
        String domain = dssDomainService.getDomainByDssId(group);
        return new StorageResourceServiceClient(domain, group.getManagePort());
    }
}
