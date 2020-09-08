package com.huawei.sharedrive.app.openapi.domain.statistics;

import java.util.HashSet;
import java.util.List;

import com.huawei.sharedrive.app.exception.InvalidParamException;

public class RestUserClusterStatisticsRequest
{
    private List<MilestioneInfo> milestones;
    
    
    public void checkParameter() throws InvalidParamException
    {        
        if (milestones == null)
        {
            throw new InvalidParamException("milestiones is empty");
        }
        if (milestones.isEmpty())
        {
            throw new InvalidParamException("milestiones is empty");
        }
        HashSet<Long> set = new HashSet<Long>(milestones.size());
        for (MilestioneInfo item : milestones)
        {
            if (item.getMilestone() == null || item.getMilestone() <= 0)
            {
                throw new InvalidParamException("milestione is negative");
            }
            set.add(item.getMilestone());
        }
        if (set.size() != milestones.size())
        {
            throw new InvalidParamException("milestiones has the same value");
        }
        
        if (milestones.size() > 10)
        {
            throw new InvalidParamException("The sum of milestiones exceed the maximum 10");
        }
    }
    
    public List<MilestioneInfo> getMilestones()
    {
        return milestones;
    }
    
    public void setMilestones(List<MilestioneInfo> milestones)
    {
        this.milestones = milestones;
    }
    
}
