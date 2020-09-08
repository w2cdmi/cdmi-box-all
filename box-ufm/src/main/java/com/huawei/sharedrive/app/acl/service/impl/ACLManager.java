package com.huawei.sharedrive.app.acl.service.impl;

import com.huawei.sharedrive.app.acl.domain.ACL;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.user.service.impl.UserServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ACL权限控制算法类，实现权限算法
 * 
 * @author c00110381
 * 
 */
@Component
public class ACLManager
{
    private static ACLManager aclManagerNew = new ACLManager();
    
    private static Logger logger = LoggerFactory.getLogger(ACLManager.class);
    
    @Autowired
    private FileBaseService fileBaseService;
    
    public static ACLManager getInstance()
    {
        return aclManagerNew;
    }
    
    private ACLRuleBasicFactory aCLRuleFactory = null;
    
    @Autowired
    private SystemRoleUtil systemRoleUtil;
    
	@Autowired
	private UserServiceImpl userServiceImpl;
    
    
    public void init()
    {
        if(null == aCLRuleFactory)
        {
            aCLRuleFactory = new ACLRuleCombinationFactory();
        }
    }
    
    /**
     * 权限控制函数
     * 
     * @param userID
     * @param userType
     * @param inode
     * @param userToken 
     * @return
     */
    public ACL getACLForAccessUser(long userID, String userType, INode inode,String enterpriseId, UserToken userToken)
    {
        ACL result = null;
        // 拥有者的权限
        if (INodeACL.TYPE_USER.equals(userType) && userID == inode.getOwnedBy())
        {
            ResourceRole role = systemRoleUtil.getRole(ResourceRole.AUTHER);
            
            ACL acl = new ACL(role);
            acl.getPriviledge(role);
            result = acl.getOperPermissible();
            return result;
        }
        if (INodeACL.TYPE_USER.equals(userType) && userID == inode.getCreatedBy())
        {
            ResourceRole role = systemRoleUtil.getRole(ResourceRole.AUTHER);
            
            ACL acl = new ACL(role);
            acl.getPriviledge(role);
            result = acl.getOperPermissible();
            return result;
        }
        init();
        // 其他权限控制
        result = aCLRuleFactory.getACL(inode, userID, userType, enterpriseId, userToken);
        
        // 就算无对应权限设置，也应该返回空权限
        if (result == null)
        {
            ResourceRole role = null;
            role = systemRoleUtil.getRole(ResourceRole.PROHIBIT_VISITORS);
            
            ACL acl = new ACL(role);
            acl.getPriviledge(role);
            result = acl.getOperPermissible();
        }
        
        return result;
    }
    
    public ACL getACLForLink(String linkCode, INode inode)
    {
        ACL result = null;
        init();
        result = aCLRuleFactory.getACLForLink(inode, linkCode);
        if (result == null)
        {
            if (isLinkCodeMatch(inode, linkCode))
            {
                ResourceRole role = systemRoleUtil.getRole(ResourceRole.VIEWER);
                ACL acl = new ACL(role);
                acl.getPriviledge(role);
                result = acl.getOperPermissible();
            }
            // 就算无对应权限设置，也应该返回空权限
            else
            {
                ResourceRole role = systemRoleUtil.getRole(ResourceRole.PROHIBIT_VISITORS);
                ACL acl = new ACL(role);
                acl.getPriviledge(role);
                result = acl.getOperPermissible();
            }
        }
        return result;
    }
    
    private boolean isLinkCodeMatch(INode inode, String linkCode)
    {
        if (null == inode)
        {
            return false;
        }
        INode parentNode = null;
        if (StringUtils.equals(inode.getLinkCode(), linkCode))
        {
            return true;
        }
        while (inode.getId() != INode.FILES_ROOT)
        {
            if (inode.getParentId() <= 0)
            {
                return false;
            }
            try
            {
                parentNode = fileBaseService.getINodeInfo(inode.getOwnedBy(), inode.getParentId());
            }
            catch (RuntimeException e)
            {
                logger.warn("get parentNode fail:" + e.getMessage());
                return false;
            }
            if (isLinkCodeMatch(parentNode, linkCode))
            {
                return true;
            }
        }
        return false;
    }
    
//    private void setACLRuleFactoryInstance(int type)
//    {
//        if (1 == type)
//        {
//            aCLRuleFactory = new ACLRuleAtomFactory();
//        }
//        else
//        {
//            aCLRuleFactory = new ACLRuleCombinationFactory();
//        }
//    }
}
