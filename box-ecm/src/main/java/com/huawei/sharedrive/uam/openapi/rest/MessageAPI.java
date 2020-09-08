package com.huawei.sharedrive.uam.openapi.rest;

import javax.servlet.http.HttpServletRequest;

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

import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.message.domain.Message;
import com.huawei.sharedrive.uam.message.service.MessageService;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.ListMessageRequest;
import com.huawei.sharedrive.uam.openapi.domain.MessageList;
import com.huawei.sharedrive.uam.openapi.domain.MessageResponse;
import com.huawei.sharedrive.uam.openapi.domain.UpdateMessageRequest;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.service.AdminService;

import pw.cdmi.common.log.UserLog;

@Controller
@RequestMapping(value = "/api/v2/messages")
public class MessageAPI
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageAPI.class);
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private UserLogService userLogService;
    
    @RequestMapping(value = "/items", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<MessageList> listMessage(HttpServletRequest request,
        @RequestBody(required = false) ListMessageRequest listMessageRequest,
        @RequestHeader("Authorization") String token) throws BaseRunException
    {
        UserLog userLog = null;
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
            
            UserToken userToken = userTokenHelper.checkTokenAndGetUser(token);
            userLog = UserLogType.getUserLog(userToken);
            MessageList messageList = messageService.listMessage(userToken.getCloudUserId(),
                listMessageRequest.getStatusCode(),
                listMessageRequest.getOffset(),
                listMessageRequest.getStartId(),
                listMessageRequest.getLimit());
            userLogService.saveUserLog(userLog, UserLogType.KEY_MESSAGE_LIST, null);
            return new ResponseEntity<MessageList>(messageList, HttpStatus.OK);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("list message failed!", e);
            userLogService.saveUserLog(userLog, UserLogType.KEY_MESSAGE_LIST_ERR, null);
            throw e;
        }
        catch (Exception e)
        {
            LOGGER.error("list message failed!", e);
            userLogService.saveUserLog(userLog, UserLogType.KEY_MESSAGE_LIST_ERR, null);
            throw e;
        }
    }
    
    @RequestMapping(value = "/{messageId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<MessageResponse> updateMessageStatus(HttpServletRequest request,
        @PathVariable Long messageId, @RequestBody UpdateMessageRequest updateMessageRequest,
        @RequestHeader("Authorization") String token) throws BaseRunException
    {
        updateMessageRequest.checkParameters();
        UserLog userLog = null;
        String[] param = {messageId + ""};
        try
        {
            UserToken userToken = userTokenHelper.checkTokenAndGetUser(token);
            userLog = UserLogType.getUserLog(userToken);
            Message message = messageService.updateStatus(userToken.getCloudUserId(),
                messageId,
                updateMessageRequest.getStatusCode());
            
            MessageResponse response = new MessageResponse(message);
            Admin admin = adminService.get(message.getProviderId());
            if (null != admin)
            {
                response.setProviderName(admin.getLoginName());
                response.setProviderUsername(admin.getLoginName());
            }
            userLogService.saveUserLog(userLog, UserLogType.KEY_UPDATE_MESSAGE, param);
            
            return new ResponseEntity<MessageResponse>(response, HttpStatus.OK);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("update message failed!", e);
            userLogService.saveUserLog(userLog, UserLogType.KEY_UPDATE_MESSAGE_ERR, param);
            
            throw e;
        }
        catch (Exception e)
        {
            LOGGER.error("update message failed!", e);
            userLogService.saveUserLog(userLog, UserLogType.KEY_UPDATE_MESSAGE_ERR, param);
            
            throw e;
        }
    }
}
