package com.huawei.sharedrive.app.openapi.restv2.mailmsg;

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
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.files.domain.MailMsg;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.MailMsgService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.RestMailMsgSetRequest;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

@Controller
@RequestMapping(value = "/api/v2/mailmsgs")
@Api(hidden = true)
public class MailMsgAPI
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MailMsgAPI.class);
    @Autowired
    private MailMsgService mailMsgService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private FileBaseService fileBaseService;
    /**
     * set mail message
     * 
     * @param request
     * @param ownerId
     * @param nodeId
     * @param authToken
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{ownerId}/{nodeId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<MailMsg> setMailMsg(@RequestBody RestMailMsgSetRequest request,
        @PathVariable Long ownerId, @PathVariable Long nodeId, @RequestHeader("Authorization") String authToken)
        throws BaseRunException
    {
        UserToken userInfo = userTokenHelper.checkTokenAndGetUserForV2(authToken, null);
        
        FilesCommonUtils.checkNonNegativeIntegers(ownerId, nodeId);
        
        request.checkParameter();
        String[]description={String.valueOf(ownerId),String.valueOf(nodeId)};
        MailMsg msg=null;
        try
        {
            msg = mailMsgService.getMailMsg(userInfo, request.getSource(), ownerId, nodeId);
            
            if(msg == null)
            {
                msg = new MailMsg();
                msg.setSource(request.getSource());
                msg.setOwnerId(ownerId);
                msg.setNodeId(nodeId);
                msg.setSubject(request.getSubject());
                msg.setMessage(request.getMessage());
                msg = mailMsgService.createMailMsg(userInfo, msg);
                LOGGER.info("setMailMsg success");
            }
            else
            {
                msg.setSubject(request.getSubject());
                msg.setMessage(request.getMessage());
                msg = mailMsgService.updateMailMsg(userInfo, msg);
                LOGGER.info("updateMailMsg success");
            }
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userInfo,
                EventType.OTHERS,
                null,
                null,
                UserLogType.SET_MAIL_MESSAGE_ERR,
                description,
                null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userInfo,
            EventType.OTHERS,
            null,
            null,
            UserLogType.SET_MAIL_MESSAGE,
            description,
            null);
        transMailMsg(msg);
        return new ResponseEntity<MailMsg>(msg, HttpStatus.OK);
        
    }
    
    /**
     * get mail message
     * 
     * @param ownerId
     * @param nodeId
     * @param type
     * @param authToken
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{ownerId}/{nodeId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<MailMsg> getMailMsg(@PathVariable Long ownerId, @PathVariable Long nodeId, String source, @RequestHeader("Authorization") String authToken)
        throws BaseRunException
    {
        UserToken userInfo = userTokenHelper.checkTokenAndGetUserForV2(authToken, null);
        
        FilesCommonUtils.checkNonNegativeIntegers(ownerId, nodeId);
        
        if (source == null)
        {
            throw new InvalidParamException("type is null");
        }
        
        if(!MailMsg.SOURCE_LINK.equals(source) && !MailMsg.SOURCE_SHARE.equals(source))
        {
            throw new InvalidParamException("source is invalid:" + source);
        }
        
        MailMsg msg=null;
        String[]description={String.valueOf(ownerId),String.valueOf(nodeId)};
        try
        {
            msg = mailMsgService.getMailMsg(userInfo, source, ownerId, nodeId);
            if (msg == null)
            {
                LOGGER.error("msg not exist, owner id: {}, id: {}", ownerId, nodeId);
                throw new NoSuchItemsException("msg not exist");
            }
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userInfo,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_MAIL_MESSAGE_ERR,
                description,
                null);
            throw e;
        }
   
        fileBaseService.sendINodeEvent(userInfo,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_MAIL_MESSAGE,
            description,
            null);
        
        LOGGER.info("getMailMsg success");
        transMailMsg(msg);
        return new ResponseEntity<MailMsg>(msg, HttpStatus.OK);
        
    }
    
    private void transMailMsg(MailMsg mailMsg)
    {
        mailMsg.setSender(mailMsg.getUserId());
        mailMsg.setOwnedBy(mailMsg.getOwnerId());
    }
}
