package com.huawei.sharedrive.app.files.service.job;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.core.job.Task;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.dataserver.thrift.client.FileObjectThriftServiceClient;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.files.dao.WaitingDeleteObjectDAO;
import com.huawei.sharedrive.app.files.domain.WaitingDeleteObject;

import pw.cdmi.core.utils.SpringContextUtil;

public class AyncDeleteObjectForVersionTask extends Task
{
    private static Logger logger = LoggerFactory.getLogger(AyncDeleteObjectForVersionTask.class);
    
    private DCManager dcManager;
    
    private DssDomainService dssDomainService;
    
    private String objectId;
    
    private int resourceGroupId;
    
    private WaitingDeleteObjectDAO waitingDeleteObjectDAO;
    
    public AyncDeleteObjectForVersionTask(String objectId, int resourceGroupId)
    {
        this.objectId = objectId;
        this.resourceGroupId = resourceGroupId;
    }
    
    @Override
    public void execute()
    {
        initBean();
        
        logger.info(" begin delete object:" + objectId);
        
        // 删除对象
        ResourceGroup group = dcManager.getCacheResourceGroup(resourceGroupId);
        String domain = dssDomainService.getDomainByDssId(group);
        FileObjectThriftServiceClient client = null;
        try
        {
            client = new FileObjectThriftServiceClient(domain, group.getManagePort());
            if (!client.deleteFileObject(objectId))
            {
                // 删除失败添加到任务中
                WaitingDeleteObject waitingDeleteObject = new WaitingDeleteObject();
                waitingDeleteObject.setObjectId(objectId);
                waitingDeleteObject.setResourceGroupId(resourceGroupId);
                waitingDeleteObject.setCreatedAt(new Date());
                waitingDeleteObjectDAO.create(waitingDeleteObject);
            }
        }
        catch (Exception e)
        {
            logger.warn(e.getMessage(), e);
            // 需要添加到任务表，然后再删除
            WaitingDeleteObject waitingDeleteObject = new WaitingDeleteObject();
            waitingDeleteObject.setObjectId(objectId);
            waitingDeleteObject.setResourceGroupId(resourceGroupId);
            waitingDeleteObject.setCreatedAt(new Date());
            waitingDeleteObjectDAO.create(waitingDeleteObject);
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
        
        logger.info(" end delete object:" + objectId);
    }
    
    @Override
    public String getName()
    {
        return this.getClass().getName();
    }
    
    /**
     * 初始化Bean
     */
    private void initBean()
    {
        dcManager = (DCManager) SpringContextUtil.getBean("dcManager");
        waitingDeleteObjectDAO = (WaitingDeleteObjectDAO) SpringContextUtil.getBean("waitingDeleteObjectDAO");
        dssDomainService = (DssDomainService) SpringContextUtil.getBean("dssDomainService");
    }
    
}
