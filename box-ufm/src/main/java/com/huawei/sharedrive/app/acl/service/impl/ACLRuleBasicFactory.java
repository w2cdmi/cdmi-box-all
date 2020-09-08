package com.huawei.sharedrive.app.acl.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.SpringContextUtil;

import com.huawei.sharedrive.app.acl.dao.INodeACLDAO;
import com.huawei.sharedrive.app.acl.domain.ACL;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;
import com.huawei.sharedrive.app.authapp.service.AuthAppService;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.share.service.LinkService;
import com.huawei.sharedrive.app.teamspace.dao.TeamSpaceDAO;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceMembershipService;
import com.huawei.sharedrive.app.user.domain.GroupInfo;
import com.huawei.sharedrive.app.user.service.DepartmentService;
import com.huawei.sharedrive.app.user.service.GroupMemberService;
import com.huawei.sharedrive.app.utils.BusinessConstants;

/**
 * 权限控制工厂抽象类
 * 
 * @author c00110381
 * 
 */

public class ACLRuleBasicFactory
{
    protected FileBaseService filesBaseService = (FileBaseService) SpringContextUtil.getBean("fileBaseService");
    
    private INodeACLDAO iNodeACLDAO = (INodeACLDAO) SpringContextUtil.getBean("iNodeACLDAO");
    
    private SystemRoleUtil systemRoleUtil = (SystemRoleUtil) SpringContextUtil.getBean("systemRoleUtil");
    
    private TeamSpaceDAO teamSpaceDAO = (TeamSpaceDAO) SpringContextUtil.getBean("teamSpaceDAO");
    
    private TeamSpaceMembershipService teamSpaceMembershipService = (TeamSpaceMembershipService) SpringContextUtil.getBean("teamSpaceMembershipService");
    
    private GroupMemberService groupMemberService = (GroupMemberService) SpringContextUtil.getBean("groupMemberService");

    private DepartmentService departmentService = (DepartmentService) SpringContextUtil.getBean("departmentService");
    
    private LinkService linkService = (LinkService) SpringContextUtil.getBean("linkService");
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ACLRuleBasicFactory.class);

    /**
     * 获取用户相关的操作权限
     * 
     * @param inode
     * @param userToken 
     * @return
     */
    public ACL getACL(INode inode, long userID, String userType,String enterpriseIduamClientService, UserToken userToken)
    {
        return null;
    }
    
    public ACL getACLForLink(INode inode, String linkCode)
    {
        List<INodeACL> lstACL = iNodeACLDAO.getAllByUser(inode.getOwnedBy(), linkCode, INodeACL.TYPE_LINK);
        
        INodeACL tmpACL = findShortACLForLink(lstACL, inode, linkCode);
        
        if (tmpACL != null)
        {
            return new ACL(systemRoleUtil.getRole(tmpACL.getResourceRole()));
        }
        return null;
    }
    
    /**
     * 按照就近原则，找到合适的节点权限
     * 
     * @param lstAllACL
     * @param inode
     * @return
     * @throws BaseRunException
     */
    private INodeACL findShortACLForLink(List<INodeACL> lstAllACL, INode inode, String linkCode)
        throws BaseRunException
    {
        if (lstAllACL.isEmpty())
        {
            return null;
        }
        
        INode tmpNode = INode.valueOf(inode);
        
        // 首先找本节点
        INodeACL tmpACL = findINodeACLForSameLink(lstAllACL, tmpNode, linkCode);
        
        while (tmpACL == null)
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
            
            tmpACL = findINodeACLForSameLink(lstAllACL, tmpNode, linkCode);
        }
        
        return tmpACL;
    }
    
    /**
     * 每一个权限项，0: 默认拒绝，1：允许(allow），2：拒绝（deny）,权限合并按“0 默认拒绝<1 允许<2 拒绝”处理 如果存在group和USER则合并
     * 
     * @param effectiveAcl
     * @param userID
     * @param userType
     * @return
     */
    public ACL getACL(List<INodeACL> effectiveAcl, long userID, String userType)
    {
        if (CollectionUtils.isEmpty(effectiveAcl))
        {
            return null;
        }
        ACL initAcl = new ACL(systemRoleUtil.getRole(ResourceRole.PROHIBIT_VISITORS));
        ACL tmpACL = null;
        List<INodeACL> secretINodeACLs=new ArrayList<>();
        for (int i=effectiveAcl.size()-1;i>=0;i--)
        {
        	if(effectiveAcl.get(i).getUserType().equals(INodeACL.TYPE_SECRET)){
        		secretINodeACLs.add(effectiveAcl.get(i));
        		effectiveAcl.remove(i);
        	} 
        }
        //The node re-author,remove the member`s authorities.
        List<INodeACL> newINodeACLs=new ArrayList<>();
        if(secretINodeACLs.size()!=0){
            for (int i=secretINodeACLs.size()-1;i>=0;i--){
                boolean haseAcl=false;
                for(int j=0;j<effectiveAcl.size();j++){
                  if(secretINodeACLs.get(i).getiNodeId()==effectiveAcl.get(j).getiNodeId()&&
                          secretINodeACLs.get(i).getOwnedBy()== effectiveAcl.get(j).getOwnedBy()){
                      haseAcl=true;
                      newINodeACLs.add(effectiveAcl.get(j));
                  }
                }
                if(!haseAcl){
                    return null;
                }
	        }
	        if(newINodeACLs.size()!=0){
	        	effectiveAcl= newINodeACLs;
	        }
        }
        for (INodeACL acl : effectiveAcl)
        {
        	 
            tmpACL = new ACL(systemRoleUtil.getRole(acl.getResourceRole()));
            
            if (tmpACL.getListValue() > initAcl.getListValue())
            {
                initAcl.setListValue(tmpACL.getListValue());
            }
            if (tmpACL.getAuthorValue() > initAcl.getAuthorValue())
            {
                initAcl.setAuthorValue(tmpACL.getAuthorValue());
            }
            if (tmpACL.getDeleteValue() > initAcl.getDeleteValue())
            {
                initAcl.setDeleteValue(tmpACL.getDeleteValue());
            }
            if (tmpACL.getDownloadValue() > initAcl.getDownloadValue())
            {
                initAcl.setDownloadValue(tmpACL.getDownloadValue());
            }
            if (tmpACL.getEditValue() > initAcl.getEditValue())
            {
                initAcl.setEditValue(tmpACL.getEditValue());
            }
            if (tmpACL.getPublishLinkValue() > initAcl.getPublishLinkValue())
            {
                initAcl.setPublishLinkValue(tmpACL.getPublishLinkValue());
            }
            if (tmpACL.getPreviewValue() > initAcl.getPreviewValue())
            {
                initAcl.setPreviewValue(tmpACL.getPreviewValue());
            }
            if (tmpACL.getUploadValue() > initAcl.getUploadValue())
            {
                initAcl.setUploadValue(tmpACL.getUploadValue());
            }
        }
        
        return initAcl;
        
    }
    
    /**
     * 对同一用户找到该节点的权限
     * 
     * @param lstINodeACL
     * @param inode
     * @return
     */
    protected List<INodeACL> findINodeACLForSameUser(List<INodeACL> lstINodeACL, INode inode)
    {
        if (null == lstINodeACL || null == inode)
        {
            return null;
        }
        
        List<INodeACL> nodeACLLst = new ArrayList<INodeACL>(BusinessConstants.INITIAL_CAPACITIES);
        for (INodeACL acl : lstINodeACL)
        {
            // 需要节点ID，节点父ID，拥有者三者一致权限才有效
            if (inode.getId()!=null && acl.getiNodeId() == inode.getId().longValue() && acl.getOwnedBy() == inode.getOwnedBy())
            {
                nodeACLLst.add(acl);
            }
        }
        
        return nodeACLLst;
    }
    
    protected INodeACL findINodeACLForSameLink(List<INodeACL> lstINodeACL, INode inode, String linkCode)
    {        if (null == lstINodeACL || null == inode)
        {
            return null;
        }
        
        for (INodeACL acl : lstINodeACL)
        {
            // 需要节点ID，节点父ID，拥有者三者一致权限才有效
        	INodeLink iNodeLink = linkService.getLinkByLinkCode(linkCode);
        	if(iNodeLink.getiNodeId()==-1&&!iNodeLink.getSubINodes().equals("")){
        		List<INode> subfiles = (List<INode>) JsonUtils.stringToList(iNodeLink.getSubINodes(), List.class,INode.class);
        		for(INode subiNode : subfiles){
        			if(subiNode.getId().longValue()==inode.getId().longValue() && inode.getOwnedBy()==subiNode.getOwnedBy()){
        				if (inode.getId()!=null && acl.getiNodeId() == inode.getId().longValue() && acl.getOwnedBy() == subiNode.getOwnedBy()
                                && StringUtils.equals(acl.getAccessUserId(), linkCode)) {
                                return acl;
                         }
        			}
        		}
        	}else{
        		if (inode.getId()!=null && acl.getiNodeId() == inode.getId().longValue() && acl.getOwnedBy() == inode.getOwnedBy()
                        && StringUtils.equals(acl.getAccessUserId(), linkCode)) {
                        return acl;
                 }
        	}
            
        }
        
        return null;
    }
    
    protected List<INodeACL> getUserAllINodeACLOfCloudUserID(INode inode, long userID, String userType, String enterpriseId, UserToken userToken)
    {
        // 获取自己的
        List<INodeACL> lstACL = iNodeACLDAO.getAllByUser(inode.getOwnedBy(),
            String.valueOf(userID),
            INodeACL.TYPE_USER);
        
        // 获取userID对应的群组集合
        List<GroupInfo> groupInfoes = groupMemberService.getUserGroupList(userID);
        List<INodeACL> lstGroupACL = null;
        for (GroupInfo group : groupInfoes)
        {
            lstGroupACL = iNodeACLDAO.getAllByUser(inode.getOwnedBy(),
                String.valueOf(group.getId()),
                INodeACL.TYPE_GROUP);
            lstACL.addAll(lstGroupACL);
        }
        
        // 获取userID对应的群组集合
        if(StringUtils.isEmpty(enterpriseId)){
        	enterpriseId = "0";
        }
        List<Long> deptList = departmentService.getDeptCloudUserIdByCloudUserId(Long.parseLong(enterpriseId), userToken.getId(), userToken.getAccountId());;
        List<INodeACL> deptacl = null;
        for (Long deptId : deptList)
        {
        	deptacl = iNodeACLDAO.getAllByUser(inode.getOwnedBy(),
                String.valueOf(deptId),
                INodeACL.TYPE_DEPT);
            lstACL.addAll(deptacl);
        }
        
        
        // 获取团队公开的
        TeamSpace team = teamSpaceDAO.get(inode.getOwnedBy());
        if (team != null)
        {
            List<INodeACL> lstTeamACL = iNodeACLDAO.getAllByUser(inode.getOwnedBy(),
              enterpriseId,
                INodeACL.TYPE_TEAM);
            
            if (lstTeamACL != null)
            {
                TeamSpaceMemberships memberShip = teamSpaceMembershipService.getUserMemberShips(inode.getOwnedBy(),
                    userID, enterpriseId);
                if (memberShip != null)
                {
                    lstACL.addAll(lstTeamACL);
                }
            }
        }
        // 获取system
        List<INodeACL> lstSystemACL = iNodeACLDAO.getAllByUser(inode.getOwnedBy(),
            enterpriseId,
            INodeACL.TYPE_SYSTEM);
        lstACL.addAll(lstSystemACL);
        
        // 获取public
        List<INodeACL> lstPublicACL = iNodeACLDAO.getAllByUser(inode.getOwnedBy(),
            enterpriseId,
            INodeACL.TYPE_PUBLIC);
        lstACL.addAll(lstPublicACL);
        
        
        for(INodeACL iNodeACL:lstACL){
        	if(iNodeACL.getResourceRole().equals("auther")){
        		 return lstACL;
        	}
        }
        List<INodeACL> secretacl = iNodeACLDAO.getAllByUser(inode.getOwnedBy(),
                INodeACL.ID_SECRET,
                INodeACL.TYPE_SECRET);
        lstACL.addAll(secretacl);
       
        // 获取数据迁移授权信息
        List<INodeACL> migrationACL = iNodeACLDAO.getAllByUser(inode.getOwnedBy(),
            userID + "",
            INodeACL.TYPE_MIGRATION);
        if (null != migrationACL && migrationACL.size() > 0){
        	lstACL.addAll(migrationACL);
        }
       
        return lstACL;
    }

	public ACL getACL(INode inode, long userID, String userType, String enterpriseId, AuthAppService authAppService,
			RestClient uamClientService, UserToken userToken) {
		// TODO Auto-generated method stub
		return null;
	}

    
}
