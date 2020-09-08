package com.huawei.sharedrive.app.mirror.manager;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.core.job.Task;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.ResourceGroupService;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.mirror.thrift.client.DCMirrorThriftServiceClient;

import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.core.utils.SpringContextUtil;

/**
 * 发送管理信息任务到DSS
 * 
 * @author c00287749
 * 
 */
public class ManagerDSSCopyTask extends Task
{
    public static final String CLASS_NAME = "ManagerDSSCopyTask";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerDSSCopyTask.class);
    
    private DssDomainService dssDomainService;
    
    private ResourceGroupService resourceGroupService;
    
    private ResourceGroup group = null;
    
    private int state;
    
    private String taskId = null;
    
    public ManagerDSSCopyTask(ResourceGroup group, int state, String taskId)
    {
        this.group = group;
        this.state = state;
        this.taskId = taskId;
    }
    
    @MethodLogAble
    @Override
    public void execute()
    {
        LOGGER.debug("SendCopyTask begin");
        initBean();
        sendTaskStateToDSS();
        LOGGER.debug("SendCopyTask end");
        
    }
    
    @Override
    public String getName()
    {
        return CLASS_NAME;
    }
    
    /**
     * 初始化Bean
     */
    private void initBean()
    {
        
        dssDomainService = (DssDomainService) SpringContextUtil.getBean("dssDomainService");
        
        resourceGroupService = (ResourceGroupService) SpringContextUtil.getBean("resourceGroupService");
        
    }
    
    /**
     * 复制任务下发到DSS中
     * 
     * @param task
     */
    private void sendTaskStateToDSS()
    {
        
        List<ResourceGroup> lstGroup = new ArrayList<ResourceGroup>(10);
 
        if (group == null)
        {
            // 发送给所以DSS
            lstGroup = resourceGroupService.listAllGroups();
        }
        else
        {
            lstGroup.add(group);
        }
        
        
        String domain = null;
        DCMirrorThriftServiceClient client = null;
        for (ResourceGroup tmpGroup : lstGroup)
        {
            domain = dssDomainService.getDomainByDssId(tmpGroup);
            try
            {
                client = new DCMirrorThriftServiceClient(domain, tmpGroup.getManagePort());
                client.managerCopyTask(state, taskId);
                LOGGER.info("sendTaskStateToDSS successed ,state:" + state);
            }
            catch (RuntimeException e)
            {
                LOGGER.warn(e.getMessage(), e);
            }
            catch (Exception e)
            {
                LOGGER.warn(e.getMessage(), e);
            }
            finally
            {
                if (client != null)
                {
                    client.close();
                }
            }
        }
    }
}
