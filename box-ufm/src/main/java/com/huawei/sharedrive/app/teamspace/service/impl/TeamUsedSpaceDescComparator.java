package com.huawei.sharedrive.app.teamspace.service.impl;

import java.io.Serializable;
import java.util.Comparator;

import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;

public class TeamUsedSpaceDescComparator implements Comparator<TeamSpace>, Serializable
{
    
    private static final long serialVersionUID = 4911022451819838049L;
    
    @Override
    public int compare(TeamSpace arg0, TeamSpace arg1)
    {
        return Long.compare((Long) arg1.getSpaceUsed(), (Long) arg0.getSpaceUsed());
    }
    
}
