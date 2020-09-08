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
package com.huawei.sharedrive.uam.authapp.web;

import java.util.List;

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

import com.huawei.sharedrive.uam.authapp.domain.AuthAppNetRegionIp;
import com.huawei.sharedrive.uam.authapp.manager.AuthAppNetRegionIpManager;
import com.huawei.sharedrive.uam.authapp.service.AuthAppNetRegionIpService;
import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.user.service.UserService;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.http.request.RestRegionInfo;
import pw.cdmi.uam.domain.AuthApp;

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
    
    @RequestMapping(value = "{appId}", method = {RequestMethod.GET})
    public String enterList(@PathVariable(value = "appId") String appId, Model model)
    {
        AuthApp authApp = authAppService.getByAuthAppID(appId);
        
        if (null == authApp)
        {
            LOGGER.warn("authApp {} not exists.", appId);
            throw new InvalidParamterException("authApp " + appId + " not exists.");
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
            for (AuthAppNetRegionIp config : ips.getContent())
            {
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
    public ResponseEntity<?> saveConfig(AuthAppNetRegionIp authAppNetRegionIp, String token)
    {
        super.checkToken(token);
        
        try
        {
            if (null == authAppNetRegionIp.getId())
            {
                LOGGER.info("create new app network region config {}.", authAppNetRegionIp);
                authAppNetRegionIpManager.createAuthAppNetRegionIp(authAppNetRegionIp);
            }
            else
            {
                LOGGER.info("update app network region config {}.", authAppNetRegionIp);
                authAppNetRegionIpManager.updateAuthAppNetRegionIp(authAppNetRegionIp);
            }
            return new ResponseEntity(HttpStatus.OK);
        }
        catch (Exception e)
        {
            LOGGER.error("save app network region config failed.", e);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "deleteConfig", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> deleteConfig(long[] ids, String token)
    {
        super.checkToken(token);
        if (null == ids || ids.length == 0)
        {
            LOGGER.warn("id is null");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        
        try
        {
            authAppNetRegionIpManager.deleteNetworkRegionConfigs(ids);
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
    public ResponseEntity<?> updateConfigStatus(String appId, byte networkRegionStatus, String token)
    {
        try
        {
            super.checkToken(token);
            if (AuthApp.NETWORK_REGION_CONFIG_OPENED != networkRegionStatus
                && AuthApp.NETWORK_REGION_CONFIG_CLOSED != networkRegionStatus)
            {
                LOGGER.warn("value of network region status {} not right.", networkRegionStatus);
                throw new InvalidParamterException("value of network region status " + networkRegionStatus
                    + " not right.");
            }
            
            AuthApp authApp = authAppService.getByAuthAppID(appId);
            
            if (null == authApp)
            {
                LOGGER.warn("authApp {} not exists.", appId);
                throw new InvalidParamterException("authApp " + appId + " not exists.");
            }
            
            authApp.setNetworkRegionStatus(networkRegionStatus);
            
            authAppService.updateNetworkRegionStatus(authApp);
            return new ResponseEntity(HttpStatus.OK);
        }
        catch (RuntimeException e)
        {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e)
        {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
