package com.huawei.sharedrive.app.acl.service.impl;

import com.huawei.sharedrive.app.acl.dao.INodeACLDAO;
import com.huawei.sharedrive.app.acl.dao.ResourceRoleDAO;
import com.huawei.sharedrive.app.acl.domain.ACL;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.domain.INodeACLList;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;
import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.group.domain.GroupMemberships;
import com.huawei.sharedrive.app.group.service.GroupMembershipsService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize;
import com.huawei.sharedrive.app.teamspace.dao.TeamSpaceDAO;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceMembershipService;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.DepartmentService;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

import java.util.ArrayList;
import java.util.List;



@Component
public class INodeACLServiceImpl implements INodeACLService
{
    private static Logger logger = LoggerFactory.getLogger(INodeACLServiceImpl.class);

    @Autowired
    private ACLManager aclManager;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private GroupMembershipsService groupMembershipsService;
    
    @Autowired
    private INodeACLDAO iNodeACLDAO;
    
    @Autowired
    private ResourceRoleDAO resourceRoleDAO;
    
    @Autowired
    private TeamSpaceDAO teamSpaceDAO;
    
    @Autowired
    private TeamSpaceMembershipService teamSpaceMembershipService;
    
	@Autowired
    private DepartmentService departmentService;

    @Override
    public INodeACL addINodeACL(INodeACL iNodeACL) throws BaseRunException
    {
        INodeACL existACL = getByResourceAndUser(iNodeACL.getOwnedBy(),
            iNodeACL.getiNodeId(),
            iNodeACL.getAccessUserId(),
            iNodeACL.getUserType());
        
        if (existACL != null)
        {
        	iNodeACLDAO.delete(existACL.getOwnedBy(), existACL.getId());
        }
        
        iNodeACLDAO.create(iNodeACL);
        
        return iNodeACL;
    }
    
    @Override
    public INodeACL addINodeIsVisibleACL(INodeACL iNodeACL) throws BaseRunException
    {
       
        
    	 iNodeACLDAO.create(iNodeACL);
        
        return iNodeACL;
    }
    
    @Override
    public void deleteByResourceAndUser(long ownedBy, long iNodeId, String userId, String userType)
    {
        iNodeACLDAO.deleteByResourceAndUser(ownedBy, iNodeId, userId, userType);
    }
    
    @Override
    public void deleteINodeACLById(long ownedBy, long id) throws BaseRunException
    {
        INodeACL existACL = iNodeACLDAO.get(ownedBy, id);
        
        if (existACL == null)
        {
            String excepMessage = "acl not exist,ownerid:" + ownedBy + ",id:" + id;
            throw new NoSuchItemsException(excepMessage);
        }
        
        iNodeACLDAO.delete(ownedBy, id);
    }
    
    @Override
    public void deleteINodeAllACLs(long ownedBy, long nodeId) throws BaseRunException
    {
        iNodeACLDAO.deleteByResource(ownedBy, nodeId);
    }
    
    @Override
    public void deleteSpaceACLsByUser(long ownedBy, String userId, String userType) throws BaseRunException
    {
        // acl不存在不用报异常，因为存在删除未设置acl的团队成员
        iNodeACLDAO.deleteByUser(ownedBy, userId, userType);
    }
    
    @Override
    public void deleteSpaceAllACLs(long ownerBy) throws BaseRunException
    {
        iNodeACLDAO.deleteSpaceAll(ownerBy);
    }
    
    @Override
    public ACL getACLForAccessUser(long userId, String userType, INode inode, String enterpriseId,UserToken userToken)
    {
        INode tempNode = inode;
        if (tempNode.getId()!=null && tempNode.getId() != INode.FILES_ROOT)
        {
            tempNode = fileBaseService.getINodeInfo(inode.getOwnedBy(), inode.getId());
            if (tempNode == null)
            {
                return null;
            }
        }
        
        return aclManager.getACLForAccessUser(userId, userType, tempNode, enterpriseId,userToken);
    }
    
    @Override
    public ACL getACLForLink(String linkCode, INode inode)
    {
        INode tempNode = inode;
        if (tempNode.getId()!=null && tempNode.getId() != INode.FILES_ROOT)
        {
            tempNode = fileBaseService.getINodeInfo(inode.getOwnedBy(), inode.getId());
            if (tempNode == null)
            {
                return null;
            }
        }
        
        return aclManager.getACLForLink(linkCode, tempNode);
    }
    
    @Override
    public INodeACL getByResourceAndUser(long ownedBy, long iNodeId, String userId, String userType)
    {
        return iNodeACLDAO.getByResourceAndUser(ownedBy, iNodeId, userId, userType);
    }
    
    @Override
    public INodeACL getINodeACLById(long ownedBy, long id)
    {
        return iNodeACLDAO.get(ownedBy, id);
    }
    
    @Override
    public long getINodeACLsCount(long ownerBy, long nodeId)
    {
        return iNodeACLDAO.getByResourceCount(ownerBy, nodeId);
    }

    @Override
    public List<INodeACL> getINodeACLSelfAndAnyACLs(long ownedBy, long nodeId, long userId, String enterpriseId, UserToken userToken) {
        List<INodeACL> lstiNodeACL = new ArrayList<>(BusinessConstants.INITIAL_CAPACITIES);
        // 获取个人的
        INodeACL result = iNodeACLDAO.getByResourceAndUser(ownedBy, nodeId, String.valueOf(userId), INodeACL.TYPE_USER);
        if (result != null) {
            lstiNodeACL.add(result);
        }

        // 获取群组
        List<GroupMemberships> groupMembershipses = groupMembershipsService.getUserList(null, null, userId, GroupConstants.GROUP_USERTYPE_USER, null);
        if (CollectionUtils.isNotEmpty(groupMembershipses)) {
            for (GroupMemberships gm : groupMembershipses) {
                result = iNodeACLDAO.getByResourceAndUser(ownedBy, nodeId, String.valueOf(gm.getGroupId()), INodeACL.TYPE_GROUP);
                if (result != null) {
                    lstiNodeACL.add(result);
                }
            }

        }

        //查询部门信息
        List<Long> deptList = departmentService.getDeptCloudUserIdByCloudUserId(Long.parseLong(enterpriseId), userToken.getId(), userToken.getAccountId());
        for (Long deptId : deptList) {
            INodeACL deptAcl = iNodeACLDAO.getByResourceAndUser(ownedBy, nodeId, String.valueOf(deptId), INodeACL.TYPE_DEPT);
            if (deptAcl != null) {
                lstiNodeACL.add(deptAcl);
            }
        }

        // 获取团队空间public的
        result = iNodeACLDAO.getByResourceAndUser(ownedBy, nodeId, INodeACL.ID_PUBLIC, INodeACL.TYPE_TEAM);
        if (result != null) {
            lstiNodeACL.add(result);
        }

        // 获取系统用户的
        TeamSpace team = teamSpaceDAO.get(ownedBy);
        if (team != null) {
            TeamSpaceMemberships memberShip = teamSpaceMembershipService.getUserMemberShips(ownedBy, userId, enterpriseId);
            if (memberShip != null) {
                result = iNodeACLDAO.getByResourceAndUser(ownedBy, nodeId, INodeACL.ID_PUBLIC, INodeACL.TYPE_SYSTEM);

                if (result != null) {
                    lstiNodeACL.add(result);
                }
            }
        }

        // 获取public的
        result = iNodeACLDAO.getByResourceAndUser(ownedBy, nodeId, INodeACL.ID_PUBLIC, INodeACL.TYPE_PUBLIC);
        if (result != null) {
            lstiNodeACL.add(result);
        }
        return lstiNodeACL;
    }
    
    @Override
    public INodeACLList listAllACLs(long ownerId, List<Order> orderList, Limit limit)
    {
        INodeACLList nodeACLList = new INodeACLList();
        
        nodeACLList.setTotalCount(iNodeACLDAO.getAllCountNoLink(ownerId));
        nodeACLList.setLimit(limit.getLength());
        nodeACLList.setOffset(limit.getOffset());
        
        // 分页显示
        List<INodeACL> itemList = iNodeACLDAO.getAllNoLink(ownerId, orderList, limit);
        
        nodeACLList.setNodeACLs(itemList);
        
        return nodeACLList;
    }
    
    @Override
    public INodeACLList listINodeACLs(long ownerId, long nodeId, List<Order> orderList, Limit limit)
    {
        INodeACLList nodeACLList = new INodeACLList();
        
        nodeACLList.setTotalCount(iNodeACLDAO.getByResourceCountNoLink(ownerId, nodeId));
        if (limit != null)
        {
            nodeACLList.setLimit(limit.getLength());
            nodeACLList.setOffset(limit.getOffset());
        }
        
        // 分页显示
        List<INodeACL> itemList = iNodeACLDAO.getByResourceNoLink(ownerId, nodeId, orderList, limit);
        
        nodeACLList.setNodeACLs(itemList);
        
        return nodeACLList;
    }
    
    @Override
    public List<ResourceRole> listResourceRole()
    {
        return resourceRoleDAO.listResourceRole();
    }
    
    @Override
    public List<ResourceRole> listResourceRole(long createdBy)
    {
        return resourceRoleDAO.listResourceRole(createdBy);
    }
    
    @Override
    public INodeACL modifyINodeACLById(INodeACL iNodeACL) throws BaseRunException
    {
        INodeACL existACL = iNodeACLDAO.get(iNodeACL.getOwnedBy(), iNodeACL.getId());
        
        if (existACL == null)
        {
            throw new NoSuchItemsException();
        }
        
        iNodeACLDAO.updateById(iNodeACL);
        return iNodeACL;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.app.files.service.FileBaseService#vaildINodeOperACL(com.huawei
     * .sharedrive.app.oauth2.domain.UserToken,
     * com.huawei.sharedrive.app.files.domain.INode, java.lang.String)
     */
    @Override
    public long vaildINodeOperACL(UserToken user, INode node, String oper) throws BaseRunException
    {
        return vaildINodeOperACL(user, node, oper, false);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.app.files.service.FileBaseService#vaildINodeOperACL(com.huawei
     * .sharedrive.app.oauth2.domain.UserToken,
     * com.huawei.sharedrive.app.files.domain.INode, java.lang.String, java.lang.String,
     * boolean)
     */
    @Override
    public long vaildINodeOperACL(UserToken userToken, INode node, String oper, boolean valiAccessCode)
        throws BaseRunException
    {
    	if (null == userToken) {
            throw new ForbiddenException("User is null");
        }
    	
    	String enterpriseId = "";
		if (userToken.getAccountVistor() != null) {
			enterpriseId = String.valueOf(userToken.getAccountVistor().getEnterpriseId());
		}

        if (node.getOwnedBy() == userToken.getId())
        {
            return node.getOwnedBy();
        }

        // 外链的权限校验
        if (StringUtils.isNotBlank(userToken.getLinkCode()))
        {
            ACL nodeACL = this.getACLForLink(userToken.getLinkCode(), node);
            if (!checkNodeACL(nodeACL, oper))
            {
                logger.warn("checkAcl of link failed: enterpriseId={}, userId={}, nodeId={}", enterpriseId, userToken.getId(), node.getId());
                throw new ForbiddenException();
            }
            return node.getOwnedBy();
        }
        else if (User.ANONYMOUS_USER_ID != userToken.getId())
        {
            // 1,配置文件启动时批量添加共享关系
            // 获取权限操作项
            // try
            // {
            // TODO 暂时：如果是团队空间需要校验团队空间的状态是否异常
            checkIfTeamSpace(node);
            ACL nodeACL = this.getACLForAccessUser(userToken.getId(), INodeACL.TYPE_USER, node,enterpriseId,userToken);
            if (!checkNodeACL(nodeACL, oper))
            {
                logger.warn("checkAcl for user failed: enterpriseId={}, userId={}, nodeId={}", enterpriseId, userToken.getId(), node.getId());
                throw new ForbiddenException();
            }
            return node.getOwnedBy();
        }
        else
        {
            logger.warn("checkAcl for ANONYMOUS user failed: enterpriseId={}, userId={}, nodeId={}", enterpriseId, userToken.getId(), node.getId());
            throw new ForbiddenException();
        }
    }
    
    private void checkIfTeamSpace(INode node) throws ForbiddenException
    {
        TeamSpace teamSpace = teamSpaceDAO.get(node.getOwnedBy());
        
        if (teamSpace != null && teamSpace.getStatus() != TeamSpace.STATUS_ENABLE)
        {
            String msg = "teamSpace is abnormal, teamSpaceId:" + node.getOwnedBy();
            throw new ForbiddenException(msg);
        }
    }
    
    private boolean checkNodeACL(ACL nodeACL, String oper)
    {
        if (nodeACL == null)
        {
            return false;
        }
        // TODO 检视
        // 检查list权限
        Authorize.AuthorityMethod method = Authorize.AuthorityMethod.valueOf(oper);
        if (Authorize.AuthorityMethod.GET_INFO.contain(method) && nodeACL.isList())
        {
            return true;
        }
        
        // 检查upload权限
        if (Authorize.AuthorityMethod.UPLOAD_OBJECT.contain(method) && nodeACL.isUpload())
        {
            return true;
        }
        
        // 检查download权限
        if (Authorize.AuthorityMethod.GET_ALL.contain(method) && nodeACL.isDownload())
        {
            return true;
        }
        
        // 检查preview权限
        if (Authorize.AuthorityMethod.GET_PREVIEW.contain(method) && nodeACL.isPreview())
        {
            return true;
        }
        
        // 检查delete权限
        if (Authorize.AuthorityMethod.DELETE_ALL.contain(method) && nodeACL.isDelete())
        {
            return true;
        }
        
        // 检查edit权限
        if (nodeACL.isEdit())
        {
            return true;
        }
        
        return false;
    }

	@Override
	public INodeACL getNodeIsVisibleACL(INodeACL inodeRole) {
		// TODO Auto-generated method stub
		return iNodeACLDAO.getByResourceAndUser(inodeRole.getOwnedBy(),inodeRole.getiNodeId(), inodeRole.getAccessUserId(), inodeRole.getUserType());
	}
}
