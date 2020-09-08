package com.huawei.sharedrive.app.openapi.restv2.log;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.log.service.UserLogService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.userlog.UserLogListReq;
import com.huawei.sharedrive.app.openapi.domain.userlog.UserLogListRsp;
import com.huawei.sharedrive.app.utils.Constants;

import pw.cdmi.common.log.ClientType;
import pw.cdmi.core.log.Level;

/**
 * 文件夹/文件共用接口, 提供复制, 移动, 删除等共用操作
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-4-30
 * @see
 * @since
 */
@Controller
@RequestMapping(value = "/api/v2/userlogs")
@Api(hidden = true)
public class UserLogApi
{
    
    private static final Integer MAX_VALUE_LIMIT = 1000;
    
    private static final List<String> PLUGIN_APPID_SET = new ArrayList<String>(2);
    static
    {
        PLUGIN_APPID_SET.add(Constants.APPID_PPREVIEW);
        PLUGIN_APPID_SET.add(Constants.APPID_SECURITYSCAN);
    }
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private UserLogService userLogService;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UserLogListRsp> listLogs(@RequestBody UserLogListReq req,
        @RequestHeader("Authorization") String authorization, HttpServletRequest request)
        throws BaseRunException
    {
        String appId = userTokenHelper.checkAppSystemToken(authorization, request.getHeader("Date"));
        if (PLUGIN_APPID_SET.contains(appId))
        {
            throw new ForbiddenException("app " + appId + " is not allowed to access this method");
        }
        String[] akArray = authorization.split(",");
        UserToken userToken = new UserToken();
        userToken.setAppId(appId);
        userToken.setLoginName(akArray[1]);
        req.setAppId(appId);
        checkLimitAndOffset(req);
        checkAndConvertDataType(req);
        checkParams(req);
        UserLogListRsp result;
        try
        {
            result = userLogService.queryLogs(req);
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_LINK_FILE_INFO_ERR,
                null,
                null);
            throw e;
        }
        return new ResponseEntity<UserLogListRsp>(result, HttpStatus.OK);
    }
    
    private void checkParams(UserLogListReq req)
    {
        if (req.getLimit() <= 0 || req.getLimit() > MAX_VALUE_LIMIT)
        {
            throw new InvalidParamException("Bad limit " + req.getLimit());
        }
        if (req.getOffset() < 0)
        {
            throw new InvalidParamException("Bad offset " + req.getOffset());
        }
        if (null != req.getBeginTime())
        {
            req.setBeginTime(req.getBeginTime() / 1000);
        }
        if (null != req.getEndTime())
        {
            req.setEndTime(req.getEndTime() / 1000);
        }
    }
    
    private void checkLimitAndOffset(UserLogListReq req)
    {
        if (null == req.getLimit())
        {
            req.setLimit(100);
        }
        if (null == req.getOffset())
        {
            req.setOffset(0L);
        }
    }
    
    private void checkAndConvertDataType(UserLogListReq req)
    {
        try
        {
            if (req.getClientType() != null)
            {
                req.setClientTypeDb(ClientType.build(req.getClientType()).getValue());
            }
            if (req.getLevel() != null)
            {
                req.setLevelDb(Level.build(req.getLevel()).getValue());
            }
            if (req.getType() != null)
            {
                String type = req.getType().trim();
                String[] split = type.split(",");
                List<Integer> codeList = new ArrayList<Integer>(split.length);
                for (String operationType : split)
                {
                    if (StringUtils.isNotBlank(operationType))
                    {
                        Integer typeCode = UserLogType.valueOf(operationType).getTypeCode();
                        codeList.add(typeCode);
                    }
                }
                req.setTypeDb(codeList);
            }
        }
        catch (IllegalArgumentException e)
        {
            throw new InvalidParamException(e.getMessage(), e);
        }
        catch (InvalidParamException e)
        {
            throw new InvalidParamException(e.getMessage(), e);
        }
    }
    
}
