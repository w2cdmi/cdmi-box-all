/**
 * 
 */
package com.huawei.sharedrive.app.files.service;

/**
 * @author q90003805
 * 
 */
public interface INodeIdGenerateService
{
    /**
     * 获取用户的下一个节点ID
     * 
     * @param userId
     * @return
     */
    long getNextUserNodeId(long userId);
    
    /**
     * 删除用户对应的节点关系
     * 
     * @param userId
     */
    void delete(long userId);
    
}