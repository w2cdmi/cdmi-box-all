package com.huawei.sharedrive.app.favorite.service;

public interface FavoriteIdGenerateService
{
    /**
     * 获取用户的下一个节点ID
     * 
     * @param userId
     * @return
     */
    long getNextId(long userId);
    
    /**
     * 删除用户对应的节点关系
     * 
     * @param userId
     */
    void delete(long userId);
}
