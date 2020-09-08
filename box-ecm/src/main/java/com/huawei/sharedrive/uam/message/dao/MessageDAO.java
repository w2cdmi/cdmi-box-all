package com.huawei.sharedrive.uam.message.dao;

import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.uam.message.domain.Message;
import com.huawei.sharedrive.uam.message.domain.MessageStatus;

import pw.cdmi.box.domain.Limit;

public interface MessageDAO
{
    /**
     * 
     * @param message
     */
    void save(Message message);
    
    /**
     * 
     * @return
     */
    List<Message> listUnInitStatusMessage(long receiverId);
    
    void save(MessageStatus status);
    
    /**
     * 
     * @param message
     * @return
     */
    int getTotalMessages(long receiverId, byte status, long startId);
    
    /**
     * 
     * @param message
     * @param limit
     * @return
     */
    List<Message> listMessage(long receiverId, byte status, long startId, Limit limit);
    
    
    /**
     * 
     * @param message
     * @return
     */
    Message getMessage(long id);
    
    MessageStatus getMessageStatus(long receiverId, long messageId);
    
    /**
     * 
     * @param receiverId
     * @return
     */
    long getMaxId();
    
    /**
     * 
     * @param message
     * @return
     */
    int updateStatus(Message message);
    
    void cleanExpiredMessage(Date time);
    
    void cleanExpiredMessageStatus(Date time, int table);
}
