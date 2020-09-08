package com.huawei.sharedrive.uam.openapi.rest;

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

import com.huawei.sharedrive.uam.announcement.domain.Announcement;
import com.huawei.sharedrive.uam.announcement.service.AnnouncementService;
import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.exception.NoSuchAnnouncementException;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.AnnouncementList;
import com.huawei.sharedrive.uam.openapi.domain.AnnouncementResponse;
import com.huawei.sharedrive.uam.openapi.domain.ListAnnouncementRequest;

import pw.cdmi.common.log.UserLog;

/**
 * 用户消息接口, 提供消息列举, 消息状态变更, 消息删除等功能
 * 
 * @version CloudStor CSE Service Platform Subproject, 2014-4-30
 * @see
 * @since
 */
@Controller
@RequestMapping(value = "/api/v2/announcements")
public class AnnouncementAPI
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnouncementAPI.class);
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private AnnouncementService announcementService;
    
    @Autowired
    private UserLogService userLogService;
    
    @RequestMapping(value = "/items", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<AnnouncementList> listAnnouncement(
        @RequestBody(required = false) ListAnnouncementRequest listAnnouncementRequest,
        @RequestHeader("Authorization") String token) throws BaseRunException
    {
        UserLog userLog = null;
        try
        {
            if (listAnnouncementRequest == null)
            {
                listAnnouncementRequest = new ListAnnouncementRequest();
            }
            else
            {
                listAnnouncementRequest.checkParameter();
            }
            
            // Token 验证
            UserToken userToken = userTokenHelper.checkTokenAndGetUser(token);
            userLog = UserLogType.getUserLog(userToken);
            
            AnnouncementList list = announcementService.listAnnouncement(listAnnouncementRequest.getOffset(),
                listAnnouncementRequest.getStartId(),
                listAnnouncementRequest.getLimit());
            userLogService.saveUserLog(userLog, UserLogType.KEY_MESSAGE_LIST_ALL, null);
            
            return new ResponseEntity<AnnouncementList>(list, HttpStatus.OK);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("list announcement failed!", e);
            userLogService.saveUserLog(userLog, UserLogType.KEY_MESSAGE_LIST_ALL_ERR, null);
            throw e;
        }
        catch (Exception e)
        {
            LOGGER.error("list announcement failed!", e);
            userLogService.saveUserLog(userLog, UserLogType.KEY_MESSAGE_LIST_ALL_ERR, null);
            
            throw e;
        }
    }
    
    @RequestMapping(value = "/{announcementId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<AnnouncementResponse> getAnnouncement(@PathVariable long announcementId,
        @RequestHeader("Authorization") String token) throws BaseRunException
    {
        UserLog userLog = null;
        String[] params = {announcementId + ""};
        try
        {
            // Token 验证
            UserToken userToken = userTokenHelper.checkTokenAndGetUser(token);
            userLog = UserLogType.getUserLog(userToken);
            Announcement announcement = announcementService.getAnnouncement(announcementId);
            if (null == announcement)
            {
                LOGGER.warn("announcement [ {} ]does not exist.", announcementId);
                throw new NoSuchAnnouncementException("announcement does not exist");
            }
            userLogService.saveUserLog(userLog, UserLogType.KEY_MESSAGE_LIST, params);
            
            return new ResponseEntity<AnnouncementResponse>(new AnnouncementResponse(announcement),
                HttpStatus.OK);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("get announcement failed!", e);
            userLogService.saveUserLog(userLog, UserLogType.KEY_MESSAGE_LIST_ERR, params);
            throw e;
        }
        catch (Exception e)
        {
            LOGGER.error("get announcement failed!", e);
            userLogService.saveUserLog(userLog, UserLogType.KEY_MESSAGE_LIST_ERR, params);
            throw e;
        }
        
    }
}
