package com.huawei.sharedrive.app.openapi.restv2.message;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.message.domain.Message;
import com.huawei.sharedrive.app.message.domain.MessageStatus;
import com.huawei.sharedrive.app.message.manage.MessageManager;
import com.huawei.sharedrive.app.message.manage.MessageSender;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.SystemTokenHelper;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.message.ListMessageRequest;
import com.huawei.sharedrive.app.openapi.domain.message.MessageList;
import com.huawei.sharedrive.app.openapi.domain.message.MessageListenUrl;
import com.huawei.sharedrive.app.openapi.domain.message.MessagePublishRequest;
import com.huawei.sharedrive.app.openapi.domain.message.MessageResponse;
import com.huawei.sharedrive.app.openapi.domain.message.UpdateMessageRequest;

/**
 * 用户消息接口, 提供消息列举, 消息状态变更, 消息删除等功能
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-4-30
 * @see
 * @since
 */
@Controller
@RequestMapping(value = "/api/v2/messages")
@Api(description = "用户消息接口, 提供消息列举, 消息状态变更, 消息删除等功能")
public class MessageAPI
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageAPI.class);
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private SystemTokenHelper systemTokenHelper;
    
    @Autowired
    private MessageManager messageManager;
    
    @Autowired
    private MessageSender messageSender;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteMessage(HttpServletRequest request, @PathVariable Long messageId,
        @RequestHeader("Authorization") String token) throws BaseRunException
    {
        // Token 验证
        UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
        String[] description = {String.valueOf(messageId)};
        try
        {
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), userToken.getCloudUserId());
            messageManager.deleteMessagae(userToken.getCloudUserId(), messageId);
        }
        catch (Exception e)
        {
            LOGGER.error("Delete message failed!", e);
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.DELETE_MESSAGE_ERR,
                description,
                null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.DELETE_MESSAGE,
            description,
            null);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/listener", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<MessageListenUrl> getListener(@RequestHeader("Authorization") String token,
        HttpServletRequest request) throws BaseRunException
    {
        MessageListenUrl listerner = null;
        // Token 验证
        UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
        try
        {
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), userToken.getCloudUserId());
            listerner = messageManager.getListener(userToken.getCloudUserId());
        }
        catch (RuntimeException e)
        {
            LOGGER.error("Get message listener failed!", e);
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_MESSAGE_ADDRESS_ERR,
                null,
                null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_MESSAGE_ADDRESS,
            null,
            null);
        return new ResponseEntity<MessageListenUrl>(listerner, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/items", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<MessageList> listMessage(HttpServletRequest request,
        @RequestBody(required = false) ListMessageRequest listMessageRequest,
        @RequestHeader("Authorization") String token) throws BaseRunException
    {
        UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
        MessageList messageList = null;
        try
        {
            if (listMessageRequest == null)
            {
                listMessageRequest = new ListMessageRequest();
            }
            else
            {
                listMessageRequest.checkParameter();
            }
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), userToken.getCloudUserId());
            
            messageList = messageManager.listMessage(userToken.getCloudUserId(), listMessageRequest);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("list message failed!", e);
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.LIST_MESSAGE_RECEIVE_ERR,
                null,
                null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.LIST_MESSAGE_RECEIVE,
            null,
            null);
        return new ResponseEntity<MessageList>(messageList, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/publish", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> sendMessage(HttpServletRequest request,
        @RequestBody MessagePublishRequest publishRequest,
        @RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date)
        throws BaseRunException
    {
        // 采用系统鉴权
        systemTokenHelper.checkSystemToken(authorization, date);
        
        publishRequest.checkParameter();
        String[] akArray = authorization.split(",");
        UserToken userToken = new UserToken();
        userToken.setLoginName(akArray[1]);
        userToken.setDeviceAddress(request.getRemoteAddr());
        try
        {
            Message message = new Message(publishRequest);
            messageSender.sendMessage(message);
            
        }
        catch (Exception e)
        {
            LOGGER.error("send message failed!", e);
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.PUBLISH_MESSAGE_ERR,
                null,
                null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.PUBLISH_MESSAGE,
            null,
            null);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{messageId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<MessageResponse> updateMessageStatus(HttpServletRequest request,
        @PathVariable Long messageId, @RequestBody UpdateMessageRequest updateMessageRequest,
        @RequestHeader("Authorization") String token) throws BaseRunException
    {
        updateMessageRequest.checkParameters();
        // Token 验证
        UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
        String[] description = {String.valueOf(messageId)};
        Message message = null;
        try
        {
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), userToken.getCloudUserId());
            
            message = messageManager.updateMessage(userToken.getCloudUserId(),
                messageId,
                MessageStatus.getValue(updateMessageRequest.getStatus()));
            
        }
        catch (Exception e)
        {
            LOGGER.error("Update message failed!", e);
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.UPDATE_MESSAGE_ERR,
                description,
                null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.UPDATE_MESSAGE,
            description,
            null);
        return new ResponseEntity<MessageResponse>(message.toMessageResponse(), HttpStatus.OK);
    }
    
}
