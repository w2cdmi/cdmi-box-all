/**
 * 
 */
package com.huawei.sharedrive.isystem.cluster.web;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;
import com.huawei.sharedrive.isystem.cluster.service.DCService;
import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.thrift.client.DCManageServiceClient;
import com.huawei.sharedrive.isystem.util.FormValidateUtil;
import com.huawei.sharedrive.thrift.app2isystem.ResourceGroupCreateInfo;

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
@Controller
@RequestMapping(value = "/cluster/dcmanage")
public class DCManageController extends AbstractCommonController
{
    @Autowired
    private DCService dcService;
    
    @Autowired
    private ThriftClientProxyFactory ufmThriftClientProxyFactory;
    
    private static final String DEFAULT_PROTOCOL = "https";
    
    @Value("${resourceGroup.managePort}")
    private int managePort;
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter(Model model)
    {
        return "clusterManage/clusterMain";
    }
    
    /**
     * 进入DC创建页面
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "create/{regionId}", method = RequestMethod.GET)
    public String enterCreate(@PathVariable("regionId") int regionId, Model model)
    {
        model.addAttribute("regionId", regionId);
        return "clusterManage/createDC";
    }
    
    /**
     * 创建DC
     * 
     * @param dc
     * @return
     * @throws TException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> create(DataCenter dc, HttpServletRequest request, String token)
        throws TException
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request, UserLogType.DC_ADD, new String[]{dc.getName()});
        userLogService.saveUserLog(userLog);
        Set violations = validator.validate(dc);
        if (!violations.isEmpty())
        {
            throw new ConstraintViolationException(violations);
        }
        violations = validator.validate(dc.getResourceGroup());
        if (!violations.isEmpty())
        {
            throw new ConstraintViolationException(violations);
        }
        
        if (!FormValidateUtil.isValidIPv4(dc.getResourceGroup().getManageIp()))
        {
            userLog.setDetail(UserLogType.DC_ADD.getErrorDetails(new String[]{dc.getName()}));
            userLog.setType(UserLogType.DC_ADD.getValue());
            userLogService.update(userLog);
            throw new ConstraintViolationException("ip is invaid", null);
        }
        
        ResourceGroupCreateInfo createInfo = new ResourceGroupCreateInfo();
        createInfo.setName(dc.getName());
        createInfo.setManagerIp(dc.getResourceGroup().getManageIp());
        createInfo.setManagerPort(managePort);
        createInfo.setRegionId(dc.getRegion().getId());
        createInfo.setDomainName(dc.getResourceGroup().getDomainName());
        createInfo.setGetProtocol(DEFAULT_PROTOCOL);
        createInfo.setPutProtocol(DEFAULT_PROTOCOL);
        
        ufmThriftClientProxyFactory.getProxy(DCManageServiceClient.class).addResourceGroup(createInfo);
        userLog.setDetail(UserLogType.DC_ADD.getDetails(new String[]{dc.getName()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        
        return new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * 根据ID启用指定的DC资源组
     * 
     * @param id 要启用的DC资源组ID
     * @return 启用结果
     * @throws TException
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "activate", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> activate(String token, int id, HttpServletRequest request) throws TException
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request, UserLogType.DC_ENABLE, new String[]{id + ""});
        userLogService.saveUserLog(userLog);
        
        DataCenter dataCenter = dcService.getDataCenter(id);
        ufmThriftClientProxyFactory.getProxy(DCManageServiceClient.class).activeResourceGroup(id);
        
        userLog.setDetail(UserLogType.DC_ENABLE.getDetails(new String[]{dataCenter.getName()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * 根据ID删除指定的DC资源组
     * 
     * @param id 要删除的DC资源组ID
     * @return 删除结果
     * @throws TException
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> delete(String token, int id, HttpServletRequest request) throws TException
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request, UserLogType.DC_DELETE, new String[]{id + ""});
        userLogService.saveUserLog(userLog);
        DataCenter dataCenter = dcService.getDataCenter(id);
        ufmThriftClientProxyFactory.getProxy(DCManageServiceClient.class).deleteResourceGroup(id);
        userLog.setDetail(UserLogType.DC_DELETE.getDetails(new String[]{dataCenter.getName()}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "setpriority", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> setpriority(String token, int dcid, int regionid, HttpServletRequest request)
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request,UserLogType.PRIORITY_SET, new String[]{regionid + ","+dcid+""});
        userLogService.saveUserLog(userLog);
        dcService.setPriority(regionid,dcid);
        userLog.setDetail(UserLogType.PRIORITY_SET.getDetails(new String[]{regionid + ","+dcid+""}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "setprioritydefault", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> setprioritydefault(String token, int dcid, int regionid, HttpServletRequest request)
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request,UserLogType.PRIORITY_SET, new String[]{regionid + ","+dcid+""});
        userLogService.saveUserLog(userLog);
        dcService.setPriorityDefault(regionid,dcid);
        userLog.setDetail(UserLogType.PRIORITY_SET.getDetails(new String[]{regionid + ","+dcid+""}));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity(HttpStatus.OK);
    }
    
}
