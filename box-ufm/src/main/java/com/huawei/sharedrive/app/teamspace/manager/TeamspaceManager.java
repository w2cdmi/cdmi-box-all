package com.huawei.sharedrive.app.teamspace.manager;

import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceCreateRequest;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;

public interface TeamspaceManager
{
    TeamSpace deleteTeamspaceById(long teamspaceId, UserToken user);
    
    TeamSpace createTeamspace(RestTeamSpaceCreateRequest createRequest, UserToken userInfo);
}
