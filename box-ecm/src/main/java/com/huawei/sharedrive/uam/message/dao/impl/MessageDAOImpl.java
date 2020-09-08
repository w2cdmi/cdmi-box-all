package com.huawei.sharedrive.uam.message.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.uam.core.dao.util.HashTool;
import com.huawei.sharedrive.uam.message.dao.MessageDAO;
import com.huawei.sharedrive.uam.message.domain.Message;
import com.huawei.sharedrive.uam.message.domain.MessageStatus;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;

@Repository
@SuppressWarnings("deprecation")
public class MessageDAOImpl extends AbstractDAOImpl implements MessageDAO
{
    private static final int TABLE_COUNT = 10;
    
    private static final int BASE_MESSAGE_ID = 1;
    
    @Override
    public void save(Message message)
    {
        sqlMapClientTemplate.insert("Message.create", message);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Message> listUnInitStatusMessage(long receiverId)
    {
        int tableSuffix = getTableSuffix(receiverId);
        
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("receiverId", receiverId);
        params.put("tableSuffix", tableSuffix);
        
        return sqlMapClientTemplate.queryForList("Message.listUnInitStatusMessage", params);
    }
    
    @Override
    public void save(MessageStatus status)
    {
        int tableSuffix = getTableSuffix(status.getReceiverId());
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("status", status);
        params.put("tableSuffix", tableSuffix);
        sqlMapClientTemplate.insert("Message.createStatus", params);
    }
    
    @Override
    public int getTotalMessages(long receiverId, byte status, long startId)
    {
        int tableSuffix = getTableSuffix(receiverId);
        Map<String, Object> params = new HashMap<String, Object>(4);
        params.put("status", status);
        params.put("startId", startId);
        params.put("receiverId", receiverId);
        params.put("tableSuffix", tableSuffix);
        
        return (int) sqlMapClientTemplate.queryForObject("Message.getTotalMessages", params);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Message> listMessage(long receiverId, byte status, long startId, Limit limit)
    {
        int tableSuffix = getTableSuffix(receiverId);
        Map<String, Object> params = new HashMap<String, Object>(5);
        params.put("status", status);
        params.put("receiverId", receiverId);
        params.put("startId", startId);
        params.put("tableSuffix", tableSuffix);
        
        params.put("limit", limit);
        
        return sqlMapClientTemplate.queryForList("Message.listMessage", params);
    }
    
    
    @Override
    public Message getMessage(long id)
    {
        Object obj = sqlMapClientTemplate.queryForObject("Message.getMessage", id);
        if (null == obj)
        {
            return null;
        }
        return (Message) obj;
    }
    
    @Override
    public MessageStatus getMessageStatus(long receiverId, long messageId)
    {
        int tableSuffix = getTableSuffix(receiverId);
        Map<String, Object> params = new HashMap<String, Object>(3);
        params.put("receiverId", receiverId);
        params.put("tableSuffix", tableSuffix);
        params.put("messageId", messageId);
        
        Object obj = sqlMapClientTemplate.queryForObject("Message.getMessageStatus", params);
        if (null == obj)
        {
            return null;
        }
        return (MessageStatus) obj;
    }
    
    @Override
    public long getMaxId()
    {
        Object maxId = sqlMapClientTemplate.queryForObject("Message.getMaxId");
        return maxId == null ? BASE_MESSAGE_ID : (long) maxId;
    }
    
    @Override
    public int updateStatus(Message message)
    {
        int tableSuffix = getTableSuffix(message.getReceiverId());
        Map<String, Object> params = new HashMap<String, Object>(3);
        params.put("message", message);
        params.put("tableSuffix", tableSuffix);
        
        return sqlMapClientTemplate.update("Message.updateStatus", params);
    }
    
    private int getTableSuffix(long receiverId)
    {
        return (int) (HashTool.apply(String.valueOf(receiverId)) % TABLE_COUNT);
    }
    
    @Override
    public void cleanExpiredMessage(Date time)
    {
        sqlMapClientTemplate.delete("Message.cleanExpiredMessage", time);
    }
    
    @Override
    public void cleanExpiredMessageStatus(Date time, int table)
    {
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("time", time);
        params.put("tableSuffix", table);
        
        sqlMapClientTemplate.delete("Message.cleanExpiredMessageStatus", params);
    }
}
