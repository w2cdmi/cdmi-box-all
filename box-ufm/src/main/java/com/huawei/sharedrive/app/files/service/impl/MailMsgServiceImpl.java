package com.huawei.sharedrive.app.files.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.exception.NoSuchFileException;
import com.huawei.sharedrive.app.files.dao.MailMsgDao;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.MailMsg;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.MailMsgService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

@Service("mailMsgService")
public class MailMsgServiceImpl implements MailMsgService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MailMsgServiceImpl.class);
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private MailMsgDao mailMsgDao;
    
    @Override
    public MailMsg createMailMsg(UserToken user, MailMsg msg)
    {
        checkAndGetNode(msg.getOwnerId(), msg.getNodeId());
        msg.setUserId(user.getId());
        mailMsgDao.create(msg);
        return msg;
    }
    
    @Override
    public MailMsg updateMailMsg(UserToken user, MailMsg msg)
    {
        mailMsgDao.update(msg);
        return msg;
    }
    
    @Override
    public MailMsg getMailMsg(UserToken user, String source, long ownerId, long nodeId)
    {
        MailMsg msg = new MailMsg();
        msg.setUserId(user.getId());
        msg.setSource(source);
        msg.setOwnerId(ownerId);
        msg.setNodeId(nodeId);
        return mailMsgDao.get(msg);
    }
    
    private INode checkAndGetNode(long ownerId, long nodeId)
    {
        INode node = fileBaseService.getINodeInfo(ownerId, nodeId);
        
        if (node == null)
        {
            LOGGER.error("Node not exist, owner id: {}, id: {}", ownerId, nodeId);
            throw new NoSuchFileException("File not exist");
        }
        return node;
    }
}
