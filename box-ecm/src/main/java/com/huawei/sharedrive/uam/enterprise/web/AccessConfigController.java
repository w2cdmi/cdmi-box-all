package com.huawei.sharedrive.uam.enterprise.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
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
import org.springframework.web.servlet.support.RequestContextUtils;

import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.domain.AccessConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccountSecConfig;
import com.huawei.sharedrive.uam.enterprise.domain.ClientType;
import com.huawei.sharedrive.uam.enterprise.domain.ClientTypeEnum;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegion;
import com.huawei.sharedrive.uam.enterprise.domain.SafeLevel;
import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;
import com.huawei.sharedrive.uam.enterprise.manager.AccessConfigManager;
import com.huawei.sharedrive.uam.enterprise.manager.AccountSecManager;
import com.huawei.sharedrive.uam.enterprise.manager.NetRegionManager;
import com.huawei.sharedrive.uam.enterprise.manager.SafeLevelManager;
import com.huawei.sharedrive.uam.enterprise.manager.SecurityRoleManager;
import com.huawei.sharedrive.uam.enterprise.service.NetRegionService;
import com.huawei.sharedrive.uam.enterprise.service.SafeLevelService;
import com.huawei.sharedrive.uam.enterprise.service.SecurityRoleService;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.SecurityConfigConstants;
import com.huawei.sharedrive.uam.util.SecurityConfigUtil;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/enterprise/security")
public class AccessConfigController extends AbstractCommonController
{
    private static Logger logger = LoggerFactory.getLogger(AccessConfigController.class);
    
    @Autowired
    private SafeLevelManager safeLevelManager;
    
    @Autowired
    private NetRegionManager netRegionManager;
    
    @Autowired
    private SecurityRoleManager securityRoleManager;
    
    @Autowired
    private AccessConfigManager manager;
    
    @Autowired
    private AccountSecManager accountSecManager;
    
    @Autowired
    private AdminLogManager adminLogManager;
    
    @Autowired
    private SecurityRoleService securityRoleService;
    
    @Autowired
    private SafeLevelService safeLevelService;
    
    @Autowired
    private NetRegionService netRegionService;
    
    @RequestMapping(value = "listAccessConfig/{appId}/", method = {RequestMethod.GET})
    public String enterSecurityRole(@PathVariable(value = "appId") String appId, Model model,
        HttpServletRequest httpRequest)
    {
        Locale locale = RequestContextUtils.getLocaleResolver(httpRequest).resolveLocale(httpRequest);
        long accountId = getAccoutId(appId);
        buildSafeLevelList(appId, model);
        buildNetRegionList(model, accountId);
        
        model.addAttribute("clientTypeList", getClientTypeList(locale));
        model.addAttribute("safeRoleList", getSafeRoleList(accountId));
        return "enterprise/security/accessConfigList";
    }
    
    @SuppressWarnings({"rawtypes"})
    @RequestMapping(value = "listAccessConfigSwitch/{appId}/", method = {RequestMethod.POST})
    public ResponseEntity listAccessConfigSwitch(@PathVariable(value = "appId") String appId, String token)
    {
        super.checkToken(token);
        AccountSecConfig secConfig = accountSecManager.getSwitch((int) getAccoutId(appId));
        if (null == secConfig)
        {
            secConfig = new AccountSecConfig();
            secConfig.setEnableFileSec(AccountSecConfig.DISABLE);
            return new ResponseEntity<AccountSecConfig>(secConfig, HttpStatus.OK);
        }
        return new ResponseEntity<AccountSecConfig>(secConfig, HttpStatus.OK);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked", "PMD.ExcessiveParameterList"})
    @RequestMapping(value = "listAccessConfig/{appId}/", method = {RequestMethod.POST})
    public ResponseEntity list(@PathVariable(value = "appId") String appId,
        @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber, HttpServletRequest httpRequest, AccessConfig accessConfig, String token)
    {
        super.checkToken(token);
        Locale locale = RequestContextUtils.getLocaleResolver(httpRequest).resolveLocale(httpRequest);
        PageRequest request = new PageRequest(pageNumber, Constants.DEFAULT_PAGE_SIZE);
        
        accessConfig.setAccountId(getAccoutId(appId));
        
        Page<AccessConfig> securityRolePage = manager.getFilterd(accessConfig, request, locale);
        return new ResponseEntity(securityRolePage, HttpStatus.OK);
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "createAccessConfig/{appId}/", method = RequestMethod.GET)
    public String enterCreateLocal(@PathVariable(value = "appId") String appId, Model model,
        HttpServletRequest request)
    {
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        long accountId = getAccoutId(appId);
        buildSafeLevelList(appId, model);
        buildNetRegionList(model, accountId);
        
        model.addAttribute("clientTypeList", getClientTypeList(locale));
        model.addAttribute("safeRoleList", getSafeRoleList(accountId));
        return "enterprise/security/createAccessConfig";
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "modifyAccessConfig/{appId}/", method = RequestMethod.GET)
    public String enterModify(@PathVariable(value = "appId") String appId, long id, Model model,
        HttpServletRequest httpRequest)
    {
        Locale locale = RequestContextUtils.getLocaleResolver(httpRequest).resolveLocale(httpRequest);
        AccessConfig accessConfig = manager.getById(id);
        if (null != accessConfig)
        {
            model.addAttribute("accessConfig", accessConfig);
        }
        else
        {
            logger.error("the modified accessconfig does not exists.");
        }
        long accountId = getAccoutId(appId);
        
        buildSafeLevelList(appId, model);
        buildNetRegionList(model, accountId);
        
        model.addAttribute("clientTypeList", getClientTypeList(locale));
        model.addAttribute("safeRoleList", getSafeRoleList(accountId));
        
        model.addAttribute("downLoadResrouceTypeIds",
            accessConfig == null ? "" : accessConfig.getDownLoadResrouceTypeIds());
        model.addAttribute("previewResourceTypeIds",
            accessConfig == null ? "" : accessConfig.getPreviewResourceTypeIds());
        
        return "enterprise/security/modifyAccessConfig";
    }
    
    private void buildNetRegionList(Model model, long accountId)
    {
        NetRegion nr = new NetRegion();
        nr.setAccountId(accountId);
        List<NetRegion> list = netRegionManager.getFilterdNetRegionList(nr);
        model.addAttribute("netRegionList", list);
    }
    
    /**
     * 
     * @param admin
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "modifyAccessConfig/{appId}/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> modify(@PathVariable(value = "appId") String appId,
        AccessConfig securityRole, HttpServletRequest httpRequest, String token) throws IOException
    {
        super.checkToken(token);
        LogOwner owner = new LogOwner();
        owner.setAppId(appId);
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(httpRequest));
        Locale locale = RequestContextUtils.getLocaleResolver(httpRequest).resolveLocale(httpRequest);
        try
        {
            String[] description = handleModifyAndCreateResourceStrategyLogInfo(appId,
                securityRole,
                httpRequest);
            checkRequestValue(securityRole);
            securityRole.setAccountId(getAccoutId(appId));
            manager.modify(securityRole, locale);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCESSCONFIG_CHANGE, description);
        }
        catch (RuntimeException e)
        {
            logger.error("modify file access config fail", e);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCESSCONFIG_CHANGE_ERROR, new String[]{
                getEnterpriseName(), appId});
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @SuppressWarnings({"PMD.ExcessiveParameterList", "PMD.PreserveStackTrace"})
    @RequestMapping(value = "deleteAccessConfig/{appId}", method = RequestMethod.POST)
    public ResponseEntity<?> delete(HttpServletRequest request, String id,
        @PathVariable(value = "appId") String appId, HttpServletRequest httpRequest, String token)
        throws NumberFormatException, IOException
    {
        super.checkToken(token);
        LogOwner owner = new LogOwner();
        owner.setAppId(appId);
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(request));
        try
        {
            AccessConfig accessConfig = manager.getById(Long.parseLong(id));
            String[] description = handleModifyAndCreateResourceStrategyLogInfo(appId,
                accessConfig,
                httpRequest);
            
            if (StringUtils.isBlank(id))
            {
                logger.error("id is null");
                adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCESSCONFIG_DELETE_ERROR, description);
                throw new InvalidParamterException();
            }
            
            manager.delete(Integer.parseInt(id), getAccoutId(appId));
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCESSCONFIG_DELETE, description);
        }
        catch (RuntimeException e)
        {
            logger.error("delete file access config fail");
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCESSCONFIG_DELETE_ERROR, new String[]{
                getEnterpriseName(), appId});
            throw new InvalidParamterException();
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void checkRequestValue(AccessConfig securityRole)
    {
        Set violations = validator.validate(securityRole);
        if (!violations.isEmpty())
        {
            logger.error("invalid request parameter.");
            throw new ConstraintViolationException(violations);
        }
    }
    
    /**
     * 
     * @param admin
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "createAccessConfig/{appId}/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createLocal(@PathVariable(value = "appId") String appId,
        AccessConfig accessConfig, HttpServletRequest httpRequest, String token) throws IOException
    {
        super.checkToken(token);
        LogOwner owner = new LogOwner();
        owner.setAppId(appId);
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(httpRequest));
        Locale locale = RequestContextUtils.getLocaleResolver(httpRequest).resolveLocale(httpRequest);
        try
        {
            String[] description = handleModifyAndCreateResourceStrategyLogInfo(appId,
                accessConfig,
                httpRequest);
            checkRequestValue(accessConfig);
            accessConfig.setAccountId(getAccoutId(appId));
            manager.create(accessConfig, locale);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCESSCONFIG_ADD, description);
        }
        catch (RuntimeException e)
        {
            logger.error("create file access config fail", e);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCESSCONFIG_ADD_ERROR, new String[]{
                getEnterpriseName(), appId});
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private List<SecurityRole> getSafeRoleList(long accountId)
    {
        SecurityRole sSecurityRole = new SecurityRole();
        sSecurityRole.setAccountId(accountId);
        List<SecurityRole> list = securityRoleManager.getFilterdList(sSecurityRole);
        return list;
    }
    
    private List<ClientType> getClientTypeList(Locale locale)
    {
        ClientTypeEnum[] clientTypes = ClientTypeEnum.values();
        List<ClientTypeEnum> operateTypeList = new ArrayList<ClientTypeEnum>(10);
        for (ClientTypeEnum operateType : clientTypes)
        {
            operateTypeList.add(operateType);
        }
        List<ClientType> listDomain = new ArrayList<ClientType>(10);
        ClientType operateTypeDomain;
        for (int i = 0; i < operateTypeList.size(); i++)
        {
            operateTypeDomain = new ClientType();
            operateTypeDomain.setId(operateTypeList.get(i).getCode());
            operateTypeDomain.setClientTypeName(operateTypeList.get(i).getDetails(locale, null));
            listDomain.add(operateTypeDomain);
        }
        return listDomain;
    }
    
    private String[] handleModifyAndCreateResourceStrategyLogInfo(String appId, AccessConfig accessConfig,
        HttpServletRequest httpRequest)
    {
        Locale locale = RequestContextUtils.getLocaleResolver(httpRequest).resolveLocale(httpRequest);
        long accountId = getAccoutId(appId);
        long roleId = accessConfig.getSafeRoleId();
        
        long spaceRoleId = accessConfig.getSafeRoleId();
        
        long netRegionId = accessConfig.getNetRegionId();
        
        SecurityRole sr = new SecurityRole();
        sr.setAccountId(accountId);
        List<SecurityRole> listSecurityRole = securityRoleService.getFilterdList(sr);
        String securityRoleName = SecurityConfigUtil.getSecurityRileById(roleId, listSecurityRole, locale);
        
        String spaceRoleName = SecurityConfigUtil.getSecurityRileById(spaceRoleId, listSecurityRole, locale);
        
        String clientName = SecurityConfigUtil.getClientTypeById(SecurityConfigConstants.TYPE_OF_SELECT_ALL,
            locale);
        
        NetRegion netRegion = new NetRegion();
        netRegion.setAccountId(accountId);
        List<NetRegion> listNetRegion = netRegionService.getFilterdList(netRegion);
        String netRegionName = SecurityConfigUtil.getNetRegionNameById(netRegionId, listNetRegion, locale);
        
        SafeLevel safeLevel = new SafeLevel();
        safeLevel.setAccountId(accountId);
        List<SafeLevel> listSafeLevel = safeLevelService.getFilterdList(safeLevel);
        
        String downloadName = SecurityConfigUtil.getResourceTypeByIds(accessConfig.getDownLoadResrouceTypeIds(),
            locale,
            listSafeLevel);
        
        String previewName = SecurityConfigUtil.getResourceTypeByIds(accessConfig.getPreviewResourceTypeIds(),
            locale,
            listSafeLevel);
        String[] description = new String[]{getEnterpriseName(), appId, securityRoleName, spaceRoleName,
            netRegionName, clientName, downloadName, previewName};
        return description;
    }
    
    private void buildSafeLevelList(String appId, Model model)
    {
        SafeLevel safeLevel = new SafeLevel();
        safeLevel.setAccountId(getAccoutId(appId));
        List<SafeLevel> list = SecurityConfigUtil.getSafeLevelList(safeLevelManager.getFilterdList(safeLevel));
        model.addAttribute("safeLevelList", list);
    }
    
}
