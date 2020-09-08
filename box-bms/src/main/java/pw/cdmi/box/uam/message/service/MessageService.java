package pw.cdmi.box.uam.message.service;

import java.util.Date;

import pw.cdmi.box.uam.message.domain.Message;

public interface MessageService
{
    void create(Message message);
    
    /**
     * 判断消息是否已过期
     * 
     * @param message
     * @return
     */
    boolean beenExpired(Message message);
    
    Message updateStatus(long receiverId, long id, byte status);
    
    Message get(long receiverId, long id);
    
    int getTotalMessages(long receiverId, byte status, long startId);
    
    /**
     * 只清理system_message表的信息，不清理system_message_status表的信息
     */
    void cleanExpiredMessage(Date time);
    
    void cleanExpiredMessageStatus(Date time, int table);
}
