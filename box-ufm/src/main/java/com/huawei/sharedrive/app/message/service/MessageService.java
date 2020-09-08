package com.huawei.sharedrive.app.message.service;

import java.util.List;

import com.huawei.sharedrive.app.message.domain.Message;

public interface MessageService
{
    void save(Message message);
    
    Message get(long receiverId, long id);
    
    void delete(long receiverId, long id);
    
    void updateStatus(long receiverId, long id, byte status);
    
    int getTotalMessages(long receiverId, byte status, long startId);
    
    List<Message> listMessage(long receiverId, byte status, long startId, long offset, int length);
    
    long getMessageId(long receiverId);
    
    int cleanExpiredMessage(int db, int table);
    
}
