package com.huawei.sharedrive.app.teamspace.manager.impl;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.account.service.AccountService;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;
import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ExceedQuotaException;
import com.huawei.sharedrive.app.exception.NoSuchAccountException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FolderServiceV2;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceCreateRequest;
import com.huawei.sharedrive.app.teamspace.domain.TeamRole;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.teamspace.manager.TeamspaceManager;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceMembershipService;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TeamspaceManagerImpl implements TeamspaceManager {
    private static Logger logger = LoggerFactory.getLogger(TeamspaceManagerImpl.class);

    @Autowired
    private TeamSpaceService teamSpaceService;

    @Autowired
    private TeamSpaceMembershipService teamSpaceMembershipService;

    @Autowired
    private AccountService accountService;
    
    @Autowired
	private INodeACLService iNodeACLService;
    
    @Autowired
    private FolderServiceV2 folderServiceV2;
    
    @Override
    public TeamSpace deleteTeamspaceById(long teamspaceId, UserToken user) throws BaseRunException
    {
        TeamSpace teamSpace = teamSpaceService.getTeamSpaceInfo(user, teamspaceId);
        Account account = accountService.getById(teamSpace.getAccountId());
        if (account == null)
        {
            throw new NoSuchAccountException("cant not found account  " + teamSpace.getAccountId());
        }
        teamSpaceService.deleteTeamSpace(user, teamspaceId,String.valueOf(account.getEnterpriseId()));
        accountService.updateCurrentTeamspace(account);
        folderServiceV2.deleteRecentByOwner(teamspaceId);
        folderServiceV2.deleteShortByOwner(teamspaceId);
        return teamSpace;
    }
    
    @Override
    public TeamSpace createTeamspace(RestTeamSpaceCreateRequest createRequest, UserToken userInfo)
    {
        Account account = accountService.getById(userInfo.getAccountId());
        if (account == null)
        {
            throw new NoSuchAccountException("cant not found account  " + userInfo.getAccountId());
        }
        if (accountService.isTeamspaceExceed(account))
        {
            throw new ExceedQuotaException("teamspace exceed in account: " + account.getId());
        }
        TeamSpace teamSpace = teamSpaceService.initTeamspace(createRequest, userInfo);
        TeamSpace ts = teamSpaceService.createTeamSpace(userInfo, teamSpace);

        //创建的是部门空间，自动将部门作为成员加入。
        if(createRequest.getType() == TeamSpace.TYPE_OFFICIAL ) {
            TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
            teamSpaceMemberships.setCloudUserId(teamSpace.getCloudUserId());
            teamSpaceMemberships.setCreatedAt(new Date());
            teamSpaceMemberships.setCreatedBy(-2L);
            teamSpaceMemberships.setStatus(TeamSpaceMemberships.STATUS_NORMAL);
            teamSpaceMemberships.setTeamRole(TeamRole.ROLE_MEMBER);
            //部门空间的cloudUserId同样对应于部门的cloudUserId, 将其作为成员加入空间成员中
            teamSpaceMemberships.setUserId(teamSpace.getCloudUserId());
            teamSpaceMemberships.setUserType(TeamSpaceMemberships.TYPE_DEPT);
            teamSpaceMemberships.setUsername(createRequest.getName());
            teamSpaceMemberships.setLoginName(createRequest.getName());
            teamSpaceMembershipService.createTeamSpaceMember(teamSpaceMemberships);
            
    		// 创建ACL
        	INodeACL iNodeACL = new INodeACL();
        	//部门空间的cloudUserId同样对应于部门的cloudUserId, 将其加入到ACL控制表中
        	iNodeACL.setAccessUserId(String.valueOf(teamSpace.getCloudUserId()));
			iNodeACL.setUserType(INodeACL.TYPE_DEPT);
			iNodeACL.setCreatedAt(new Date());
			iNodeACL.setCreatedBy(-2L);
			iNodeACL.setiNodeId(INode.FILES_ROOT);
			iNodeACL.setiNodePid(INode.FILES_ROOT);
			iNodeACL.setOwnedBy(teamSpace.getCloudUserId());
			iNodeACL.setModifiedAt(new Date());
			iNodeACL.setModifiedBy(-2L);
			iNodeACL.setResourceRole(ResourceRole.UPLOAD_VIEWER);
			iNodeACLService.addINodeACL(iNodeACL);
        }

        accountService.updateCurrentTeamspace(account);
        return ts;
    }
    
}
