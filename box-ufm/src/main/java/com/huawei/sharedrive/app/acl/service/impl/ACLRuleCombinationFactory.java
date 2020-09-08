package com.huawei.sharedrive.app.acl.service.impl;

import com.huawei.sharedrive.app.acl.domain.ACL;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 合并原则，权限项受整个PATH路径控制
 * 
 * @author c00110381
 * 
 */
public class ACLRuleCombinationFactory extends ACLRuleBasicFactory
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ACLRuleCombinationFactory.class);
    
    /**
     * 找到该节点路径上所有权限项
     * 
     * @param lstAllACL
     * @param inode
     * @return
     * @throws BaseRunException
     */
    private List<INodeACL> findShortACL(List<INodeACL> lstAllACL, INode inode) throws BaseRunException
    {
        // 查看是否只有一个权限项，如果只有一个权限项，判断和该节点的关系
        if (1 == lstAllACL.size())
        {
            INodeACL acl = lstAllACL.get(0);
            if (acl.getiNodeId() == INode.FILES_ROOT)
            {
                return lstAllACL;
            }
        }
        
        INode tmpNode = INode.valueOf(inode);
        
        // 首先找本节点
        List<INodeACL> tmpACLLst = findINodeACLForSameUser(lstAllACL, tmpNode);
        
        while (true)
        {
            // 如果是父节点为根节点，则直接返回
            if (tmpNode.getParentId() == INode.FILES_ROOT)
            {
                // 检测跟目录
                tmpNode.setId(INode.FILES_ROOT);
                tmpNode.setParentId(INode.FILES_ROOT);
                tmpACLLst.addAll(findINodeACLForSameUser(lstAllACL, tmpNode));
                break;
            }
            
            // 递归
            tmpNode = filesBaseService.getParentINodeInfoCheckStatus(tmpNode.getOwnedBy(),
                tmpNode.getParentId(),
                INode.STATUS_NORMAL);
            
            if (null == tmpNode)
            {
                break;
            }
            
            tmpACLLst.addAll(findINodeACLForSameUser(lstAllACL, tmpNode));
            
        }
        
        return tmpACLLst;
    }

    @Override
    public ACL getACL(INode inode, long userID, String userType, String enterpriseId, UserToken userToken) {
        List<INodeACL> lstAllACL = getUserAllINodeACLOfCloudUserID(inode, userID, userType, enterpriseId, userToken);

        try {
            List<INodeACL> shortAcl = findShortACL(lstAllACL, inode);
            return getACL(shortAcl, userID, userType);
        } catch (BaseRunException e) {
            LOGGER.warn(e.getMessage(), e);
        }

        return null;
    }
}
