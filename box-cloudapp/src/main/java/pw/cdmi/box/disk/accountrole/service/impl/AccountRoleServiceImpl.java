package pw.cdmi.box.disk.accountrole.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.accountrole.dao.AccountRoleDao;
import pw.cdmi.box.disk.accountrole.domain.AccountRole;
import pw.cdmi.box.disk.accountrole.domain.PageNodeRoleInfo;
import pw.cdmi.box.disk.accountrole.service.AccountRoleService;
import pw.cdmi.box.disk.teamspace.domain.RestACL;
import pw.cdmi.box.disk.teamspace.domain.RestNodeRoleInfo;
import pw.cdmi.box.disk.teamspace.service.NodeRoleService;

@Service
public class AccountRoleServiceImpl implements AccountRoleService
{
    @Autowired
    private AccountRoleDao accountRoleDao;
    
    @Autowired
    private NodeRoleService nodeRoleService;
    
    @Override
    public void create(long accountId, String roleId)
    {
        AccountRole accountRole = new AccountRole();
        accountRole.setResourceRole(roleId);
        accountRole.setAccountId(accountId);
        accountRoleDao.create(accountRole);
    }
    
    @Override
    public void delete(long accountId, String roleId)
    {
        accountRoleDao.delete(accountId, roleId);
    }
    
    @Override
    public List<AccountRole> getList(long accountId)
    {
        return accountRoleDao.getList(accountId);
    }
    
    private List<PageNodeRoleInfo> transNodeRoles(List<RestNodeRoleInfo> roleList)
    {
        List<PageNodeRoleInfo> pageList = new ArrayList<PageNodeRoleInfo>(roleList.size());
        PageNodeRoleInfo info;
        RestACL acl;
        for (RestNodeRoleInfo item : roleList)
        {
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
    
    @Override
    public List<PageNodeRoleInfo> listAccountRoles(long accountId)
    {
        List<RestNodeRoleInfo> roleList = nodeRoleService.getNodeRoles();
        if (roleList == null)
        {
            return new ArrayList<PageNodeRoleInfo>(0);
        }
        //List<AccountRole> accountRoles = getList(accountId);
        //获取系统默认
        List<AccountRole> accountRoles = getList(0);
        List<RestNodeRoleInfo> accountNodeRoles = new ArrayList<RestNodeRoleInfo>(1);
        
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
        List<PageNodeRoleInfo> pageList = transNodeRoles(accountNodeRoles);
        
        return pageList;
    }
    
}
