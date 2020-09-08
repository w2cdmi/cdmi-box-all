package com.huawei.sharedrive.uam.statistics.domain;

import java.util.List;

public class RestUserClusterStatisticsRequest
{
    private List<MilestioneInfo> milestones;
    
    public RestUserClusterStatisticsRequest()
    {
        
    }
    
    public RestUserClusterStatisticsRequest(List<MilestioneInfo> milestones)
    {
        this.milestones = milestones;
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
