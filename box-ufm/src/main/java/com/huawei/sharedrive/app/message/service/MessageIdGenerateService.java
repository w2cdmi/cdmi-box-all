package com.huawei.sharedrive.app.message.service;

public interface MessageIdGenerateService
{
    /**
     * 获取用户的下一条消息ID
     * 
     * @param userId
     * @return
     */
    long getNextMessageId(long receiverId);
    
    /**
     * 删除用户对应的节点关系
     * 
     * @param userId
     */
    void delete(long userId);
}
