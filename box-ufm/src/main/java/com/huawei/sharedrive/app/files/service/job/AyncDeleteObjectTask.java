package com.huawei.sharedrive.app.files.service.job;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.core.job.Task;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.dataserver.thrift.client.FileObjectThriftServiceClient;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.files.dao.WaitingDeleteObjectDAO;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.domain.WaitingDeleteObject;
import com.huawei.sharedrive.app.files.service.FileService;

import pw.cdmi.core.utils.SpringContextUtil;

public class AyncDeleteObjectTask extends Task
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AyncDeleteObjectTask.class);
    
    private DCManager dcManager;
    
    private DssDomainService dssDomainService;
    
    private FileService fileService;
    
    private ObjectReference objectReference;
    
    private WaitingDeleteObjectDAO waitingDeleteObjectDAO;
    
    public AyncDeleteObjectTask(FileService fileService, ObjectReference objRef)
    {
        this.fileService = fileService;
        this.objectReference = objRef;
    }
    
    @Override
    public void execute()
    {
        // 初始化bean
        initBean();
        
        LOGGER.info(" begin delete object:" + objectReference.getId());
        
        // 删除对象应用数和SHA1
        if (1 == fileService.deleteObjectReference(objectReference))
        {
//            //清楚对象的mirror关系
            
            // 删除对象
            ResourceGroup group = dcManager.getCacheResourceGroup(objectReference.getResourceGroupId());
            String domain = dssDomainService.getDomainByDssId(group);
            FileObjectThriftServiceClient client = null;
            try
            {
                client = new FileObjectThriftServiceClient(domain, group.getManagePort());
                if (!client.deleteFileObject(objectReference.getId()))
                {
                    // 删除失败添加到任务中
                    createWaitingDeleteObject(objectReference);
                }
            }
            catch (Exception e)
            {
                LOGGER.warn(e.getMessage(), e);
                // 需要添加到任务表，然后再删除
                createWaitingDeleteObject(objectReference);
            }
            finally
            {
                if (null != client)
                {
                    client.close();
                }
            }
        }
        
        LOGGER.info(" end delete object:" + objectReference.getId());
    }
    
    @Override
    public String getName()
    {
        return this.getClass().getName();
    }
    
    public ObjectReference getObjectReference()
    {
        return objectReference;
    }
    
    public void setObjectReference(final ObjectReference objectReference)
    {
        this.objectReference = objectReference;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    private void createWaitingDeleteObject(ObjectReference objectReference)
    {
        WaitingDeleteObject waitingDeleteObject = new WaitingDeleteObject();
        waitingDeleteObject.setObjectId(objectReference.getId());
        waitingDeleteObject.setResourceGroupId(objectReference.getResourceGroupId());
        waitingDeleteObject.setCreatedAt(new Date());
        waitingDeleteObjectDAO.create(waitingDeleteObject);
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
