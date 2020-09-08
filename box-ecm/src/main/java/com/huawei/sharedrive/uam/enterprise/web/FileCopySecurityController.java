package com.huawei.sharedrive.uam.enterprise.web;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.servlet.support.RequestContextUtils;

import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.domain.FileCopyCreateRequest;
import com.huawei.sharedrive.uam.enterprise.domain.FileCopyDelRequest;
import com.huawei.sharedrive.uam.enterprise.domain.FileCopySecurityResponse;
import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.FileCopySecurityManager;
import com.huawei.sharedrive.uam.enterprise.manager.SecurityRoleManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.util.Constants;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/enterprise/fileCopy")
public class FileCopySecurityController extends AbstractCommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FileCopySecurityController.class);
    
    @Autowired
    private FileCopySecurityManager fileCopySecurityManager;
    
    @Autowired
    private SecurityRoleManager securityRoleManager;
    
    @Autowired
    private AdminLogManager adminLogManager;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    private static final long DEFAULT_ROLE_ID = -1;
    
    @RequestMapping(value = "{appId}", method = RequestMethod.POST)
    public ResponseEntity<?> createSecurity(@PathVariable(value = "appId") String appId,
        FileCopyCreateRequest createRequest, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        long accountId = getAccoutId(appId);
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(request));
        owner.setAppId(appId);
        try
        {
            String[] description = getDescription(appId,
                accountId,
                createRequest.getSrcSafeRoleId(),
                createRequest.getTargetSafeRoleId(),
                locale);
            
            fileCopySecurityManager.addFileCopySecurity(createRequest.getSrcSafeRoleId(),
                createRequest.getTargetSafeRoleId(),
                accountId);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCESSCONFIG_FILECOPY_ADD, description);
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("creat file copy strategy fail");
            adminLogManager.saveAdminLog(owner,
                AdminLogType.KEY_ACCESSCONFIG_FILECOPY_ADD_ERROR,
                new String[]{getEnterpriseName(), appId});
            // throw e;
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
    
    private String[] getDescription(String appId, long accountId, long srcSafeRoleId, long targetSafeRoleId,
        Locale locale)
    {
        String srcSafeRoleName = "";
        String targetSafeRoleName = "";
        List<SecurityRole> securityRoleList = getSafeRoleList(accountId);
        if (srcSafeRoleId == DEFAULT_ROLE_ID)
        {
            srcSafeRoleName = getText("fileCopy.security.any", null, locale);
        }
        if (targetSafeRoleId == DEFAULT_ROLE_ID)
        {
            targetSafeRoleName = getText("fileCopy.security.any", null, locale);
        }
        for (SecurityRole sr : securityRoleList)
        {
            if (StringUtils.isBlank(srcSafeRoleName))
            {
                if (sr.getId().longValue() == srcSafeRoleId)
                {
                    srcSafeRoleName = sr.getRoleName();
                }
            }
            if (StringUtils.isBlank(targetSafeRoleName))
            {
                if (sr.getId().longValue() == targetSafeRoleId)
                {
                    targetSafeRoleName = sr.getRoleName();
                }
            }
        }
        String[] values = new String[]{getEnterpriseName(), appId, srcSafeRoleName, targetSafeRoleName};
        return values;
    }
    
    @RequestMapping(value = "{appId}", method = RequestMethod.GET)
    public String createSecurity(@PathVariable(value = "appId") String appId, Model model)
    {
        long accountId = getAccoutId(appId);
        model.addAttribute("safeRoleList", getSafeRoleList(accountId));
        model.addAttribute("targetSafeList", getSafeRoleList(accountId));
        model.addAttribute("appId", appId);
        return "enterprise/filecopy/createFileCopyConfig";
    }
    
    @RequestMapping(value = "delConfig/{appId}", method = RequestMethod.POST)
    public ResponseEntity<?> delConfig(@PathVariable(value = "appId") String appId,
        FileCopyDelRequest fileCopyDelRequest, HttpServletRequest request, String token)
    {
        super.checkToken(token);
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        long accountId = getAccoutId(appId);
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(request));
        owner.setAppId(appId);
        try
        {
            String[] description = getDescription(appId,
                accountId,
                fileCopyDelRequest.getSrcSafeRoleId(),
                fileCopyDelRequest.getTargetSafeRoleId(),
                locale);
            
            int result = fileCopySecurityManager.delete(fileCopyDelRequest, accountId);
            if (result == 0)
            {
                adminLogManager.saveAdminLog(owner,
                    AdminLogType.KEY_ACCESSCONFIG_FILECOPY_DELETE_ERROR,
                    description);
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCESSCONFIG_FILECOPY_DELETE, description);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("delete file copy config error");
            adminLogManager.saveAdminLog(owner,
                AdminLogType.KEY_ACCESSCONFIG_FILECOPY_DELETE_ERROR,
                new String[]{getEnterpriseName(), appId});
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "list/{appId}", method = RequestMethod.POST)
    public ResponseEntity<?> list(@PathVariable(value = "appId") String appId,
        @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
        HttpServletRequest servletRequest, String token)
    {
        super.checkToken(token);
        Locale locale = RequestContextUtils.getLocaleResolver(servletRequest).resolveLocale(servletRequest);
        PageRequest request = new PageRequest(pageNumber, Constants.DEFAULT_PAGE_SIZE);
        Long accountId = getAccoutId(appId);
        Page<FileCopySecurityResponse> page = fileCopySecurityManager.queryForList(request,
            null,
            null,
            accountId,
            locale);
        return new ResponseEntity<Page<FileCopySecurityResponse>>(page, HttpStatus.OK);
    }
    
    @RequestMapping(value = "listConfig/{appId}", method = RequestMethod.GET)
    public String listConfig(@PathVariable(value = "appId") String appId, Model model)
    {
        enterpriseAccountManager.bindAppCheck(appId);
        model.addAttribute("appId", appId);
        return "enterprise/filecopy/fileCopyList";
    }
    
    private List<SecurityRole> getSafeRoleList(long accountId)
    {
        SecurityRole sSecurityRole = new SecurityRole();
        sSecurityRole.setAccountId(accountId);
        List<SecurityRole> list = securityRoleManager.getFilterdList(sSecurityRole);
        return list;
    }
    
    private String getText(String code, Object[] params, Locale locale)
    {
        return messageSource.getMessage(code, params, locale);
    }
}
