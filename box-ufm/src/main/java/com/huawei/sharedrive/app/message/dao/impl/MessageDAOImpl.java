package com.huawei.sharedrive.app.message.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.message.dao.MessageDAO;
import com.huawei.sharedrive.app.message.domain.Message;
import com.huawei.sharedrive.app.utils.BusinessConstants;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.core.utils.HashTool;

@Repository
@SuppressWarnings("deprecation")
public class MessageDAOImpl extends AbstractDAOImpl implements MessageDAO
{
    // 消息表总数
    private static final int TABLE_COUNT = 10;
    
    private static final int BASE_MESSAGE_ID = 1;
    
    @Override
    public int cleanExpiredMessage(int db, int table)
    {
        Map<String, Object> params = new HashMap<String, Object>(10);
        Message filter = new Message();
        filter.setTableSuffix(table);
        filter.setExpiredAt(new Date());
        params.put("partitionNum", db);
        params.put("filter", filter);
        return sqlMapClientTemplate.delete("Message.cleanExpiredMessage", params);
    }
    
    @Override
    public int delete(Message message)
    {
        message.setTableSuffix(getTableSuffix(message.getReceiverId()));
        return sqlMapClientTemplate.delete("Message.delete", message);
    }
    
    @Override
    public Message get(Message message)
    {
        message.setTableSuffix(getTableSuffix(message.getReceiverId()));
        return (Message) sqlMapClientTemplate.queryForObject("Message.get", message);
    }
    
    @Override
    public long getMaxId(long receiverId)
    {
        Message message = new Message();
        message.setReceiverId(receiverId);
        message.setTableSuffix(getTableSuffix(receiverId));
        Object maxId = sqlMapClientTemplate.queryForObject("Message.getMaxId", message);
        return maxId == null ? BASE_MESSAGE_ID : (long) maxId;
    }
    
    @Override
    public int getTotalMessages(Message message)
    {
        message.setTableSuffix(getTableSuffix(message.getReceiverId()));
        return (int) sqlMapClientTemplate.queryForObject("Message.getTotalMessages", message);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Message> listMessage(Message message, Limit limit)
    {
        message.setTableSuffix(getTableSuffix(message.getReceiverId()));
        Map<String, Object> params = new HashMap<String, Object>(BusinessConstants.INITIAL_CAPACITIES);
        params.put("filter", message);
        params.put("limit", limit);
        return sqlMapClientTemplate.queryForList("Message.listMessage", params);
    }
    
    @Override
    public void save(Message message)
    {
        message.setTableSuffix(getTableSuffix(message.getReceiverId()));
        sqlMapClientTemplate.insert("Message.create", message);
    }
    
    @Override
    public int updateStatus(Message message)
    {
        message.setTableSuffix(getTableSuffix(message.getReceiverId()));
        return sqlMapClientTemplate.update("Message.updateStatus", message);
    }
    
    private int getTableSuffix(long receiverId)
    {
        if (receiverId <= 0)
        {
            throw new InvalidParamException("Illegal user id " + receiverId);
        }
        return (int) (HashTool.apply(String.valueOf(receiverId)) % TABLE_COUNT);
    }
    
}
