package com.huawei.sharedrive.app.teamspace.service;

public interface SpaceMemberIdGenerateService
{
    /**
     * 获取下一个团队关系节点ID
     * 
     * @return
     */
    long getNextMemberId(long ownerBy);
    
    
    /**
     * 删除团队空间对应的节点关系
     * 
     * @param cloudUserId
     */
    void delete(long cloudUserId);
}
