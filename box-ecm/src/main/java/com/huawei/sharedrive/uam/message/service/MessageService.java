package com.huawei.sharedrive.uam.message.service;

import java.util.Date;

import com.huawei.sharedrive.uam.message.domain.Message;
import com.huawei.sharedrive.uam.openapi.domain.MessageList;

public interface MessageService
{
    /**
     * 
     * @param message
     * @return
     */
    boolean beenExpired(Message message);
    
    MessageList listMessage(long receiverId, byte status, long offset, long startId, int length);
    
    Message updateStatus(long receiverId, long id, byte status);
    
    Message get(long receiverId, long id);
    
    int getTotalMessages(long receiverId, byte status, long startId);
    
    void cleanExpiredMessage(Date time);
    
    void cleanExpiredMessageStatus(Date time, int table);
}
