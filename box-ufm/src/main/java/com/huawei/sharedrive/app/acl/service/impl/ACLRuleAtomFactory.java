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
 * 就近原则，权限项受最近一个节点权限项控制
 * 
 * @author c00110381
 */

public class ACLRuleAtomFactory extends ACLRuleBasicFactory
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ACLRuleAtomFactory.class);
    
    /**
     * 按照就近原则，找到合适的节点权限
     * 
     * @param lstAllACL
     * @param inode
     * @return
     * @throws BaseRunException
     */
    private List<INodeACL> findShortACL(List<INodeACL> lstAllACL, INode inode) throws BaseRunException
    {
        if (lstAllACL.isEmpty())
        {
            return null;
        }
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
        boolean isEmpty = tmpACLLst.isEmpty();
        while (isEmpty)
        {
            // 如果是父节点为根节点，则直接返回
            if (tmpNode.getParentId() == INode.FILES_ROOT)
            {
                break;
            }
            // 递归查询父节点
            tmpNode = filesBaseService.getINodeInfoCheckStatus(tmpNode.getOwnedBy(),
                tmpNode.getParentId(),
                INode.STATUS_NORMAL);
            if (null == tmpNode)
            {
                break;
            }
            tmpACLLst.addAll(findINodeACLForSameUser(lstAllACL, tmpNode));
            isEmpty = tmpACLLst.isEmpty();
        }
        
        return tmpACLLst;
    }

    @Override
    public ACL getACL(INode inode, long userID, String userType, String enterpriseId, UserToken userToken) {
        List<INodeACL> allACL = getUserAllINodeACLOfCloudUserID(inode, userID, userType, enterpriseId, userToken);

        try {
            List<INodeACL> shortAcl = findShortACL(allACL, inode);
            return getACL(shortAcl, userID, userType);
        } catch (BaseRunException e) {
            LOGGER.warn(e.getMessage(), e);
        }

        return null;

    }
}
