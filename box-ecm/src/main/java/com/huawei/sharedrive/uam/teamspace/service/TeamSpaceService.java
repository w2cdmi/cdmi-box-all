package com.huawei.sharedrive.uam.teamspace.service;

import java.util.List;

import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.teamspace.domain.*;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface TeamSpaceService
{
    Page<RestTeamSpaceInfo> getPagedTeamSpace(ListAllTeamSpaceRequest listRequest, String appId, PageRequest pageRequest);

    RestTeamSpaceInfo createTeamSpace(String appId, RestTeamSpaceCreateRequest request);

    /**
     *创建团队空间，系统自动创建时使用
     */
    RestTeamSpaceInfo createTeamSpace(Long enterpriseId, String appId, RestTeamSpaceCreateRequest request);

    RestTeamSpaceInfo modifyTeamSpace(Long teamId, String appId, RestTeamSpaceModifyRequest spaceModifyRequest);

    /**
     *修改团队空间，系统后台修改时使用
     */
    RestTeamSpaceInfo modifyTeamSpace(Long enterpriseId, Long teamId, String appId, RestTeamSpaceModifyRequest spaceModifyRequest);

    String modifyTeamSpaces(String teamIds, String appId, RestTeamSpaceModifyRequest spaceModifyRequest, String keyword);

    void deleteTeamSpace(Long enterpriseId, String appId, Long teamId);

    RestTeamSpaceInfo getTeamSpaceInfo(Long teamId, String appId);
    
    RestTeamSpaceInfo changeOwner(Long teamId, String appId, ChangeOwnerRequest request);
    
    String[] getAllTeamSpaceIds(String appId, String keyword);
    
    List<RestNodeRoleInfo> getSystemRoles(String appId);
    
    List<RestNodeRoleInfo> getSystemRoles(long accountId);

    RestTeamMemberInfo addTeamSpaceMember(Long enterpriseId, String appId, Long teamId, RestTeamMemberCreateRequest request);

//    void deleteTeamSpaceMember(Long enterpriseId, String appId, Long teamId, Long memberId);

    /*查询某个用户所属的团队空间*/
    List<RestTeamMemberInfo> listUserTeamSpaces(Long enterpriseId, String appId, Long memberId, Integer spaceType);

	void deleteTeamSpaceMemberByCloudUserId(Long enterpriseId, String appId, Long teamId, Long cloudUserId);

	RestTeamSpaceInfo changeOwner(Long teamId, UserToken userToken,ChangeOwnerRequest request);
}
