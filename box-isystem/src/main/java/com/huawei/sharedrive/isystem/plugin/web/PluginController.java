package com.huawei.sharedrive.isystem.plugin.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.exception.BadRquestException;
import com.huawei.sharedrive.isystem.exception.InvalidParamException;
import com.huawei.sharedrive.isystem.logfile.domain.ListTreeNodeRequest;
import com.huawei.sharedrive.isystem.plugin.domain.DCTreeNode;
import com.huawei.sharedrive.isystem.plugin.domain.PluginServerInstance;
import com.huawei.sharedrive.isystem.plugin.domain.PluginServerView;
import com.huawei.sharedrive.isystem.plugin.manager.PluginManager;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.util.custom.SecurityScanUtils;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServerCluster;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServiceRouter;
import com.sun.star.util.Date;

import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping("/pluginServer/pluginServerCluster")
public class PluginController extends AbstractCommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginController.class);
    
    private static final String TASK_TYPE_SECURITY_SCAN = "SecurityScan";
    
    @Autowired
    private PluginManager pluginManager;
    
    @RequestMapping(value = "", method = {RequestMethod.GET})
    public String enter(Model model)
    {
        model.addAttribute("showCopyPlocy", showCopyPlocy);
        
        return "appManage/pluginAuthAppManageMain";
    }
    
    @RequestMapping(value = "list", method = {RequestMethod.GET})
    public String getListPluginAuthApp(Model model)
    {
        List<AuthApp> pluginApplist = pluginManager.listAuthApp();
        model.addAttribute("pluginApplist", pluginApplist);
        if (!SecurityScanUtils.enableSecurityScan())
        {
            pluginApplist.remove(1);
        }
        return "appManage/pluginAuthAppList";
    }
    
    @RequestMapping(value = "listPluginServer", method = {RequestMethod.GET})
    public String getListPluginServer(String appId, Model model)
    {
        if (TASK_TYPE_SECURITY_SCAN.equals(appId) && !SecurityScanUtils.enableSecurityScan())
        {
            LOGGER.error("user is not allowed to use SecurityScan function[doreset]" + ",TIME:" + new Date()
                + ",RESULT:failed.");
            throw new BadRquestException("Do not enalbe the funciton to SecurityScan");
        }
        List<PluginServerView> pluginServer = null;
        try
        {
            pluginServer = pluginManager.getListClusterView(appId);
        }
        catch (TException e)
        {
            LOGGER.error(e.getMessage());
        }
        
        model.addAttribute("appId", appId);
        model.addAttribute("authApp", pluginManager.getAuthApp(appId));
        model.addAttribute("pluginServer", pluginServer);
        return "appManage/pluginServerList";
    }
    
    @RequestMapping(value = "create", method = {RequestMethod.GET})
    public String createPluginServer(String appId, Model model)
    {
        if (TASK_TYPE_SECURITY_SCAN.equals(appId) && !SecurityScanUtils.enableSecurityScan())
        {
            LOGGER.error("user is not allowed to use SecurityScan function[doreset]" + ",TIME:" + new Date()
                + ",RESULT:failed.");
            throw new BadRquestException("Do not enalbe the funciton to SecurityScan");
        }
        model.addAttribute("appId", appId);
        return "appManage/createPluginServer";
    }
    
    @RequestMapping(value = "listTreeNode", method = {RequestMethod.POST})
    public ResponseEntity<String> listDCTreeNode(ListTreeNodeRequest request, Model model, String token)
    {
        super.checkToken(token);
        TPluginServerCluster pluginServerCluster = new TPluginServerCluster();
        pluginServerCluster.setAppId(request.getAppId());
        pluginServerCluster.setClusterId(request.getClusterId());
        if (null != request.getDssId())
        {
            pluginServerCluster.setDssId(request.getDssId());
        }
        List<DCTreeNode> list;
        try
        {
            if (request.getRegionId() == null || request.getRegionId() == 0)
            {
                list = pluginManager.getListRegionDao();
            }
            else
            {
                list = pluginManager.getListDCRouter(request.getRegionId(), pluginServerCluster);
            }
            String returnS = JsonUtils.toJson(list);
            return new ResponseEntity<String>(returnS, HttpStatus.OK);
        }
        catch (TException e)
        {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "addPreview", method = {RequestMethod.POST})
    public ResponseEntity<String> addPreview(TPluginServerCluster pluginServerCluster, String addRouter,
        String delRouter, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        checkAddPreviewParaValid(pluginServerCluster);
        
        UserLogType type;
        if (pluginServerCluster.getClusterId() != 0)
        {
            type = UserLogType.PLUGIN_SERVICE_MODEFY;
        }
        else
        {
            type = UserLogType.PLUGIN_SERVICE_ADD;
        }
        
        UserLog userLog = userLogService.initUserLog(request, type, logParam(pluginServerCluster));
        userLogService.saveUserLog(userLog);
        try
        {
            List<TPluginServiceRouter> addList = new ArrayList<TPluginServiceRouter>(0);
            List<TPluginServiceRouter> delList = new ArrayList<TPluginServiceRouter>(0);
            if (StringUtils.isNotEmpty(addRouter))
            {
                addList = (List<TPluginServiceRouter>) JsonUtils.stringToList(addRouter,
                    List.class,
                    TPluginServiceRouter.class);
            }
            if (StringUtils.isNotEmpty(delRouter))
            {
                delList = (List<TPluginServiceRouter>) JsonUtils.stringToList(delRouter,
                    List.class,
                    TPluginServiceRouter.class);
            }
            // 把周期分钟转成秒
            pluginServerCluster.setMonitorCycle(pluginServerCluster.getMonitorCycle() * 60);
            pluginManager.addPreview(pluginServerCluster, addList, delList);
            
            userLog.setDetail(type.getDetails(logParam(pluginServerCluster)));
            userLog.setLevel(UserLogService.SUCCESS_LEVEL);
            userLogService.update(userLog);
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (TException e)
        {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        
    }
    
    private void checkAddPreviewParaValid(TPluginServerCluster pluginServerCluster)
    {
        if (pluginServerCluster.getName() == null || pluginServerCluster.getName().length() < 1
            || pluginServerCluster.getName().length() > 255)
        {
            throw new InvalidParamException("invalid plgin server cluster name rule");
        }
        if (pluginServerCluster.getDescription() != null
            && pluginServerCluster.getDescription().length() > 512)
        {
            throw new InvalidParamException("invalid plgin server cluster name rule");
        }
        if (pluginServerCluster.getMonitorCycle() > 59 || pluginServerCluster.getMonitorCycle() < 1)
        {
            throw new InvalidParamException("invalid monitorCycle rule");
        }
    }
    
    @RequestMapping(value = "deletePreview", method = {RequestMethod.POST})
    public ResponseEntity<String> deletePreview(Long clusterId, String appId, String seviceName,
        HttpServletRequest request)
    {
        UserLog userLog = userLogService.initUserLog(request, UserLogType.PLUGIN_SERVICE_DEL, new String[]{
            appId, seviceName});
        userLogService.saveUserLog(userLog);
        if (TASK_TYPE_SECURITY_SCAN.equals(appId) && !SecurityScanUtils.enableSecurityScan())
        {
            LOGGER.error("user is not allowed to use SecurityScan function[doreset]" + ",TIME:" + new Date()
                + ",RESULT:failed.");
            throw new BadRquestException("Do not enalbe the funciton to SecurityScan");
        }
        try
        {
            if (null != clusterId && clusterId != 0)
            {
                pluginManager.deletePluginServcie(clusterId);
                
                userLog.setDetail(UserLogType.PLUGIN_SERVICE_DEL.getDetails(new String[]{appId, seviceName}));
                userLog.setLevel(UserLogService.SUCCESS_LEVEL);
                userLogService.update(userLog);
                return new ResponseEntity<String>(HttpStatus.OK);
            }
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        catch (Exception e)
        {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        
    }
    
    @RequestMapping(value = "listInstances", method = {RequestMethod.GET})
    public String listInstances(Long clusterId, String appId, String name, Model model)
    {
        List<PluginServerInstance> instances;
        if (TASK_TYPE_SECURITY_SCAN.equals(appId) && !SecurityScanUtils.enableSecurityScan())
        {
            LOGGER.error("user is not allowed to use SecurityScan function[doreset]" + ",TIME:" + new Date()
                + ",RESULT:failed.");
            throw new BadRquestException("Do not enalbe the funciton to SecurityScan");
        }
        try
        {
            name = URLDecoder.decode(name, "UTF-8");
            if (null != clusterId && clusterId != 0)
            {
                instances = pluginManager.getListPluginServerInstance(clusterId);
                model.addAttribute("instances", instances);
                model.addAttribute("appId", appId);
                model.addAttribute("clusterId", clusterId);
                model.addAttribute("name", name);
            }
        }
        catch (UnsupportedEncodingException e)
        {
            LOGGER.error(e.getMessage());
        }
        catch (TException e)
        {
            LOGGER.error(e.getMessage());
        }
        return "appManage/pluginInstanceList";
    }
    
    @RequestMapping(value = "modifyPluginServcive", method = {RequestMethod.GET})
    public String modifyPluginServcive(TPluginServerCluster modifyPSCluster, Model model)
    {
        try
        {
            PluginServerView pluginServerView = pluginManager.getPluginServerView(modifyPSCluster);
            model.addAttribute("modifyPSCluster", pluginServerView);
        }
        catch (TException e)
        {
            LOGGER.error(e.getMessage());
        }
        return "appManage/modiftyPluginServer";
    }
    
    private String[] logParam(TPluginServerCluster cluster)
    {
        if (null == cluster)
        {
            return new String[]{"", ""};
        }
        return new String[]{cluster.getAppId(), cluster.getName()};
    }
}
