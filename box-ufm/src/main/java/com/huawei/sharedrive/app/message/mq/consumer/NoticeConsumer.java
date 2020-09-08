package com.huawei.sharedrive.app.message.mq.consumer;

import java.util.Map;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.message.domain.Message;
import com.huawei.sharedrive.app.message.domain.MessageParser;
import com.huawei.sharedrive.app.message.domain.MessageType;
import com.huawei.sharedrive.app.message.task.AsyncSendMessageTask;
import com.huawei.sharedrive.app.message.websocket.MessageListener;
import com.huawei.sharedrive.app.openapi.domain.message.MessageResponse;

import pw.cdmi.core.utils.JsonUtils;

public class NoticeConsumer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NoticeConsumer.class);
    
    @Autowired
    private AsyncSendMessageTask asyncSendMessageTask;
    
    public void receive(byte[] data)
    {
        try
        {
            Message message = MessageParser.bytesToMessage(data, 0, data.length);
            MessageResponse response = new MessageResponse(message);
            
            String content = JsonUtils.toJsonExcludeNull(response);
            if(LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Receive message: " + content);
            }
            if (message.getType() == MessageType.SYSTEM.getValue())
            {
                noticeToAll(content);
            }
            else
            {
                noticeToOne(content, message.getReceiverId());
            }
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
        }
    }
    
    /**
     * 通知到所有在线的人
     * 
     * @param message
     */
    private void noticeToAll(String message)
    {
        // 异步发送消息
        asyncSendMessageTask.notice(message);
    }
    
    /**
     * 通知到指定的人
     * 
     * @param message
     * @param receiverId
     */
    private void noticeToOne(final String message, long receiverId)
    {
        String clientKey = MessageListener.USER_SESSOIN_KEY_PREFIX + receiverId;
        Map<String, Session> sessionMap = MessageListener.getRemoteSession(clientKey);
        if (sessionMap == null || sessionMap.isEmpty())
        {
            return;
        }
        Session session = null;
        for (Map.Entry<String, Session> entry : sessionMap.entrySet())
        {
            session = entry.getValue();
            if (session != null)
            {
                try
                {
                    session.getBasicRemote().sendText(message);
                }
                catch (Exception e)
                {
                    LOGGER.error("Notify client failed.", e);
                }
            }
        }
    }
    
}
