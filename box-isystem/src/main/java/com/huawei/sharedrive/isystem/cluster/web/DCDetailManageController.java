/**
 * 
 */
package com.huawei.sharedrive.isystem.cluster.web;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.isystem.adminlog.domain.OperateDescriptionType;
import com.huawei.sharedrive.isystem.cluster.FileSystemConstant;
import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroupNode;
import com.huawei.sharedrive.isystem.cluster.domain.filesystem.NasStorage;
import com.huawei.sharedrive.isystem.cluster.domain.filesystem.StorageStatus;
import com.huawei.sharedrive.isystem.cluster.domain.filesystem.UdsStorage;
import com.huawei.sharedrive.isystem.cluster.service.DCService;
import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.dns.common.DnsThriftCommon;
import com.huawei.sharedrive.isystem.dns.manager.InnerLoadBalanceManager;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.exception.InvalidParamException;
import com.huawei.sharedrive.isystem.logfile.domain.FSEndpoint;
import com.huawei.sharedrive.isystem.logfile.service.LogAgentService;
import com.huawei.sharedrive.isystem.logfile.web.LogAgentUtils;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.thrift.client.DCManageServiceClient;
import com.huawei.sharedrive.isystem.thrift.client.StorageResourceServiceClient;
import com.huawei.sharedrive.thrift.dns.Node;
import com.huawei.sharedrive.thrift.dns.TDomainNotExistException;
import com.huawei.sharedrive.thrift.filesystem.StorageInfo;

import pw.cdmi.common.log.UserLog;
import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;

/**
 * 
 * 
 * DC管理
 * 
 * @author d00199602
 * 
 */
@SuppressWarnings({"rawtypes"})
@Controller
@RequestMapping(value = "/cluster/dcdetailmanage")
public class DCDetailManageController extends AbstractCommonController
{
    private static Logger logger = LoggerFactory.getLogger(DCDetailManageController.class);
    
    @Autowired
    private DCService dcService;
    
    @Autowired
    private DnsThriftCommon dnsThriftCommon;
    
    @Autowired
    private InnerLoadBalanceManager innerLoadBalanceManager;
    
    @Autowired
    private LogAgentService logAgentService;
    
    @Autowired
    private LogAgentUtils logAgentUtils;
    
    @Autowired
    private ThriftClientProxyFactory ufmThriftClientProxyFactory;
    
    /**
     * 删除存储资源
     * 
     * @return
     * @throws TException
     */
    @RequestMapping(value = "deleteStorageRes", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> deleteStorageRes(String token, int dcId, String storageResId,
        HttpServletRequest request) throws TException
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.STORAGE_DELETE,
            new String[]{getStorageResDesc(dcId, storageResId)});
        userLogService.saveUserLog(userLog);
        
        String message = UserLogType.STORAGE_DELETE.getDetails(new String[]{getStorageResDesc(dcId,
            storageResId)});
        ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class).deleteStorageResource(dcId,
            storageResId);
        userLog.setDetail(message);
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * 停用存储资源
     * 
     * @return
     * @throws TException
     */
    @RequestMapping(value = "disableStorageRes", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> disableStorageRes(String token, int dcId, String storageResId,
        HttpServletRequest request) throws TException
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.STORAGE_DISABLE,
            new String[]{getStorageResDesc(dcId, storageResId)});
        userLogService.saveUserLog(userLog);
        ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class).disableStorageResource(dcId,
            storageResId);
        userLog.setDetail(UserLogType.STORAGE_DISABLE.getDetails(new String[]{getStorageResDesc(dcId,
            storageResId)}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * 启用存储资源
     * 
     * @return
     * @throws TException
     */
    @RequestMapping(value = "enableStorageRes", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> enableStorageRes(String token, int dcId, String storageResId,
        HttpServletRequest request) throws TException
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.STORAGE_ENABLE,
            new String[]{getStorageResDesc(dcId, storageResId)});
        userLogService.saveUserLog(userLog);
        ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class).enableStorageResource(dcId,
            storageResId);
        
        userLog.setDetail(UserLogType.STORAGE_ENABLE.getDetails(new String[]{getStorageResDesc(dcId,
            storageResId)}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value = "{id}", method = {RequestMethod.GET})
    public String enter(@PathVariable("id") int id, Model model)
    {
        // DC资源组信息
        DataCenter dataCenter = dcService.getDataCenter(id);
        try
        {
            
            model.addAttribute("dcId", id);
            
            model.addAttribute("STORAGE_RES_STATUS_ENABLE", StorageStatus.ENABLE.getCode());
            model.addAttribute("STORAGE_RES_STATUS_DISABLE", StorageStatus.DISABLED.getCode());
            model.addAttribute("STORAGE_RES_STATUS_NOTUSE", StorageStatus.NOT_ENABLED.getCode());
            fillStatus(model);
            
            listStorageResource(model, dataCenter.getResourceGroup().getId());
            
            boolean isMergeDC = logAgentService.isMergeDC(id);
            
            model.addAttribute("isMergeDC", isMergeDC);
            adjustStatus(dataCenter);
            // 判断是否是合并部署的DC
            if (!isMergeDC)
            {
                // 加载日志归档参数
                loadLogAgentConfig(model, id);
            }
            
        }
        catch (Exception e)
        {
            logger.error("list resource group fail", e);
            
        }
        model.addAttribute("dataCenter", dataCenter);
        return "clusterManage/dcDetail";
    }
    
    @RequestMapping(value = "enterChangeStorage/{dcId}/{storageResId}", method = {RequestMethod.GET})
    public String enterChangeStorage(@PathVariable("dcId") int dcId,
        @PathVariable("storageResId") String storageResId, Model model)
    {
        try
        {
            // 获取当前存储资源
            StorageInfo storageResource = ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
                .getStorageResource(dcId, storageResId);
            if (null == storageResource)
            {
                throw new BusinessException();
            }
            
            if (FileSystemConstant.FILE_SYSTEM_UDS.equals(storageResource.getFsType()))
            {
                model.addAttribute("storageResource", new UdsStorage(storageResource));
                return "clusterManage/changeUDSStorage";
            }
            
            model.addAttribute("storageResource", new NasStorage(storageResource));
            return "clusterManage/changeNASStorage";
            
        }
        catch (TException e)
        {
            throw new BusinessException(e);
        }
    }
    
    @RequestMapping(value = "enterSetDomainName/{id}", method = {RequestMethod.GET})
    public String enterSetDomainName(@PathVariable("id") int id, Model model)
    {
        DataCenter dataCenter = dcService.getDataCenter(id);
        model.addAttribute("dataCenter", dataCenter);
        model.addAttribute("domainName", dataCenter.getResourceGroup().getDomainName());
        return "clusterManage/setDCDomainName";
    }
    
    @RequestMapping(value = "/logagentconfig/{clusterId}/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveLogStorageInfo(@PathVariable(value = "clusterId") int clusterId,
        @RequestParam(value = "fsType", required = true) String fsType,
        @RequestParam(value = "endpoint", required = true) String endpoint, HttpServletRequest request,
        String token)
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request, UserLogType.DC_CONFIG_LOG, new String[]{fsType,
            endpoint});
        userLogService.saveUserLog(userLog);
        
        try
        {
            endpoint = logAgentUtils.validateAndGetRealEndpoint(validator, fsType, endpoint);
        }
        catch (InvalidParameterException e)
        {
            userLog.setDetail(UserLogType.DC_CONFIG_LOG.getErrorDetails(new String[]{fsType, endpoint}));
            userLog.setType(UserLogType.DC_CONFIG_LOG.getValue());
            userLogService.update(userLog);
            throw e;
        }
        
        logAgentService.setFSEndpointForCluster(clusterId, fsType, endpoint);
        
        userLog.setDetail(UserLogType.DC_CONFIG_LOG.getDetails(new String[]{clusterId + ""}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * 修改DC域名
     * 
     * @param dc
     * @return
     * @throws TException
     */
    @RequestMapping(value = "setDomainName", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> setDomainName(String token, int id, String domainName, HttpServletRequest request)
        throws TException
    {
        super.checkToken(token);
        if (StringUtils.isNotBlank(domainName) && domainName.length() > 128)
        {
            throw new InvalidParamException("the domainname is larger than 128");
        }
        UserLog userLog = userLogService.initUserLog(request,
            UserLogType.DATACENTER_CONFIG_NAME,
            new String[]{id + "", domainName});
        userLogService.saveUserLog(userLog);
        DataCenter dataCenter = dcService.getDataCenter(id);
        ufmThriftClientProxyFactory.getProxy(DCManageServiceClient.class).modifyResourceGroup(id, domainName);
        userLog.setDetail(UserLogType.DATACENTER_CONFIG_NAME.getDetails(new String[]{dataCenter.getName(),
            domainName}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/updateNateView", method = RequestMethod.GET)
    public String updateNateView(Model model, ResourceGroupNode resourceGroupNode)
    {
        model.addAttribute("resourceGroupNode", resourceGroupNode);
        return "clusterManage/resourceGroupNodeNateView";
    }
    
    private void adjustStatus(DataCenter dataCenter)
    {
        if (null == dataCenter)
        {
            return;
        }
        
        // 若是开启了内部负载均衡，直接返回，前面已经从数据库获取到状态，不走之前DNS更新状态的流程，没开启走DNS更新状态
        if (innerLoadBalanceManager.isSysInnerLoadblanceConfig())
        {
            return;
        }
        // 初始化状态
        setResourceGroupNodeToOffline(dataCenter);
        List<Node> nodes = null;
        try
        {
            nodes = dnsThriftCommon.getNodesForDataCenter(dataCenter);
        }
        catch (TDomainNotExistException e)
        {
            logger.error("Domain Not Exist", e);
            return;
        }
        catch (TException e)
        {
            logger.error("Thrift Exception", e);
            return;
        }
        Map<String, Node> map = new HashMap<String, Node>(10);
        for (Node node : nodes)
        {
            map.put(node.getServiceAddress(), node);
        }
        if (null == dataCenter.getResourceGroup().getNodes())
        {
            return;
        }
        
        ResourceGroup resourceGroup = dataCenter.getResourceGroup();
        resourceGroup.adjustStatus(map);
    }
    
    private String getStorageResDesc(int dcId, String storageResId) throws TException
    {
        DataCenter dataCenter = dcService.getDataCenter(dcId);
        if (dataCenter == null)
        {
            return "";
        }
        StorageInfo storageInfo = ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
            .getStorageResource(dcId, storageResId);
        if (storageInfo == null)
        {
            return "";
        }
        return OperateDescriptionType.STORAGE_PARAM.getDetails(new String[]{dataCenter.getName(),
            storageInfo.getEndpoint()});
    }
    
    private List<StorageInfo> listStorageResource(int resourceGroupId)
    {
        List<StorageInfo> storageResList = null;
        try
        {
            storageResList = ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
                .getAllStorageResource(resourceGroupId);
            
        }
        catch (Exception e)
        {
            logger.error("list storage resource fail", e);
        }
        return storageResList;
    }
    
    private void listStorageResource(Model model, int resourceGroupId)
    {
        // 重试3次
        List<UdsStorage> udsInfos = null;
        List<NasStorage> nasInfos = null;
        List<StorageInfo> storageResList = null;
        
        UdsStorage udsStorage = null;
        NasStorage nasStorage = null;
        
        for (int i = 0; i < 3; i++)
        {
            storageResList = listStorageResource(resourceGroupId);
            
            if (null == storageResList)
            {
                continue;
            }
            
            udsInfos = new ArrayList<UdsStorage>(1);
            nasInfos = new ArrayList<NasStorage>(1);
            
            for (StorageInfo storageInfo : storageResList)
            {
                if (FileSystemConstant.FILE_SYSTEM_NAS.equals(storageInfo.getFsType()))
                {
                    nasStorage = new NasStorage(storageInfo);
                    nasStorage.setDcId(resourceGroupId);
                    nasInfos.add(nasStorage);
                }else{
                    udsStorage = new UdsStorage(storageInfo);
                    udsStorage.setDcId(resourceGroupId);
                    udsInfos.add(udsStorage);
                }
            }
            // TEST
            model.addAttribute("udsInfos", udsInfos);
            model.addAttribute("nasInfos", nasInfos);
            return;
        }
        
        model.addAttribute("storageFail", "yes");
    }
    
    private void loadLogAgentConfig(Model model, int clusterId)
    {
        FSEndpoint endpoint = logAgentService.getFSEndpointByClusterId(clusterId);
        if (endpoint != null)
        {
            logAgentUtils.setModelInfo(model, endpoint.getFsType(), endpoint.getEndpoint());
        }
    }
    
    private void setResourceGroupNodeToOffline(DataCenter dataCenter)
    {
        dataCenter.setResourceGroupNodeRuntimeStatus(com.huawei.sharedrive.isystem.cluster.domain.ResourceGroupNode.RuntimeStatus.Offline);
    }
    
}
