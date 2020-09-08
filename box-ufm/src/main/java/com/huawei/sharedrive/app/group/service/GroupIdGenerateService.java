package com.huawei.sharedrive.app.group.service;

public interface GroupIdGenerateService
{
    /**
     * 删除群组ID
     * 
     * @param ownerBy
     */
    void delete();
    
    /**
     * 获取下一个群组ID
     * 
     * @param ownerBy
     * @return
     */
    long getNextGroupId();
}
