package com.huawei.sharedrive.uam.enterprise.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfigExt;
import com.huawei.sharedrive.uam.enterprise.domain.AccessSpaceConfigResponse;
import com.huawei.sharedrive.uam.enterprise.domain.ClientType;
import com.huawei.sharedrive.uam.enterprise.domain.ClientTypeEnum;
import com.huawei.sharedrive.uam.enterprise.domain.NetRegion;
import com.huawei.sharedrive.uam.enterprise.domain.SafeLevel;
import com.huawei.sharedrive.uam.enterprise.domain.SecOperation;
import com.huawei.sharedrive.uam.enterprise.domain.SecOperationConstants;
import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;
import com.huawei.sharedrive.uam.enterprise.manager.AccessConfigManager;
import com.huawei.sharedrive.uam.enterprise.manager.AccessSpaceManager;
import com.huawei.sharedrive.uam.enterprise.manager.NetRegionManager;
import com.huawei.sharedrive.uam.enterprise.manager.SafeLevelManager;
import com.huawei.sharedrive.uam.enterprise.manager.SecurityRoleManager;
import com.huawei.sharedrive.uam.enterprise.service.NetRegionService;
import com.huawei.sharedrive.uam.enterprise.service.SecurityRoleService;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.SecurityConfigConstants;
import com.huawei.sharedrive.uam.util.SecurityConfigUtil;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.utils.BundleUtil;
import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/enterprise/space")
public class AccessSpaceController extends AbstractCommonController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(AccessSpaceController.class);
    
    @Autowired
    private AccessSpaceManager accessSpaceManager;
    
    @Autowired
    private NetRegionManager netRegionManager;
    
    @Autowired
    private SecurityRoleManager securityRoleManager;
    
    @Autowired
    private AdminLogManager adminLogManager;
    
    @Autowired
    private SecurityRoleService securityRoleService;
    
    @Autowired
    private NetRegionService netRegionService;
    
    @Autowired
    private AccessConfigManager manager;
    
    @Autowired
    private SafeLevelManager safeLevelManager;
    
    @RequestMapping(value = "createAccessConfig/{appId}", method = RequestMethod.POST)
    public ResponseEntity<String> createConfig(AccessSpaceConfigExt accessSpace, HttpServletRequest request,
        @PathVariable(value = "appId") String appId, String token) throws IOException
    {
        super.checkToken(token);
        LogOwner owner = new LogOwner();
        owner.setAppId(appId);
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(request));
        try
        {
            AccessSpaceConfig acc = new AccessSpaceConfig();
            BeanUtils.copyProperties(accessSpace, acc);
            String[] operTypes = accessSpace.getOperation().split(",");
            acc.setOperation(SecurityConfigUtil.handleOperationMatrix(operTypes));
            String[] description = handleModifyAndCreateResourceStrategyLogInfo(appId, acc, request);
            Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
            long accountId = getAccoutId(appId);
            accessSpace.setAccountId(accountId);
            accessSpaceManager.createConfig(accessSpace, locale);
            AccessConfig accessConfig = new AccessConfig();
            accessConfig.setAccountId(accountId);
            accessConfig.setNetRegionId(accessSpace.getNetRegionId());
            accessConfig.setSafeRoleId(accessSpace.getSafeRoleId());
            accessConfig.setSpaceRoleId(accessSpace.getTargetSafeRoleId());
            accessConfig.setDownLoadResrouceTypeIds(accessSpace.getDownLoadResrouceTypeIds());
            accessConfig.setPreviewResourceTypeIds(accessSpace.getPreviewResourceTypeIds());
            manager.create(accessConfig, locale);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCESSCONFIG_SPACE_ADD, description);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("create access config fail", e);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCESSCONFIG_SPACE_ADD_ERROR, new String[]{
                getEnterpriseName(), appId});
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "delSpaceConfig/{appId}", method = RequestMethod.POST)
    public ResponseEntity<?> delSpaceConfig(HttpServletRequest request, String id,
        @PathVariable(value = "appId") String appId, HttpServletRequest httpRequest, String token)
    {
        super.checkToken(token);
        LogOwner owner = new LogOwner();
        owner.setAppId(appId);
        Long enterpriseId = checkAdminAndGetId();
        owner.setEnterpriseId(enterpriseId);
        owner.setIp(IpUtils.getClientAddress(request));
        try
        {
            AccessSpaceConfig accessSpace = accessSpaceManager.getById(id);
            String[] description = handleModifyAndCreateResourceStrategyLogInfo(appId,
                accessSpace,
                httpRequest);
            
            AccessConfig accessConfig = new AccessConfig();
            long accountId = getAccoutId(appId);
            accessConfig.setAccountId(accountId);
            accessConfig.setNetRegionId(accessSpace.getNetRegionId());
            accessConfig.setSafeRoleId(accessSpace.getSafeRoleId());
            accessConfig.setSpaceRoleId(accessSpace.getTargetSafeRoleId());
            manager.deleteByCondition(accessConfig);
            
            accessSpaceManager.deleteConfig(id);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCESSCONFIG_SPACE_DELETE, description);
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("delete access config fail");
            adminLogManager.saveAdminLog(owner,
                AdminLogType.KEY_ACCESSCONFIG_SPACE_DELETE_ERROR,
                new String[]{getEnterpriseName(), appId});
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "listAccessConfig/{appId}", method = RequestMethod.GET)
    public String enterAccessConfig(Model model, HttpServletRequest request,
        @PathVariable(value = "appId") String appId)
    {
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        long accountId = getAccoutId(appId);
        
        buildSafeLevelList(appId, model);
        
        model.addAttribute("netRegionList", getNetRegionList(accountId));
        model.addAttribute("clientTypeList", getClientTypeList(locale));
        model.addAttribute("safeRoleList", getSafeRoleList(accountId));
        return "enterprise/space/accessConfigList";
    }
    
    @RequestMapping(value = "createAccessConfig/{appId}", method = RequestMethod.GET)
    public String enterCreateLocal(Model model, HttpServletRequest request,
        @PathVariable(value = "appId") String appId)
    {
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        long accountId = getAccoutId(appId);
        buildSafeLevelList(appId, model);
        model.addAttribute("netRegionList", getNetRegionList(accountId));
        model.addAttribute("clientTypeList", getClientTypeList(locale));
        model.addAttribute("safeRoleList", getSafeRoleList(accountId));
        model.addAttribute("targetSafeList", getSafeRoleList(accountId));
        return "enterprise/space/createAccessConfig";
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "modifyAccessConfig/{appId}/", method = RequestMethod.GET)
    public String enterModify(@PathVariable(value = "appId") String appId, String id, Model model,
        HttpServletRequest httpRequest)
    {
        Locale locale = RequestContextUtils.getLocaleResolver(httpRequest).resolveLocale(httpRequest);
        PageRequest request = new PageRequest();
        request.setSize(Constants.DEFAULT_PAGE_SIZE);
        StringBuilder operation = new StringBuilder();
        AccessSpaceConfig accessSpaceConfig = accessSpaceManager.getById(id);
        
        List<SecOperation> list = SecOperation.getSecOperations(accessSpaceConfig.getOperation());
        long value;
        for (SecOperation so : list)
        {
            value = so.getIntValue();
            operation.append(value);
            operation.append(SecOperationConstants.SIGN);
        }
        AccessSpaceConfigExt accessSpaceConfigExt = new AccessSpaceConfigExt();
        accessSpaceConfigExt.setOperation(operation.toString());
        model.addAttribute("accessConfig", accessSpaceConfigExt);
        model.addAttribute("accessConfigList", accessSpaceConfig);
        
        long accountId = getAccoutId(appId);
        model.addAttribute("netRegionList", getNetRegionList(accountId));
        model.addAttribute("clientTypeList", getClientTypeList(locale));
        model.addAttribute("safeRoleList", getSafeRoleList(accountId));
        model.addAttribute("targetSafeList", getSafeRoleList(accountId));
        
        AccessConfig accessConfig = manager.getByAllType(accessSpaceConfig);
        if (null != accessConfig)
        {
            model.addAttribute("config", accessConfig);
        }
        else
        {
            LOGGER.error("the modified accessconfig does not exists.");
        }
        
        buildSafeLevelList(appId, model);
        
        model.addAttribute("downLoadResrouceTypeIds",
            accessConfig == null ? "" : accessConfig.getDownLoadResrouceTypeIds());
        model.addAttribute("previewResourceTypeIds",
            accessConfig == null ? "" : accessConfig.getPreviewResourceTypeIds());
        return "enterprise/space/modifyAccessConfig";
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
        AccessSpaceConfigExt accessSpace, HttpServletRequest httpRequest, String token) throws IOException
    {
        super.checkToken(token);
        LogOwner owner = new LogOwner();
        owner.setAppId(appId);
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(httpRequest));
        try
        {
            AccessSpaceConfig old = accessSpaceManager.getById(accessSpace.getId());
            
            AccessSpaceConfig acc = new AccessSpaceConfig();
            BeanUtils.copyProperties(accessSpace, acc);
            String[] operTypes = accessSpace.getOperation().split(",");
            acc.setOperation(SecurityConfigUtil.handleOperationMatrix(operTypes));
            
            String[] description = handleModifyAndCreateResourceStrategyLogInfo(appId, acc, httpRequest);
            Locale locale = RequestContextUtils.getLocaleResolver(httpRequest).resolveLocale(httpRequest);
            accessSpace.setAccountId(getAccoutId(appId));
            
            accessSpaceManager.modify(accessSpace, locale);
            AccessConfig accessConfig = new AccessConfig();
            long accountId = getAccoutId(appId);
            accessConfig.setAccountId(accountId);
            accessConfig.setNetRegionId(old.getNetRegionId());
            accessConfig.setSafeRoleId(old.getSafeRoleId());
            accessConfig.setSpaceRoleId(old.getTargetSafeRoleId());
            manager.deleteByCondition(accessConfig);
            
            accessConfig = new AccessConfig();
            accessConfig.setAccountId(accountId);
            accessConfig.setNetRegionId(accessSpace.getNetRegionId());
            accessConfig.setSafeRoleId(accessSpace.getSafeRoleId());
            accessConfig.setSpaceRoleId(accessSpace.getTargetSafeRoleId());
            accessConfig.setDownLoadResrouceTypeIds(accessSpace.getDownLoadResrouceTypeIds());
            accessConfig.setPreviewResourceTypeIds(accessSpace.getPreviewResourceTypeIds());
            manager.create(accessConfig, locale);
            
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCESSCONFIG_SPACE_CHANGE, description);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("change access config fail", e);
            adminLogManager.saveAdminLog(owner,
                AdminLogType.KEY_ACCESSCONFIG_SPACE_CHANGE_ERROR,
                new String[]{getEnterpriseName(), appId});
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "listAccessConfig/{appId}", method = RequestMethod.POST)
    public ResponseEntity<?> listAccessConfig(HttpServletRequest httpRequest,
        @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber, Model model,
        @PathVariable(value = "appId") String appId, AccessSpaceConfigExt accessSpaceConfigExt, String token)
    {
        super.checkToken(token);
        Locale locale = RequestContextUtils.getLocaleResolver(httpRequest).resolveLocale(httpRequest);
        long accountId = getAccoutId(appId);
        accessSpaceConfigExt.setAccountId(accountId);
        PageRequest request = new PageRequest(pageNumber, Constants.DEFAULT_PAGE_SIZE);
        Page<AccessSpaceConfig> spaceConfigs = accessSpaceManager.getAccessSpaceList(accessSpaceConfigExt,
            request,
            locale);
        List<AccessSpaceConfigResponse> content = new ArrayList<AccessSpaceConfigResponse>(
            spaceConfigs.getContent().size());
        transSpaceConfig(locale, spaceConfigs, content);
        Page<AccessSpaceConfigResponse> pages = new PageImpl<AccessSpaceConfigResponse>(content, request,
            spaceConfigs.getTotalElements());
        return new ResponseEntity<Page<AccessSpaceConfigResponse>>(pages, HttpStatus.OK);
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
    
    private List<NetRegion> getNetRegionList(long accountId)
    {
        NetRegion nr = new NetRegion();
        nr.setAccountId(accountId);
        List<NetRegion> list = netRegionManager.getFilterdNetRegionList(nr);
        return list;
        
    }
    
    private List<SecurityRole> getSafeRoleList(long accountId)
    {
        SecurityRole sSecurityRole = new SecurityRole();
        sSecurityRole.setAccountId(accountId);
        List<SecurityRole> list = securityRoleManager.getFilterdList(sSecurityRole);
        return list;
    }
    
    private void transSpaceConfig(Locale locale, Page<AccessSpaceConfig> spaceConfigs,
        List<AccessSpaceConfigResponse> content)
    {
        AccessSpaceConfigResponse spaceConfigResponse;
        List<SecOperation> list;
        StringBuilder operateType;
        long value;
        AccessConfig accessConfig = null;
        Page<AccessConfig> accessConfigPage;
        for (AccessSpaceConfig accessSpaceConfig : spaceConfigs.getContent())
        {
            spaceConfigResponse = new AccessSpaceConfigResponse(accessSpaceConfig);
            list = SecOperation.getSecOperations(accessSpaceConfig.getOperation());
            operateType = new StringBuilder();
            for (SecOperation so : list)
            {
                value = so.getIntValue();
                operateType.append(BundleUtil.getText(SecOperationConstants.BUNDLE_NAME,
                    locale,
                    SecOperationConstants.getOperateTypes().get(value)));
                operateType.append(SecOperationConstants.SIGN);
            }
            if (CollectionUtils.isNotEmpty(list))
            {
                String newTypes = operateType.substring(0,
                    operateType.lastIndexOf(SecOperationConstants.SIGN));
                spaceConfigResponse.setOperation(newTypes);
            }
            else
            {
                spaceConfigResponse.setOperation("-");
            }
            
            accessConfig = new AccessConfig();
            accessConfig.setAccountId(accessSpaceConfig.getAccountId());
            accessConfig.setNetRegionId(accessSpaceConfig.getNetRegionId());
            accessConfig.setSafeRoleId(accessSpaceConfig.getSafeRoleId());
            accessConfig.setSpaceRoleId(accessSpaceConfig.getTargetSafeRoleId());
            accessConfigPage = manager.getFilterd(accessConfig, null, locale);
            if (accessConfigPage != null && CollectionUtils.isNotEmpty(accessConfigPage.getContent()))
            {
                accessConfig = accessConfigPage.getContent().get(0);
                spaceConfigResponse.setDownLoadResrouceTypeIds(accessConfig.getDownLoadResrouceTypeIds());
                spaceConfigResponse.setPreviewResourceTypeIds(accessConfig.getPreviewResourceTypeIds());
            }
            content.add(spaceConfigResponse);
        }
    }
    
    private String[] handleModifyAndCreateResourceStrategyLogInfo(String appId, AccessSpaceConfig acc,
        HttpServletRequest httpRequest)
    {
        Locale locale = RequestContextUtils.getLocaleResolver(httpRequest).resolveLocale(httpRequest);
        long accountId = getAccoutId(appId);
        long roleId = acc.getSafeRoleId();
        long netRegionId = acc.getNetRegionId();
        long targetRoleId = acc.getTargetSafeRoleId();
        
        SecurityRole sr = new SecurityRole();
        sr.setAccountId(accountId);
        List<SecurityRole> listSecurityRole = securityRoleService.getFilterdList(sr);
        String securityRoleName = SecurityConfigUtil.getSecurityRileById(roleId, listSecurityRole, locale);
        
        String clientName = SecurityConfigUtil.getClientTypeById(SecurityConfigConstants.TYPE_OF_SELECT_ALL,
            locale);
        
        NetRegion netRegion = new NetRegion();
        netRegion.setAccountId(accountId);
        List<NetRegion> listNetRegion = netRegionService.getFilterdList(netRegion);
        String netRegionName = SecurityConfigUtil.getNetRegionNameById(netRegionId, listNetRegion, locale);
        
        String targetRoleName = SecurityConfigUtil.getSecurityRileById(targetRoleId, listSecurityRole, locale);
        
        AccessSpaceConfigResponse spaceConfigResponse = new AccessSpaceConfigResponse(acc);
        List<SecOperation> listSecOperation = SecOperation.getSecOperations(acc.getOperation());
        StringBuffer operateType = new StringBuffer();
        for (SecOperation so : listSecOperation)
        {
            operateType.append(BundleUtil.getText(SecOperationConstants.BUNDLE_NAME,
                locale,
                SecOperationConstants.getOperateTypes().get(so.getIntValue())));
            operateType.append(SecOperationConstants.SIGN);
        }
        if (CollectionUtils.isNotEmpty(listSecOperation))
        {
            String newTypes = operateType.substring(0, operateType.lastIndexOf(SecOperationConstants.SIGN));
            spaceConfigResponse.setOperation(newTypes);
        }
        else
        {
            spaceConfigResponse.setOperation("-");
        }
        String operation = spaceConfigResponse.getOperation();
        
        String[] description = new String[]{getEnterpriseName(), appId, securityRoleName, netRegionName,
            clientName, targetRoleName, operation};
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
