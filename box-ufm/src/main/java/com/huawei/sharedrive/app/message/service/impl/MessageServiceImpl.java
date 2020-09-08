package com.huawei.sharedrive.app.message.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.message.dao.MessageDAO;
import com.huawei.sharedrive.app.message.domain.Message;
import com.huawei.sharedrive.app.message.service.MessageIdGenerateService;
import com.huawei.sharedrive.app.message.service.MessageService;

import pw.cdmi.box.domain.Limit;

@Service("messageService")
public class MessageServiceImpl implements MessageService
{
    @Autowired
    private MessageIdGenerateService messageIdGenerateService;
    
    @Autowired
    private MessageDAO messageDAO;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void save(Message message)
    {
        long id = messageIdGenerateService.getNextMessageId(message.getReceiverId());
        message.setId(id);
        messageDAO.save(message);
    }
    
    @Override
    public void delete(long receiverId, long id)
    {
        Message message = new Message();
        message.setReceiverId(receiverId);
        message.setId(id);
        messageDAO.delete(message);
    }
    
    @Override
    public void updateStatus(long receiverId, long id, byte status)
    {
        Message message = new Message();
        message.setStatus(status);
        message.setReceiverId(receiverId);
        message.setId(id);
        messageDAO.updateStatus(message);
    }
    
    @Override
    public List<Message> listMessage(long receiverId, byte status, long startId, long offset, int length)
    {
        if(length == 0)
        {
            return new ArrayList<Message>(0);
        }
        
        Limit limit = new Limit(offset, length);
        
        Message filter = new Message();
        filter.setReceiverId(receiverId);
        filter.setStatus(status);
        filter.setId(startId);
        return messageDAO.listMessage(filter, limit);
    }

    @Override
    public int getTotalMessages(long receiverId, byte status, long startId)
    {
        Message filter = new Message();
        filter.setReceiverId(receiverId);
        filter.setStatus(status);
        filter.setId(startId);
        return messageDAO.getTotalMessages(filter);
    }

    @Override
    public long getMessageId(long receiverId)
    {
        return messageIdGenerateService.getNextMessageId(receiverId);
    }

    @Override
    public Message get(long receiverId, long id)
    {
        Message filter = new Message();
        filter.setReceiverId(receiverId);
        filter.setId(id);
        return messageDAO.get(filter);
    }

    @Override
    public int cleanExpiredMessage(int db, int table)
    {
        return messageDAO.cleanExpiredMessage(db, table);
    }
    
}
