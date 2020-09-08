package com.huawei.sharedrive.uam.enterprise.web;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.session.UnknownSessionException;
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
import com.huawei.sharedrive.uam.enterprise.domain.NetRegion;
import com.huawei.sharedrive.uam.enterprise.domain.ResourceStrategy;
import com.huawei.sharedrive.uam.enterprise.domain.SafeLevel;
import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.NetRegionManager;
import com.huawei.sharedrive.uam.enterprise.manager.ResourceStrategyManager;
import com.huawei.sharedrive.uam.enterprise.manager.SafeLevelManager;
import com.huawei.sharedrive.uam.enterprise.manager.SecurityRoleManager;
import com.huawei.sharedrive.uam.enterprise.service.NetRegionService;
import com.huawei.sharedrive.uam.enterprise.service.ResourceStrategyService;
import com.huawei.sharedrive.uam.enterprise.service.SafeLevelService;
import com.huawei.sharedrive.uam.enterprise.service.SecurityRoleService;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.exception.NoSuchNetworkRegionIpException;
import com.huawei.sharedrive.uam.exception.NoSuchSecurityLevelException;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.SecurityConfigUtil;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/enterprise/security")
public class SecurityLevelController extends AbstractCommonController
{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(SecurityLevelController.class);
    
    @Autowired
    private SafeLevelManager manager;
    
    @Autowired
    private NetRegionManager netRegionManager;
    
    @Autowired
    private SecurityRoleManager securityRoleManager;
    
    @Autowired
    private ResourceStrategyManager resourceStrategyManager;
    
    @Autowired
    private ResourceStrategyService resourceStrategyService;
    
    @Autowired
    private SecurityRoleService securityRoleService;
    
    @Autowired
    private SafeLevelService safeLevelService;
    
    @Autowired
    private NetRegionService netRegionService;
    
    @Autowired
    private SafeLevelManager safeLevelManager;
    
    @Autowired
    private AdminLogManager adminLogManager;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    @Resource(name = "cacheClient")
    private CacheClient cacheClient;
    
    @RequestMapping(value = "listSafeLevel/{appId}/", method = {RequestMethod.GET})
    public String enterSecurityLevel(@PathVariable(value = "appId") String appId, Model model,
        HttpServletRequest httpRequest)
    {
        buildSafeLevelList(appId, model);
        model.addAttribute("appId", appId);
        
        return "enterprise/security/safeLevelList";
    }
    
    @RequestMapping(value = "listResourceStrategy/{appId}/", method = {RequestMethod.GET})
    public String enterResourceStrategy(@PathVariable(value = "appId") String appId, Model model, long id)
    {
        enterpriseAccountManager.bindAppCheck(appId);
        model.addAttribute("appId", appId);
        model.addAttribute("id", id);
        return "enterprise/security/modifySafeLevel";
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "listSafeLevel/{appId}/", method = {RequestMethod.POST})
    public ResponseEntity list(@PathVariable(value = "appId") String appId,
        @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber, Model model,
        SafeLevel safeLevel, String token)
    {
        super.checkToken(token);
        PageRequest request = new PageRequest();
        request.setSize(Constants.DEFAULT_PAGE_SIZE);
        request.setPage(pageNumber);
        safeLevel.setAccountId(getAccoutId(appId));
        if (safeLevel.getId() != null && safeLevel.getId() == -1)
        {
            safeLevel.setId(null);
        }
        Page<SafeLevel> securityRolePage = manager.getFilterd(safeLevel, request);
        
        return new ResponseEntity(securityRolePage, HttpStatus.OK);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "listResourceStrategy/{appId}/", method = {RequestMethod.POST})
    public ResponseEntity listResourceStrategy(@PathVariable(value = "appId") String appId,
        @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
        HttpServletRequest httpRequest, String token, long id)
    {
        super.checkToken(token);
        Locale locale = RequestContextUtils.getLocaleResolver(httpRequest).resolveLocale(httpRequest);
        PageRequest request = new PageRequest();
        request.setSize(Constants.DEFAULT_PAGE_SIZE);
        request.setPage(pageNumber);
        ResourceStrategy rs = new ResourceStrategy();
        rs.setResourceSecurityLevelId(id);
        rs.setAccountId(getAccoutId(appId));
        Page<ResourceStrategy> pageResourceStrategy = resourceStrategyManager.getFilterd(rs, request, locale);
        return new ResponseEntity(pageResourceStrategy, HttpStatus.OK);
    }
    
    @RequestMapping(value = "listNewResourceStrategy/{appId}/", method = {RequestMethod.GET})
    public String enterNewResourceStrategy(@PathVariable(value = "appId") String appId, Model model, long id)
    {
        enterpriseAccountManager.bindAppCheck(appId);
        model.addAttribute("appId", appId);
        model.addAttribute("id", id);
        
        return "enterprise/security/createSafeLevel";
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "listNewResourceStrategy/{appId}/", method = {RequestMethod.POST})
    public ResponseEntity listNewResourceStrategy(@PathVariable(value = "appId") String appId,
        @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
        HttpServletRequest httpRequest, Model model, String token)
    {
        super.checkToken(token);
        Locale locale = RequestContextUtils.getLocaleResolver(httpRequest).resolveLocale(httpRequest);
        PageRequest request = new PageRequest();
        request.setSize(Constants.DEFAULT_PAGE_SIZE);
        request.setPage(pageNumber);
        ResourceStrategy rs = new ResourceStrategy();
        rs.setAccountId(getAccoutId(appId));
        
        String sessionId = httpRequest.getSession().getId();
        String sessionKey = "temp_resourceStrategy_" + sessionId;
        Object cache = cacheClient.getCache(sessionKey);
        List<ResourceStrategy> list = (List<ResourceStrategy>) cache;
        int resourceId = 0;
        for (ResourceStrategy item : list)
        {
            item.setId(resourceId);
            resourceId++;
        }
        Page<ResourceStrategy> pageResourceStrategy = resourceStrategyService.queryFilterd(list,
            rs,
            request,
            locale);
        
        return new ResponseEntity(pageResourceStrategy, HttpStatus.OK);
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "createSafeLevel/{appId}/", method = RequestMethod.GET)
    public String enterCreateLocal(@PathVariable(value = "appId") String appId, Model model,
        HttpServletRequest request)
    {
        long accountId = getAccoutId(appId);
        model.addAttribute("safeRoleList", getSafeRoleList(accountId));
        model.addAttribute("netRegionList", getNetRegionList(accountId));
        return "enterprise/security/createSafeLevel";
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "createResourceStrategy/{appId}/", method = RequestMethod.GET)
    public String enterCreateResourceStrategy(@PathVariable(value = "appId") String appId, Model model,
        HttpServletRequest request, long id)
    {
        long accountId = getAccoutId(appId);
        
        buildSafeLevelList(appId, model);
        model.addAttribute("id", id);
        model.addAttribute("safeRoleList", getSafeRoleList(accountId));
        model.addAttribute("netRegionList", getNetRegionList(accountId));
        return "enterprise/security/createSafeLevel";
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "modifySafeLevel/{appId}/", method = RequestMethod.GET)
    public String enterModify(@PathVariable(value = "appId") String appId, long id, Model model,
        HttpServletRequest httpRequest)
    {
        SafeLevel safeLevel = manager.getById(id);
        long accountId = getAccoutId(appId);
        model.addAttribute("securityRole", safeLevel);
        model.addAttribute("netRegionList", getNetRegionList(accountId));
        model.addAttribute("safeRoleList", getSafeRoleList(accountId));
        return "enterprise/security/modifySafeLevel";
    }
    
    /**
     * 
     * @param admin
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "modifySafeLevel/{appId}/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> modify(@PathVariable(value = "appId") String appId, SafeLevel safeLevel,
        String strRlues, HttpServletRequest req, String token) throws IOException
    {
        super.checkToken(token);
        String[] description = new String[]{getEnterpriseName(), appId, safeLevel.getSafeLevelName(),
            safeLevel.getSafeLevelDesc()};
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(req));
        owner.setAppId(appId);
        try
        {
            checkReqValue(safeLevel);
            safeLevel.setAccountId(getAccoutId(appId));
            manager.modify(safeLevel);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SECURITY_LEVEL_UPDATE, description);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("modify safy level fail");
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SECURITY_LEVEL_UPDATE_ERROR, description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @SuppressWarnings("PMD.PreserveStackTrace")
    private List<ResourceStrategy> handleListStr(String strRlues)
    {
        List<ResourceStrategy> listStr = new ArrayList<ResourceStrategy>(10);
        String[] strTemp;
        if (StringUtils.isNotBlank(strRlues))
        {
            strTemp = strRlues.split("@");
            try
            {
                String[] splitStr;
                ResourceStrategy resourceStrategy;
                for (String s : strTemp)
                {
                    splitStr = s.split(",");
                    resourceStrategy = new ResourceStrategy();
                    resourceStrategy.setNetRegionId(Long.parseLong(splitStr[1]));
                    resourceStrategy.setSecurityRoleId(Long.parseLong(splitStr[0]));
                    listStr.add(resourceStrategy);
                }
            }
            catch (NumberFormatException e)
            {
                LOGGER.error("handleListStr fail:" + e.getMessage());
                throw new InvalidParameterException();
            }
        }
        return listStr;
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void checkReqValue(SafeLevel safeLevel) throws IOException
    {
        Set violations = validator.validate(safeLevel);
        if (!violations.isEmpty())
        {
            throw new ConstraintViolationException(violations);
        }
    }
    
    /**
     * 
     * @param admin
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "createSafeLevel/{appId}/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createLocal(@PathVariable(value = "appId") String appId,
        SafeLevel safeLevel, String strRlues, HttpServletRequest req, String token) throws IOException
    {
        super.checkToken(token);
        String[] description = new String[]{getEnterpriseName(), appId, safeLevel.getSafeLevelName(),
            safeLevel.getSafeLevelDesc()};
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(req));
        owner.setAppId(appId);
        try
        {
            safeLevel.setAccountId(getAccoutId(appId));
            checkReqValue(safeLevel);
            List<ResourceStrategy> listStr = handleListStr(strRlues);
            manager.create(safeLevel, listStr);
            
            String sessionId = req.getSession().getId();
            String sessionKey = "temp_resourceStrategy_" + sessionId;
            Object cache = cacheClient.getCache(sessionKey);
            List<ResourceStrategy> list = (List<ResourceStrategy>) cache;
            if (list != null)
            {
                for (ResourceStrategy item : list)
                {
                    item.setAccountId(getAccoutId(appId));
                    item.setResourceSecurityLevelId(safeLevel.getId());
                    resourceStrategyManager.create(item, safeLevel, req);
                }
                cacheClient.deleteCache(sessionKey);
            }
            
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SECURITY_LEVEL_ADD, description);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("create safe level fail");
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SECURITY_LEVEL_ADD_ERROR, description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    /**
     * 
     * @param admin
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "createResourceStrategy/{appId}/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createResourceStrategy(@PathVariable(value = "appId") String appId,
        ResourceStrategy resourceStrategy, String strRlues, HttpServletRequest httpRequest, String token)
        throws IOException
    {
        super.checkToken(token);
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(httpRequest));
        owner.setAppId(appId);
        try
        {
            String[] description = handleModifyAndCreateResourceStrategyLogInfo(appId,
                resourceStrategy,
                httpRequest);
            resourceStrategy.setAccountId(getAccoutId(appId));
            resourceStrategyManager.create(resourceStrategy);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_RESOURCE_ADD, description);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("create file upload strategy fail");
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_RESOURCE_ADD_ERROR, new String[]{
                getEnterpriseName(), appId});
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "addResourceStrategy/{appId}/", method = RequestMethod.GET)
    public String enterAddResourceStrategy(@PathVariable(value = "appId") String appId, Model model,
        HttpServletRequest request, long id)
    {
        long accountId = getAccoutId(appId);
        
        buildSafeLevelList(appId, model);
        model.addAttribute("id", id);
        model.addAttribute("safeRoleList", getSafeRoleList(accountId));
        model.addAttribute("netRegionList", getNetRegionList(accountId));
        return "enterprise/security/createSafeLevel";
    }
    
    /**
     * 
     * @param admin
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "addResourceStrategy/{appId}/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> addResourceStrategy(@PathVariable(value = "appId") String appId,
        ResourceStrategy resourceStrategy, String strRlues, HttpServletRequest httpRequest, String token)
        throws IOException
    {
        super.checkToken(token);
        
        String sessionId = httpRequest.getSession().getId();
        String sessionKey = "temp_resourceStrategy_" + sessionId;
        Date expireTime = new Date(System.currentTimeMillis() + 600000);
        Object cache = cacheClient.getCache(sessionKey);
        List<ResourceStrategy> list = new ArrayList<ResourceStrategy>(16);
        if (null != cache)
        {
            list = (List<ResourceStrategy>) cache;
        }
        list.add(resourceStrategy);
        boolean success = cacheClient.setCache(sessionKey, list, expireTime);
        if (!success)
        {
            throw new UnknownSessionException("Store session to memcache failed.");
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private List<SecurityRole> getSafeRoleList(long accountId)
    {
        SecurityRole securityRole = new SecurityRole();
        securityRole.setAccountId(accountId);
        List<SecurityRole> list = securityRoleManager.getFilterdList(securityRole);
        return list;
    }
    
    private List<NetRegion> getNetRegionList(long accountId)
    {
        NetRegion netRegion = new NetRegion();
        netRegion.setAccountId(accountId);
        List<NetRegion> list = netRegionManager.getFilterdNetRegionList(netRegion);
        return list;
        
    }
    
    @RequestMapping(value = "deleteSafeLevel/{appId}/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteSafeLevel(@PathVariable(value = "appId") String appId, long id,
        HttpServletRequest req, String token) throws IOException
    {
        super.checkToken(token);
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(req));
        owner.setAppId(appId);
        try
        {
            SafeLevel safeLevel = new SafeLevel();
            safeLevel.setId(id);
            safeLevel.setAccountId(getAccoutId(appId));
            SafeLevel dbSafeLevel = manager.getById(id);
            if (null == dbSafeLevel)
            {
                throw new NoSuchSecurityLevelException();
            }
            String name = dbSafeLevel.getSafeLevelName();
            String[] description = new String[]{getEnterpriseName(), appId, name};
            manager.delete(safeLevel);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SECURITY_LEVEL_DELETE, description);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("delete safy level failO");
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SECURITY_LEVEL_DELETE_ERROR, new String[]{
                getEnterpriseName(), appId});
            throw e;
        }
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "deleteResourceStrategy/{appId}/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteResourceStrategy(@PathVariable(value = "appId") String appId,
        HttpServletRequest req, String token, String ids)
    {
        super.checkToken(token);
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(req));
        owner.setAppId(appId);
        try
        {
            // ResourceStrategy resourceStrategy = new ResourceStrategy();
            // resourceStrategy.setAccountId(getAccoutId(appId));
            String[] idArray = ids.split(",");
            for (int i = 0; i < idArray.length; i++)
            {
                deleteResourceStrategyForOne(appId, owner, idArray, i, req);
            }
        }
        catch (RuntimeException e)
        {
            LOGGER.error("delete safy level fail");
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_RESOURCE_DELETE_ERROR, new String[]{
                getEnterpriseName(), appId});
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @SuppressWarnings("PMD.PreserveStackTrace")
    private void deleteResourceStrategyForOne(String appId, LogOwner owner, String[] idArray, int i,
        HttpServletRequest req)
    {
        ResourceStrategy rs = resourceStrategyManager.getResourceStrategyId(Long.parseLong(idArray[i]));
        if (null == rs)
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_RESOURCE_DELETE_ERROR, new String[]{
                getEnterpriseName(), appId});
            throw new NoSuchNetworkRegionIpException();
        }
        String[] description = handleModifyAndCreateResourceStrategyLogInfo(appId, rs, req);
        try
        {
            resourceStrategyManager.deleteStrategy(Integer.parseInt(idArray[i]));
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_RESOURCE_DELETE, description);
        }
        catch (NumberFormatException e)
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_RESOURCE_DELETE_ERROR, description);
            throw new InvalidParamterException("parse id to int value failed");
        }
        catch (IOException e)
        {
            LOGGER.warn("", e);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_RESOURCE_DELETE_ERROR, description);
        }
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "modifyResourceStrategy/{appId}/", method = RequestMethod.GET)
    public String enterModifyResourceStrategy(@PathVariable(value = "appId") String appId, long id,
        Model model, HttpServletRequest httpRequest)
    {
        long accountId = getAccoutId(appId);
        ResourceStrategy rs = new ResourceStrategy();
        rs.setId(id);
        rs.setAccountId(accountId);
        ResourceStrategy resourceStrategy = resourceStrategyManager.getResourceStrategy(rs);
        
        buildSafeLevelList(appId, model);
        
        model.addAttribute("resourceStrategy", resourceStrategy);
        model.addAttribute("safeRoleList", getSafeRoleList(accountId));
        model.addAttribute("netRegionList", getNetRegionList(accountId));
        
        return "enterprise/security/modifyResourceStrategy";
    }
    
    private void buildSafeLevelList(String appId, Model model)
    {
        SafeLevel safeLevel = new SafeLevel();
        safeLevel.setAccountId(getAccoutId(appId));
        List<SafeLevel> list = safeLevelManager.getFilterdList(safeLevel);
        model.addAttribute("safeLevelList", list);
    }
    
    /**
     * 
     * @param admin
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "modifyResourceStrategy/{appId}/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> modifyResourceStrategy(@PathVariable(value = "appId") String appId,
        ResourceStrategy resourceStrategy, String strRlues, HttpServletRequest httpRequest, String token)
        throws IOException
    {
        super.checkToken(token);
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(httpRequest));
        owner.setAppId(appId);
        try
        {
            String[] description = handleModifyAndCreateResourceStrategyLogInfo(appId,
                resourceStrategy,
                httpRequest);
            resourceStrategy.setAccountId(getAccoutId(appId));
            resourceStrategyManager.modify(resourceStrategy);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_RESOURCE_UPDATE, description);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("modify Resource Strategy fail");
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_RESOURCE_UPDATE_ERROR, new String[]{
                getEnterpriseName(), appId});
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private String[] handleModifyAndCreateResourceStrategyLogInfo(String appId,
        ResourceStrategy resourceStrategy, HttpServletRequest httpRequest)
    {
        Locale locale = RequestContextUtils.getLocaleResolver(httpRequest).resolveLocale(httpRequest);
        long accountId = getAccoutId(appId);
        long roleId = resourceStrategy.getSecurityRoleId();
        long netRegionId = resourceStrategy.getNetRegionId();
        long resourceStrategyId = resourceStrategy.getResourceSecurityLevelId();
        
        SecurityRole sr = new SecurityRole();
        sr.setAccountId(accountId);
        List<SecurityRole> listSecurityRole = securityRoleService.getFilterdList(sr);
        String securityRoleName = SecurityConfigUtil.getSecurityRileById(roleId, listSecurityRole, locale);
        
        SafeLevel rs = new SafeLevel();
        rs.setAccountId(accountId);
        List<SafeLevel> listSafeLevel = safeLevelService.getFilterdList(rs);
        String resourceStrategyName = SecurityConfigUtil.getResourceTypeNameById(resourceStrategyId,
            listSafeLevel,
            locale);
        
        NetRegion netRegion = new NetRegion();
        netRegion.setAccountId(accountId);
        List<NetRegion> listNetRegion = netRegionService.getFilterdList(netRegion);
        String netRegionName = SecurityConfigUtil.getNetRegionNameById(netRegionId, listNetRegion, locale);
        
        String[] description = new String[]{getEnterpriseName(), appId, securityRoleName, netRegionName,
            resourceStrategyName};
        return description;
    }
    
    @RequestMapping(value = "deleteNewAddResourceStrategy/{appId}/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteNewAddResourceStrategy(@PathVariable(value = "appId") String appId,
        String ids, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        String sessionId = req.getSession().getId();
        String sessionKey = "temp_resourceStrategy_" + sessionId;
        Object cache = cacheClient.getCache(sessionKey);
        List<ResourceStrategy> list = (List<ResourceStrategy>) cache;
        /*
         * for (ResourceStrategy item : list) { item.setAccountId(getAccoutId(appId));
         * cacheClient.deleteCache(sessionKey); }
         */
        String[] idArray = ids.split(",");
        for (int i = idArray.length - 1; i >= 0; i--)
        {
            list.remove(Integer.parseInt(idArray[i]));
        }
        Date expireTime = new Date(System.currentTimeMillis() + 600000);
        cacheClient.setCache(sessionKey, list, expireTime);
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "deleteCloseNewFileType/{appId}/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteCloseNewFileType(@PathVariable(value = "appId") String appId,
        String ids, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        String sessionId = req.getSession().getId();
        String sessionKey = "temp_resourceStrategy_" + sessionId;
        Object cache = cacheClient.getCache(sessionKey);
        List<ResourceStrategy> list = (List<ResourceStrategy>) cache;
        for (ResourceStrategy item : list)
        {
            item.setAccountId(getAccoutId(appId));
            cacheClient.deleteCache(sessionKey);
        }
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
