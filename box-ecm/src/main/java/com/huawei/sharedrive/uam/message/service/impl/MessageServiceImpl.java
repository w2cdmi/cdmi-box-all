package com.huawei.sharedrive.uam.message.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.uam.exception.NoSuchMessageException;
import com.huawei.sharedrive.uam.message.dao.MessageDAO;
import com.huawei.sharedrive.uam.message.domain.Message;
import com.huawei.sharedrive.uam.message.domain.MessageStatus;
import com.huawei.sharedrive.uam.message.service.MessageService;
import com.huawei.sharedrive.uam.openapi.domain.MessageList;
import com.huawei.sharedrive.uam.openapi.domain.MessageResponse;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.service.AdminService;

import pw.cdmi.box.domain.Limit;

@Service("messageService")
public class MessageServiceImpl implements MessageService
{
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);
    
    @Autowired
    private MessageDAO messageDAO;
    
    @Autowired
    private AdminService adminService;
    
    public boolean beenExpired(Message message)
    {
        Date now = new Date();
        
        if (now.before(message.getExpiredAt()))
        {
            return false;
        }
        return true;
    }
    
    @Override
    public MessageList listMessage(long receiverId, byte status, long offset, long startId, int length)
    {
        generateMessageStatus(receiverId);
        
        Limit limit = new Limit();
        limit.setLength(length);
        
        limit.setOffset(offset);
        List<Message> list = messageDAO.listMessage(receiverId, status, startId, limit);
        
        long total = this.getTotalMessages(receiverId, status, startId);
        MessageList messageList = new MessageList();
        messageList.setOffset(offset);
        messageList.setLimit(length);
        messageList.setTotalCount(total);
        
        if (null != list && !list.isEmpty())
        {
            List<MessageResponse> responseList = new ArrayList<MessageResponse>(list.size());
            MessageResponse response;
            Admin admin;
            for (Message message : list)
            {
                response = new MessageResponse(message);
                admin = adminService.get(message.getProviderId());
                if (null != admin)
                {
                    response.setProviderName(admin.getLoginName());
                    response.setProviderUsername(admin.getLoginName());
                }
                
                responseList.add(response);
            }
            
            messageList.setMessages(responseList);
        }
        
        return messageList;
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void generateMessageStatus(long receiverId)
    {
        List<Message> messages = messageDAO.listUnInitStatusMessage(receiverId);
        
        if (null == messages || messages.isEmpty())
        {
            return;
        }
        
        for (Message message : messages)
        {
            if (beenExpired(message))
            {
                LOGGER.warn("message [ {} ] been expired.");
                break;
            }
            createNewMessageStatus(receiverId, message);
        }
    }
    
    private MessageStatus createNewMessageStatus(long receiverId, Message message)
    {
        MessageStatus status;
        status = new MessageStatus();
        status.setMessageId(message.getId());
        status.setReceiverId(receiverId);
        status.setStatus(MessageStatus.STATUS_UNREAD);
        status.setExpiredAt(message.getExpiredAt());
        try
        {
            messageDAO.save(status);
        }
        catch (Exception e)
        {
            LOGGER.warn("message status [ " + ReflectionToStringBuilder.toString(status)
                + " ] already exists.", e);
        }
        return status;
    }
    
    @Override
    public Message updateStatus(long receiverId, long id, byte status)
    {
        Message message = this.get(receiverId, id);
        if (null == message)
        {
            throw new NoSuchMessageException("Message does not exist");
        }
        message.setStatus(status);
        messageDAO.updateStatus(message);
        return message;
    }
    
    @Override
    public Message get(long receiverId, long id)
    {
        Message message = messageDAO.getMessage(id);
        if (null == message)
        {
            throw new NoSuchMessageException("Message does not exist");
        }
        
        MessageStatus status = messageDAO.getMessageStatus(receiverId, id);
        if (null == status)
        {
            if (beenExpired(message))
            {
                LOGGER.warn("message [ {} ] been expired.");
                throw new NoSuchMessageException("Message does not exist");
            }
            status = createNewMessageStatus(receiverId, message);
        }
        
        message.setReceiverId(receiverId);
        message.setStatus(status.getStatus());
        
        return message;
    }
    
    @Override
    public int getTotalMessages(long receiverId, byte status, long startId)
    {
        return messageDAO.getTotalMessages(receiverId, status, startId);
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void cleanExpiredMessage(Date time)
    {
        messageDAO.cleanExpiredMessage(time);
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void cleanExpiredMessageStatus(Date time, int table)
    {
        messageDAO.cleanExpiredMessageStatus(time, table);
    }
}
