package com.huawei.sharedrive.uam.statistics.domain;

import java.util.List;

public class UserStoreCurrentRequest
{
    private List<MilestioneInfo> milesTines;
    
    public List<MilestioneInfo> getMilesTines()
    {
        return milesTines;
    }
    
    public void setMilesTines(List<MilestioneInfo> milesTines)
    {
        this.milesTines = milesTines;
    }
    
}
