package pw.cdmi.box.uam.message.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.box.uam.exception.NoSuchMessageException;
import pw.cdmi.box.uam.message.dao.MessageDAO;
import pw.cdmi.box.uam.message.domain.Message;
import pw.cdmi.box.uam.message.domain.MessagePublishRequest;
import pw.cdmi.box.uam.message.domain.MessageStatus;
import pw.cdmi.box.uam.message.service.MessageIdGenerateService;
import pw.cdmi.box.uam.message.service.MessageService;
import pw.cdmi.box.uam.statistics.service.StatisticsAccesskeyService;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.user.service.AdminService;
import pw.cdmi.common.domain.StatisticsConfig;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.DateUtils;

@Service("messageService")
public class MessageServiceImpl implements MessageService
{
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);
    
    @Autowired
    private MessageIdGenerateService messageIdGenerateService;
    
    @Autowired
    private MessageDAO messageDAO;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private StatisticsAccesskeyService statisticsAccesskeyService;
    
    @Autowired
    private AdminService adminService;
    
    private static final String URL_SEND_MESSAGE = "api/v2/messages/publish";
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void create(Message message)
    {
        long id = messageIdGenerateService.getNextId();
        message.setId(id);
        messageDAO.save(message);
        
        // 调用ufm接口发送消息
        StatisticsConfig config = statisticsAccesskeyService.getStatisticsConfig();
        Map<String, String> headers = new HashMap<String, String>(16);
        Date now = new Date();
        String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, now, null);
        String appSignatureKey = SignatureUtils.getSignature(config.getSecretKey(), dateStr);
        String authorization = "system," + config.getAccessKey() + ',' + appSignatureKey;
        headers.put("Authorization", authorization);
        headers.put("Date", dateStr);
        
        MessagePublishRequest request = new MessagePublishRequest(message);
        Admin admin = adminService.get(message.getProviderId());
        if (null != admin)
        {
            request.setProviderName(admin.getLoginName());
            request.setProviderUsername(admin.getLoginName());
        }
        request.setParams(message.getParams());
        
        TextResponse response = ufmClientService.performJsonPutTextResponse(URL_SEND_MESSAGE,
            headers,
            request);
        
        if (response.getStatusCode() != HttpStatus.SC_OK)
        {
            String errorMessage = "notice system message to ufm failed";
            LOGGER.error(errorMessage + "[ " + response.getStatusCode() + " ]");
            throw new BusinessException(errorMessage);
        }
        LOGGER.info("send message [ {} ]success.", ReflectionToStringBuilder.toString(message));
    }
    
    public boolean beenExpired(Message message)
    {
        Date now = new Date();
        return !now.before(message.getExpiredAt());
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void generateMessageStatus(long receiverId)
    {
        // 获取所有还未生成status信息的数据
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
            // 如果主键冲突，表示之前已经上传成功，则屏蔽该异常，使上层调用删除该文件
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
