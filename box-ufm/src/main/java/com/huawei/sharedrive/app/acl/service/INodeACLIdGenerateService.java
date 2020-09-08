package com.huawei.sharedrive.app.acl.service;

public interface INodeACLIdGenerateService
{
    /**
     * 获取团队空间的下一个ACL节点ID
     * 
     * @param cloudUserId
     * @return
     */
    long getNextNodeACLId(long cloudUserId);
    
    /**
     * 删除团队空间对应的节点关系
     * 
     * @param cloudUserId
     */
    void delete(long cloudUserId);
}
