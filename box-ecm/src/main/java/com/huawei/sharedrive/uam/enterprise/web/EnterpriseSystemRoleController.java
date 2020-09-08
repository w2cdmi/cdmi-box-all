package com.huawei.sharedrive.uam.enterprise.web;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.accountrole.domain.AccountRole;
import com.huawei.sharedrive.uam.accountrole.service.AccountRoleService;
import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.teamspace.domain.PageNodeRoleInfo;
import com.huawei.sharedrive.uam.teamspace.domain.RestACL;
import com.huawei.sharedrive.uam.teamspace.domain.RestNodeRoleInfo;
import com.huawei.sharedrive.uam.teamspace.service.TeamSpaceService;

import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/enterprise/admin/systemrole")
public class EnterpriseSystemRoleController extends AbstractCommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseSystemRoleController.class);
    
    @Autowired
    private TeamSpaceService teamSpaceService;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private AccountRoleService accountRoleService;
    
    @Autowired
    private AdminLogManager adminLogManager;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    @RequestMapping(value = "/{appId}", method = RequestMethod.GET)
    public String list(@PathVariable String appId, Model model)
    {
        enterpriseAccountManager.bindAppCheck(appId);
        model.addAttribute("appType", authAppService.getByAuthAppID(appId).getType());
        return "enterprise/admin/app/systemRoleList";
    }
    
    @RequestMapping(value = "/{appId}/system", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<PageNodeRoleInfo>> listSystemRoles(@PathVariable String appId)
    {
        try
        {
            List<RestNodeRoleInfo> roleList = teamSpaceService.getSystemRoles(appId);
            
            List<PageNodeRoleInfo> pageList = transNodeRoles(roleList);
            
            return new ResponseEntity<List<PageNodeRoleInfo>>(pageList, HttpStatus.OK);
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<List<PageNodeRoleInfo>>(HttpStatus.BAD_REQUEST);
        }
    }
    
    private List<PageNodeRoleInfo> transNodeRoles(List<RestNodeRoleInfo> roleList)
    {
        if (roleList == null)
        {
            return new ArrayList<PageNodeRoleInfo>(0);
        }
        List<PageNodeRoleInfo> pageList = new ArrayList<PageNodeRoleInfo>(roleList.size());
        PageNodeRoleInfo info = null;
        RestACL acl = null;
        for (RestNodeRoleInfo item : roleList)
        {
            if ("auther".equalsIgnoreCase(item.getName()) || "lister".equalsIgnoreCase(item.getName())
                || "prohibitVisitors".equalsIgnoreCase(item.getName()))
            {
                continue;
            }
            info = new PageNodeRoleInfo();
            info.setName(HtmlUtils.htmlEscape(item.getName()));
            info.setDescription(HtmlUtils.htmlEscape(item.getDescription()));
            info.setStatus(item.getStatus());
            acl = item.getPermissions();
            info.setAuthorize(acl.getAuthorize());
            info.setBrowse(acl.getBrowse());
            info.setDelete(acl.getDelete());
            info.setDownload(acl.getDownload());
            info.setEdit(acl.getEdit());
            info.setPreview(acl.getPreview());
            info.setPublishLink(acl.getPublishLink());
            info.setUpload(acl.getUpload());
            pageList.add(info);
        }
        return pageList;
    }
    
    @RequestMapping(value = "/{appId}/account", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<PageNodeRoleInfo>> listAccountRoles(@PathVariable String appId)
    {
        try
        {
            List<RestNodeRoleInfo> roleList = teamSpaceService.getSystemRoles(appId);
            if (roleList == null)
            {
                return new ResponseEntity<List<PageNodeRoleInfo>>(new ArrayList<PageNodeRoleInfo>(0),
                    HttpStatus.OK);
            }
            long accountId = getAccoutId(appId);
            List<AccountRole> accountRoles = accountRoleService.getList(accountId);
            
            List<RestNodeRoleInfo> accountNodeRoles = new ArrayList<RestNodeRoleInfo>(1);
            
            addRoles(accountRoles, roleList, accountNodeRoles);
            List<PageNodeRoleInfo> pageList = transNodeRoles(accountNodeRoles);
            
            return new ResponseEntity<List<PageNodeRoleInfo>>(pageList, HttpStatus.OK);
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<List<PageNodeRoleInfo>>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/delete/{appId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteRoles(@PathVariable String appId, String roleId,
        HttpServletRequest request, String token)
    {
        super.checkToken(token);
        String[] description = new String[]{getEnterpriseName(), appId, roleId};
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setAppId(appId);
        owner.setIp(IpUtils.getClientAddress(request));
        try
        {
            long accountId = getAccoutId(appId);
            int deleteResult = accountRoleService.delete(accountId, roleId);
            if (deleteResult < 1)
            {
                LOGGER.error("roleId is empty");
                adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SYATEM_ROLE_DELETE_ERROR, description);
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SYATEM_ROLE_DELETE, description);
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (RuntimeException e)
        {
            LOGGER.error(e.getMessage(), e);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SYATEM_ROLE_DELETE_ERROR, description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/add/{appId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> addRoles(@PathVariable String appId, String roleIds,
        HttpServletRequest request, String token)
    {
        super.checkToken(token);
        String[] description = new String[]{getEnterpriseName(), appId, roleIds};
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setAppId(appId);
        owner.setIp(IpUtils.getClientAddress(request));
        try
        {
            checkParameter(appId, roleIds);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SYATEM_ROLE_SET, description);
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (RuntimeException e)
        {
            LOGGER.error(e.getMessage(), e);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SYATEM_ROLE_SET_ERROR, description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
    
    private void checkParameter(String appId, String roleIds)
    {
        long accountId = getAccoutId(appId);
        List<RestNodeRoleInfo> roleList = teamSpaceService.getSystemRoles(appId);
        if (roleList != null)
        {
            if ("all".equalsIgnoreCase(roleIds))
            {
                for (RestNodeRoleInfo item : roleList)
                {
                    createIgnorError(accountId, item.getName());
                }
            }
            else
            {
                String[] idArray = roleIds.split(",");
                for (String roleId : idArray)
                {
                    if (StringUtils.isNotBlank(roleId))
                    {
                        checkRoleIdValid(roleList, roleId);
                        createIgnorError(accountId, roleId);
                    }
                }
            }
        }
    }
    
    private void checkRoleIdValid(List<RestNodeRoleInfo> roleList, String roleId)
    {
        boolean isFound = false;
        for (RestNodeRoleInfo item : roleList)
        {
            if (StringUtils.equals(item.getName(), roleId))
            {
                isFound = true;
                break;
            }
        }
        if (!isFound)
        {
            throw new InvalidParamterException();
        }
    }
    
    private void createIgnorError(long accountId, String roleName)
    {
        try
        {
            accountRoleService.create(accountId, roleName);
        }
        catch (Exception e)
        {
            LOGGER.warn("create system role faile:" + e.getMessage());
        }
    }
    
    private void addRoles(List<AccountRole> accountRoles, List<RestNodeRoleInfo> roleList,
        List<RestNodeRoleInfo> accountNodeRoles)
    {
        for (AccountRole accountRole : accountRoles)
        {
            for (RestNodeRoleInfo item : roleList)
            {
                if (StringUtils.equals(item.getName(), accountRole.getResourceRole()))
                {
                    accountNodeRoles.add(item);
                }
            }
        }
    }
    
}
