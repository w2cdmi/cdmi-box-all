package com.huawei.sharedrive.app.teamspace.service.impl;

import java.io.Serializable;
import java.util.Comparator;

import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;

public class TeamUsedSpaceAscComparator implements Comparator<TeamSpace>, Serializable
{
    
    private static final long serialVersionUID = -3109846810028542404L;
    
    @Override
    public int compare(TeamSpace arg0, TeamSpace arg1)
    {
        return Long.compare((Long) arg0.getSpaceUsed(), (Long) arg1.getSpaceUsed());
    }
    
}
