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
package pw.cdmi.file.engine.manage.datacenter.job;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.common.alarm.AlarmHelper;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.core.utils.NetCheckUtils;
import pw.cdmi.file.engine.core.alarm.CommunicateFailedAlarm;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.core.spring.ext.BeanHolder;
import pw.cdmi.file.engine.filesystem.manage.cache.FSEndpointCache;
import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.manage.InnerLoadBalanceManager;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;
import pw.cdmi.file.engine.manage.datacenter.domain.ClusterNode;
import pw.cdmi.file.engine.manage.datacenter.domain.NodeStatus;
import pw.cdmi.file.engine.manage.datacenter.domain.ResourceGroupType;
import pw.cdmi.file.engine.manage.datacenter.domain.RuntimeStatus;
import pw.cdmi.file.engine.manage.datacenter.service.DCService;

/**
 * 检查节点自身状态，并更新到数据库中
 * 
 * @author s90006125
 * 
 */
@Service("checkSelfStatusJob")
public class CheckSelfStatusJob extends QuartzJobTask
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckSelfStatusJob.class);
    
    @Autowired
    private DCService dcService;
    
    @Autowired
    private CommunicateFailedAlarm communicateFailedAlarm;
    
    @Autowired
    private AlarmHelper alarmHelper;
    
    @Autowired
    private InnerLoadBalanceManager innerLoadBalanceManager;
    
    private static final int NET_CHECK_RETRYTIMES = 5;
    
    private static final String ADDRESS_SPLIT = ",";
    
    private static String[] netCheckAddress;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        String hostName = EnvironmentUtils.getHostName();
        ClusterNode node = dcService.getClusterNode(hostName);
        
        if (null == node)
        {
            node = new ClusterNode(hostName);
            // 从config.properties配置文件中获取以下信息
            node.setManageAddr(BeanHolder.getMessage("system.dc.environment.manageaddr"));
            node.setManagePort(SystemConfigContainer.getInteger(SystemConfigKeys.SYSTEM_ENVIRONMENT_MANAGER_PORT,
                Integer.valueOf(13010)).intValue());
            node.setInnerAddr(BeanHolder.getMessage("system.dc.environment.inneraddr"));
            node.setInnerGateway(BeanHolder.getMessage("system.dc.environment.innergw"));
            node.setServiceAddr(BeanHolder.getMessage("system.dc.environment.serviceaddr"));
            node.setServiceGateway(BeanHolder.getMessage("system.dc.environment.servicegw"));
            node.setStatus(NodeStatus.Disable);
            node.setRuntimeStatus(checkRuntimeStatus());
            node.setLastReportTime(System.currentTimeMillis());
            dcService.saveClusterNode(node);
        }
        
        try
        {
            if (checkNetworkStatus(NET_CHECK_RETRYTIMES))
            {
                node.setRuntimeStatus(checkRuntimeStatus());
                node.setLastReportTime(System.currentTimeMillis());
                dcService.updateNodeStatus(node);
                LOGGER.info("Status = " + node.getRuntimeStatus());
            }
            else
            {
                // 如果网络检查不通过，则表示节点离线，不更新数据库状态
                record.setSuccess(Boolean.FALSE);
                record.setOutput("Network anomaly");
                LOGGER.warn("Network Failed.");
            }
        }
        catch (Exception e)
        {
            record.setSuccess(Boolean.FALSE);
            record.setOutput(e.getMessage());
            LOGGER.warn("Check Network Status Failed.", e);
        }
    }
    
    /**
     * 检查当前状态
     * 
     * @return
     */
    private RuntimeStatus checkRuntimeStatus()
    {
        boolean hasException = false;
        
        List<FSEndpoint> endpoints = FSEndpointCache.getAllEndpoints();
        
        if (!endpoints.isEmpty())
        {
            for (FSEndpoint endpoint : endpoints)
            {
                if (!endpoint.isAvailable() && !endpoint.isWriteAble())
                {
                    hasException = true;
                    break;
                }
            }
        }
        
        if (hasException)
        {
            return RuntimeStatus.StorageAbnormal;
        }
        
        return RuntimeStatus.Normal;
    }
    
    /**
     * 检测网络状况<br>
     * 检测网络状态，通过检测业务网和公网来检查，内网不用检查，因为后面会更新数据库，如果内网有问题，则数据库就不能更新<br>
     * 需要注意的是，下面访问你们的getManagerGateway实际上获取到的是业务网网关，因为ac和dc之间的thrift通信，是通过该网段来的，所以叫做管理网段
     * 
     * @return
     * @throws IOException
     */
    private boolean checkNetworkStatus(int retryTimes) throws IOException
    {
        if(!NetCheckUtils.ipV4AddressCheck(retryTimes, getNetCheckAddress()))
        {
            LOGGER.warn("network check failed.");
            return true;
        }
        
        if (!checkReportChannel())
        {
            LOGGER.warn("Cann't Reach Report Channel.");
            return false;
        }
        
        return true;
    }
    
    private String getServerIp()
    {
        String dcType = SystemConfigContainer.getString(SystemConfigKeys.SYSTEM_DATACENTER_TYPE,
            ResourceGroupType.Merge.getType());
        String serverIp = null;
        if (StringUtils.equals(dcType, ResourceGroupType.Merge.getType()))
        {
            serverIp = innerLoadBalanceManager.getUFMIp();
            if (StringUtils.isNotBlank(serverIp))
            {
                return serverIp;
            }
        }
        serverIp = SystemConfigContainer.getString(SystemConfigKeys.THRIFT_APPSERVER_SERVER_IP, "127.0.0.1");
        LOGGER.info("lvs loadBalance,get ip:" + serverIp);
        return serverIp;
    }
    
    /**
     * 检查上报通道是否正常
     * 
     * @return
     */
    private boolean checkReportChannel()
    {
        if (!SystemConfigContainer.getBoolean(SystemConfigKeys.SYSTEM_DATACENTER_BEEN_INITIALIZED, Boolean.FALSE).booleanValue())
        {
            // 如果未初始化，就不检查网络是否通的
            return true;
        }
        
        String reportIp = getServerIp();
        
        int reportPort = SystemConfigContainer.getInteger(SystemConfigKeys.THRIFT_APPSERVER_SERVER_PORT,
            Integer.valueOf(13003)).intValue();
        
        Socket client = null;
        
        try
        {
            client = new Socket();
            
            client.connect(new InetSocketAddress(reportIp, reportPort), 3000);
            
            alarmHelper.sendRecoverAlarm(communicateFailedAlarm);
            return true;
        }
        catch (IOException e)
        {
            LOGGER.warn("Check Socket Failed [ " + reportIp + ", " + reportPort + " ] ", e);
            alarmHelper.sendAlarm(communicateFailedAlarm);
            
            tryUpdateNodeForThriftFailed(reportIp);
            
            return false;
        }
        finally
        {
            if (null != client)
            {
                try
                {
                    client.close();
                }
                catch (IOException e)
                {
                    LOGGER.warn("Close Check Socket Failed.", e);
                }
            }
        }
    }
    
    private void tryUpdateNodeForThriftFailed(String reportIp)
    {
        try
        {
            innerLoadBalanceManager.updateNode(reportIp);
        }
        catch (Exception e)
        {
            LOGGER.warn("update  NodeForThriftFailed : {}", reportIp, e);
        }
    }
    
    private String[] getNetCheckAddress()
    {
        if(null != netCheckAddress)
        {
            return netCheckAddress;
        }
        
        String temp = StringUtils.trimToEmpty(BeanHolder.getMessage("system.network.check.address"));
        
        netCheckAddress = temp.split(ADDRESS_SPLIT);
        
        return netCheckAddress;
    }
}
