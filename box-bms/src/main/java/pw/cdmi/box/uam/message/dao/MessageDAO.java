package pw.cdmi.box.uam.message.dao;

import java.util.Date;
import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.uam.message.domain.Message;
import pw.cdmi.box.uam.message.domain.MessageStatus;

public interface MessageDAO
{
    /**
     * 保存消息对象
     * 
     * @param message
     */
    void save(Message message);
    
    /**
     * 获取指定用户，所有未生成status信息的message数据
     * 
     * @return
     */
    List<Message> listUnInitStatusMessage(long receiverId);
    
    void save(MessageStatus status);
    
    /**
     * 获取消息总数
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
    
    // /**
    // * 根据起始消息id列举消息
    // *
    // * @param message
    // * @param limit
    // * @return
    // */
    // List<Message> listByStartId(long receiverId, byte status, Long startId, Limit
    // limit);
    
    /**
     * 根据receiverId和messageId获取消息
     * 
     * @param message
     * @return
     */
    Message getMessage(long id);
    
    MessageStatus getMessageStatus(long receiverId, long messageId);
    
    /**
     * 获取消息接收者当前最大消息id
     * 
     * @param receiverId
     * @return
     */
    long getMaxId();
    
    /**
     * 更新消息状态
     * 
     * @param message
     * @return
     */
    int updateStatus(Message message);
    
    void cleanExpiredMessage(Date time);
    
    void cleanExpiredMessageStatus(Date time, int table);
}
