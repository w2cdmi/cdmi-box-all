package com.huawei.sharedrive.app.group.service;

public interface GroupMembershipsIdGenerateService
{
    
    /**
     * 删除群组ID
     * 
     * @param ownerBy
     */
    void delete(long groupId);
    
    /**
     * 获取下一个群组ID
     * 
     * @param ownerBy
     * @return
     */
    long getNextMembershipsId(long groupId);
    
}
