package com.huawei.sharedrive.app.message.dao;

import java.util.List;

import com.huawei.sharedrive.app.message.domain.Message;

import pw.cdmi.box.domain.Limit;

public interface MessageDAO
{
    /** 
     * 保存消息对象
     * 
     * @param message 
     */
    void save(Message message);
    
    /** 
     * 根据receiverId和messageId获取消息
     * 
     * @param message
     * @return 
     */
    Message get(Message message);

    /** 
     * 根据receiverId和消息id删除消息
     * 
     * @param message
     * @return 
     */
    int delete(Message message);
    
    /** 
     * 更新消息状态
     * 
     * @param message
     * @return 
     */
    int updateStatus(Message message);

    /** 
     * 获取消息总数
     * 
     * @param message
     * @return 
     */
    int getTotalMessages(Message message);
    
    /** 
     * 列举消息
     * 
     * @param message
     * @param limit
     * @return 
     */
    List<Message> listMessage(Message message, Limit limit);
    
    /** 
     * 获取消息接收者当前最大消息id
     * 
     * @param receiverId
     * @return 
     */
    long getMaxId(long receiverId);
    
    /** 
     * 清理过期的消息
     * 
     * @param db
     * @param table
     * @return 
     */
    int cleanExpiredMessage(int db, int table);
}
