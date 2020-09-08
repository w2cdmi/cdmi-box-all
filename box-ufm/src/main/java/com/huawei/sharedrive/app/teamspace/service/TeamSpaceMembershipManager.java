package com.huawei.sharedrive.app.teamspace.service;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamMemberCreateRequest;
import com.huawei.sharedrive.app.teamspace.domain.TeamMemberList;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

import java.util.List;

public interface TeamSpaceMembershipManager {
    /**
     * 添加团队空间成员
     */
    void createTeamSpaceMember(UserToken user, long teamSpaceId, RestTeamMemberCreateRequest restTeamMemberCreateRequest, boolean isAccountOper) throws BaseRunException;

    //添加团队空间成员
    TeamSpaceMemberships createTeamSpaceMember(UserToken userToken, long teamSpaceId, boolean isAccountOper, String userId, String userType, String teamRole, String role) throws BaseRunException;

    /**
     * 删除指定ID的团队关系
     */
    void deleteTeamSpaceMemberById(UserToken user, long teamSpaceId, long id) throws BaseRunException;

    /**
     * 获取指定ID的团队关系
     */
    TeamSpaceMemberships getTeamSpaceMemberById(UserToken user, long teamSpaceId, long id) throws BaseRunException;

    /**
     * 列举团队空间成员,teamRole：按照角色过滤，keyword:按照用户名模糊匹配(name%)
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    TeamMemberList listTeamSpaceMemberships(UserToken user, long teamSpaceId, List<Order> orderList, Limit limit, String teamRole, String keyword, boolean isAccountOper) throws BaseRunException;

    /**
     * 修改指定ID的团队关系
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    TeamSpaceMemberships modifyTeamSpaceMemberRoleById(UserToken user, long teamSpaceId, long id, String teamRole, String role, boolean isAccountOper) throws BaseRunException;

    /**
     * 从团队空间中删除指定的cloudUserId
     */
    void deleteMemberByUserId(UserToken user, long teamSpaceId, long cloudUserId) throws BaseRunException;

    void batchDeleteTeamSpaceMemberById(UserToken userInfo, Long teamId, Long[] teamMemberIds);

	long countTeamSpaceMemberships(UserToken user, long teamSpaceId, List<Order> orderList, Limit limit, String teamRole, String keyword, boolean isAccountOper) throws BaseRunException;
}
