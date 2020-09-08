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
package pw.cdmi.box.uam.authapp.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.authapp.domain.AuthAppNetRegionIp;
import pw.cdmi.box.uam.authapp.manager.AuthAppNetRegionIpManager;
import pw.cdmi.box.uam.authapp.service.AuthAppNetRegionIpService;
import pw.cdmi.box.uam.authapp.service.AuthAppService;
import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.exception.BadRquestException;
import pw.cdmi.box.uam.exception.NoSuchAuthAppException;
import pw.cdmi.box.uam.httpclient.domain.RestRegionInfo;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.user.service.UserService;
import pw.cdmi.uam.domain.AuthApp;

/**
 * 
 * @author
 */
@Controller
@RequestMapping(value = "/app/network/config/")
public class AuthAppNetRegionController extends AbstractCommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthAppNetRegionController.class);
    
    @Autowired
    private AuthAppNetRegionIpManager authAppNetRegionIpManager;
    
    @Autowired
    private AuthAppNetRegionIpService authAppNetRegionIpService;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    private static final String OPEN_STATUS = "open";
    
    private static final String CLOSE_STATUS = "close";
    
    @RequestMapping(value = "{appId}", method = {RequestMethod.GET})
    public String enterList(@PathVariable(value = "appId") String appId, Model model)
    {
        AuthApp authApp = authAppService.getByAuthAppID(appId);
        
        if (null == authApp)
        {
            LOGGER.warn("authApp {} not exists.", appId);
            throw new NoSuchAuthAppException("authApp " + appId + " not exists.");
        }
        
        model.addAttribute("networkRegionStatus", authApp.getNetworkRegionStatus());
        model.addAttribute("appId", HtmlUtils.htmlEscape(appId));
        return "app/network/appNetRegionList";
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @RequestMapping(value = "list/{appId}", method = {RequestMethod.POST})
    public ResponseEntity list(@PathVariable(value = "appId") String appId,
        @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
        @RequestParam(value = "pageSize", defaultValue = "40") Integer pageSize, String token)
    {
        super.checkToken(token);
        PageRequest request = new PageRequest(pageNumber, pageSize);
        
        Page<AuthAppNetRegionIp> ips = authAppNetRegionIpService.listNetworkRegion(appId, null, request);
        if (null != ips.getContent())
        {
            List<AuthAppNetRegionIp> configList = ips.getContent();
            for (AuthAppNetRegionIp config : configList)
            {
                config.setRegionName(HtmlUtils.htmlEscape(config.getRegionName()));
                config.setAuthAppId(HtmlUtils.htmlEscape(config.getAuthAppId()));
                config.setIpEnd(HtmlUtils.htmlEscape(config.getIpEnd()));
                config.setIpStart(HtmlUtils.htmlEscape(config.getIpStart()));
                config.setRegionName(HtmlUtils.htmlEscape(config.getRegionName()));
            }
        }
        return new ResponseEntity(ips, HttpStatus.OK);
    }
    
    @RequestMapping(value = "enterConfig/{appId}", method = RequestMethod.GET)
    public String enterConfigNetworkRegion(@PathVariable(value = "appId") String appId,
        @RequestParam(value = "tag", required = false) Long networkRegionConfigId,
        @RequestParam(value = "currentPage", defaultValue = "1", required = false) int currentPage,
        Model model)
    {
        List<RestRegionInfo> regions = userService.getRegionInfo(appId);
        if (null != regions)
        {
            for (RestRegionInfo region : regions)
            {
                region.setName(HtmlUtils.htmlEscape(region.getName()));
            }
        }
        model.addAttribute("regionList", regions);
        model.addAttribute("currentPage", currentPage);
        
        if (null != networkRegionConfigId)
        {
            AuthAppNetRegionIp regionConfig = authAppNetRegionIpService.getAuthAppNetRegionIpConfig(networkRegionConfigId);
            model.addAttribute("regionConfig", regionConfig);
            if (regionConfig != null)
            {
                model.addAttribute("appId", regionConfig.getAuthAppId());
            }
        }
        else
        {
            model.addAttribute("appId", HtmlUtils.htmlEscape(appId));
        }
        
        return "app/network/configNetRegionIp";
    }
    
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "saveConfig", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> saveConfig(HttpServletRequest request, AuthAppNetRegionIp authAppNetRegionIp,
        String token)
    {
        super.checkToken(token);
        String[] description = new String[]{getRegionName(authAppNetRegionIp),
            authAppNetRegionIp.getAuthAppId(), authAppNetRegionIp.getIpStart(), authAppNetRegionIp.getIpEnd()};
        try
        {
            if (null == authAppNetRegionIp.getId())
            {
                LOGGER.info("create new app network region config {}.", authAppNetRegionIp);
                authAppNetRegionIpManager.createAuthAppNetRegionIp(authAppNetRegionIp);
                systemLogManager.saveSuccessLog(request,
                    OperateType.NetworkRegionIp,
                    OperateDescription.NETWORK_REGION_IP_ADD,
                    null,
                    description);
            }
            else
            {
                LOGGER.info("update app network region config {}.", authAppNetRegionIp);
                authAppNetRegionIpManager.updateAuthAppNetRegionIp(authAppNetRegionIp);
                systemLogManager.saveSuccessLog(request,
                    OperateType.NetworkRegionIp,
                    OperateDescription.NETWORK_REGION_IP_UPDATE,
                    null,
                    description);
            }
            return new ResponseEntity(HttpStatus.OK);
        }
        catch (Exception e)
        {
            if (null == authAppNetRegionIp.getId())
            {
                systemLogManager.saveFailLog(request,
                    OperateType.NetworkRegionIp,
                    OperateDescription.NETWORK_REGION_IP_ADD,
                    null,
                    description);
            }
            else
            {
                systemLogManager.saveFailLog(request,
                    OperateType.NetworkRegionIp,
                    OperateDescription.NETWORK_REGION_IP_UPDATE,
                    null,
                    description);
            }
            LOGGER.error("save app network region config failed.", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "deleteConfig", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> deleteConfig(long[] ids, String token, HttpServletRequest req)
    {
        super.checkToken(token);
        if (null == ids || ids.length == 0)
        {
            LOGGER.warn("id is null");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        
        try
        {
            // 记录日志方法在AuthAppNetRegionIpManager类的deleteNetworkRegionConfigs方法中被调用
            authAppNetRegionIpManager.deleteNetworkRegionConfigs(req, ids);
            return new ResponseEntity(HttpStatus.OK);
        }
        catch (Exception e)
        {
            LOGGER.error("create announcement failed.", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "updateConfigStatus", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> updateConfigStatus(String appId, byte networkRegionStatus,
        HttpServletRequest req, String token)
    {
        try
        {
            super.checkToken(token);
            if (AuthApp.NETWORK_REGION_CONFIG_OPENED != networkRegionStatus
                && AuthApp.NETWORK_REGION_CONFIG_CLOSED != networkRegionStatus)
            {
                LOGGER.warn("value of network region status {} not right.", networkRegionStatus);
                throw new BadRquestException("value of network region status " + networkRegionStatus
                    + " not right.");
            }
            
            AuthApp authApp = authAppService.getByAuthAppID(appId);
            
            if (null == authApp)
            {
                LOGGER.warn("authApp {} not exists.", appId);
                throw new NoSuchAuthAppException("authApp " + appId + " not exists.");
            }
            
            authApp.setNetworkRegionStatus(networkRegionStatus);
            String[] description = new String[]{authApp.getAuthAppId(),
                AuthApp.NETWORK_REGION_CONFIG_OPENED == networkRegionStatus ? OPEN_STATUS : CLOSE_STATUS};
            String id = systemLogManager.saveFailLog(req,
                OperateType.NetworkRegionIp,
                OperateDescription.AUTHAPP_NETWORK_REGION_STATUS,
                null,
                description);
            authAppService.updateNetworkRegionStatus(authApp);
            systemLogManager.updateSuccess(id);
            return new ResponseEntity(HttpStatus.OK);
        }
        catch (RuntimeException e)
        {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private String getRegionName(AuthAppNetRegionIp authAppNetRegionIp)
    {
        List<RestRegionInfo> regions = userService.getRegionInfo(authAppNetRegionIp.getAuthAppId());
        
        if (CollectionUtils.isEmpty(regions))
        {
            return null;
        }
        
        for (RestRegionInfo region : regions)
        {
            if (region.getId() == authAppNetRegionIp.getRegionId())
            {
                return region.getName();
            }
        }
        return null;
    }
}
