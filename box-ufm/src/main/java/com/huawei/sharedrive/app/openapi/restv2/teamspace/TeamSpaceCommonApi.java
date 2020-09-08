package com.huawei.sharedrive.app.openapi.restv2.teamspace;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;

@Controller
public class TeamSpaceCommonApi
{
    @Autowired
    private FileBaseService fileBaseService;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public void sendTeamSpaceEvent(TeamSpace teamSpace, UserToken userInfo, UserLogType userLogType,
        Long teamId, String loginName, String role)
    {
        String[] logParams = new String[]{StringUtils.trimToEmpty(loginName), String.valueOf(role),
            String.valueOf(teamId)};
        if (teamSpace != null)
        {
            String keyword = StringUtils.trimToEmpty(teamSpace.getName());
            fileBaseService.sendINodeEvent(userInfo,
                EventType.OTHERS,
                null,
                null,
                userLogType,
                logParams,
                keyword);
        }
        else
        {
            fileBaseService.sendINodeEvent(userInfo,
                EventType.OTHERS,
                null,
                null,
                userLogType,
                logParams,
                null);
        }
    }
}
