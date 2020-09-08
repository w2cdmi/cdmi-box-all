package com.huawei.sharedrive.app.openapi.restv2.account;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.account.mamager.AccountManager;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.exception.UploadSizeTooLargeException;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.account.RestAccount;
import com.huawei.sharedrive.app.openapi.domain.account.RestCreateAccountRequest;
import com.huawei.sharedrive.app.openapi.domain.account.RestModifyAccountRequest;
import com.huawei.sharedrive.app.plugins.preview.domain.AccountWatermark;
import com.huawei.sharedrive.app.plugins.preview.service.AccountWatermarkService;
import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.app.utils.Constants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

import pw.cdmi.core.utils.DateUtils;

@Controller
@RequestMapping(value = "/api/v2/accounts")
public class AccountAPI
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountAPI.class);
    
    public static final int BUFFER_REST = 64 * 1024;
    
    private static final long OPERATE_SUCCESS = 1;
    
    public static final String WATERMARK_MAX_SIZE_KEY = "watermark.max.size";
    
    private static final Set<String> ALLOWED_APPID_SET = new HashSet<String>(10);
    
    private static final List<String> PLUGIN_APPID_SET = new ArrayList<String>(2);
    
    static
    {
        ALLOWED_APPID_SET.add(Constants.APPID_PPREVIEW);
    }
    
    static
    {
        PLUGIN_APPID_SET.add(Constants.APPID_PPREVIEW);
        PLUGIN_APPID_SET.add(Constants.APPID_SECURITYSCAN);
    }
    
    @Autowired
    private AccountManager accountManager;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private AccountWatermarkService accountWatermarkService;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    private int watermarkMaxSize;
    
    @PostConstruct
    public void init()
    {
        watermarkMaxSize = Integer.parseInt(systemConfigDAO.get(WATERMARK_MAX_SIZE_KEY).getValue());
    }
    
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody RestCreateAccountRequest restAccountRequest,
        @RequestHeader("Authorization") String authorization,
        @RequestHeader(value = "Date", required = false) String date)
    {
        String appId = userTokenHelper.checkAppSystemToken(authorization, date);
        
        String[] description = new String[]{String.valueOf(restAccountRequest.getEnterpriseId()), appId,
            String.valueOf(restAccountRequest.getFilePreviewable()),
            String.valueOf(restAccountRequest.getMaxMember()), String.valueOf(restAccountRequest.getMaxTeamspace()),
            String.valueOf(restAccountRequest.getMaxSpace())};
        
        UserToken userToken = new UserToken();
        userToken.setAppId(appId);
        userToken.setId(OPERATE_SUCCESS);
        String[] akArr = authorization.split(",");
        userToken.setLoginName(akArr[1]);
        
        try
        {
            if (PLUGIN_APPID_SET.contains(appId))
            {
                throw new ForbiddenException("app " + appId + " is not allowed to access this method");
            }
            restAccountRequest.checkParameter();
            RestAccount restAccount = accountManager.create(restAccountRequest, appId);
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.CREATE_ACCOUNT_CONFIGURATION,
                description,
                null);
            return new ResponseEntity<RestAccount>(restAccount, HttpStatus.CREATED);
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.CREATE_ACCOUNT_CONFIGURATION_ERR,
                description,
                null);
            throw e;
        }
    }
    
    @RequestMapping(value = "/{accountId}", method = RequestMethod.PUT)
    public ResponseEntity<?> modify(@RequestBody RestModifyAccountRequest restAccountRequest,
        @PathVariable(value = "accountId") Long accountId,
        @RequestHeader("Authorization") String authorization,
        @RequestHeader(value = "Date", required = false) String date)
    {
        String appId = userTokenHelper.checkAppSystemToken(authorization, date);
        
        String[] authParam = authorization.split(",");
        String ak = authParam[1];
        String[] description = new String[]{String.valueOf(accountId), appId,
            String.valueOf(restAccountRequest.isFilePreviewable()),
            String.valueOf(restAccountRequest.getMaxMember()), String.valueOf(restAccountRequest.getMaxTeamspace()),
            String.valueOf(restAccountRequest.getMaxSpace())};
        UserToken userToken = new UserToken();
        userToken.setAppId(appId);
        userToken.setId(OPERATE_SUCCESS);
        userToken.setLoginName(ak);
        
        try
        {
            restAccountRequest.checkParameter();
            if (PLUGIN_APPID_SET.contains(appId))
            {
                throw new ForbiddenException("app " + appId + " is not allowed to access this method");
            }
            RestAccount restAccount = accountManager.modify(restAccountRequest, appId, accountId, authorization);
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.MODIFY_ACCOUNT_CONFIGURATION,
                description,
                null);
            return new ResponseEntity<RestAccount>(restAccount, HttpStatus.OK);
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.MODIFY_ACCOUNT_CONFIGURATION_ERR,
                description,
                null);
            throw e;
        }
    }
    
    @RequestMapping(value = "/{accountId}", method = RequestMethod.GET)
    public ResponseEntity<?> getAccount(@PathVariable(value = "accountId") Long accountId,
        @RequestHeader("Authorization") String authorization,
        @RequestHeader(value = "Date", required = false) String date)
    {
        FilesCommonUtils.checkNonNegativeIntegers(accountId);
        String appId = userTokenHelper.checkAppAndAccountToken(authorization, date);
        RestAccount restAccount = accountManager.getRestAccountById(accountId);
        String[] authParam = authorization.split(",");
        String ak = authParam[1];
        UserToken userToken = new UserToken();
        userToken.setAppId(appId);
        userToken.setLoginName(ak);
        
        if (appId != null)
        {
            if (!restAccount.getAppId().equals(appId) || PLUGIN_APPID_SET.contains(appId))
            {
                fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.GET_ACCOUNT_INFO_ERR,
                    null,
                    null);
                throw new ForbiddenException("app " + appId + " is not allowed to do this");
            }
            
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_ACCOUNT_INFO,
            null,
            null);
        return new ResponseEntity<RestAccount>(restAccount, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{accountId}/watermark", method = RequestMethod.GET)
    public void getWatermark(HttpServletResponse response, @PathVariable(value = "accountId") Long accountId,
        @RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date)
    {
        String appId = userTokenHelper.checkAppSystemToken(authorization, date);
        Account account = accountManager.getById(accountId);
        String[] authParam = authorization.split(",");
        String ak = authParam[1];
        UserToken userToken = new UserToken();
        userToken.setAppId(appId);
        userToken.setLoginName(ak);
        if (!account.getAppId().equals(appId) && !ALLOWED_APPID_SET.contains(appId))
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_WATERMARK_INFO_ERR,
                null,
                null);
            throw new ForbiddenException("app " + appId + " is not allowed to access this method");
        }
        AccountWatermark mark = getWatermark(accountId);
        String configTime = DateUtils.dateToString(mark.getLastConfigTime());
        response.setContentType(MediaType.IMAGE_PNG.toString());
        response.setHeader("x-hw-last-config-time", configTime);
        OutputStream out = null;
        try
        {
            out = response.getOutputStream();
            out.write(mark.getWatermark());
            out.flush();
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_WATERMARK_INFO,
                null,
                null);
        }
        catch (IOException e)
        {
            LOGGER.warn("can not write watermark to client", e);
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_WATERMARK_INFO_ERR,
                null,
                null);
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    LOGGER.warn("", e);
                }
            }
        }

    }
    
    private AccountWatermark getWatermark(long accountId)
    {
        AccountWatermark mark = accountWatermarkService.getWatermarkByAccountId(accountId);
        if (mark == null)
        {
            mark = new AccountWatermark();
            mark.setAccountId(accountId);
            mark.setWatermark(new byte[0]);
            mark.setLastConfigTime(new Date());
        }
        return mark;
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "/{accountId}/watermark", method = RequestMethod.PUT)
    public void setWatermark(@PathVariable("accountId") Long accountId,
        @RequestHeader("Content-Length") Long objectLength,
        @RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date,
        InputStream in, HttpServletResponse response)
    {
        String appId = userTokenHelper.checkAppSystemToken(authorization, date);
        Account account = accountManager.getById(accountId);
        if (!account.getAppId().equals(appId))
        {
            throw new ForbiddenException("app " + appId + " is not allowed to access this method");
        }
        if (objectLength > watermarkMaxSize)
        {
            throw new UploadSizeTooLargeException();
        }
        byte[] buffer = new byte[watermarkMaxSize + BUFFER_REST];
        int off = 0;
        int len = buffer.length;
        int n = 0;
        String[] authParam = authorization.split(",");
        String ak = authParam[1];
        String[] description = new String[]{String.valueOf(appId)};
        UserToken userToken = new UserToken();
        userToken.setAppId(appId);
        userToken.setLoginName(ak);
        try
        {
            while (off <= watermarkMaxSize)
            {
                n = in.read(buffer, off, len);
                if (n < 0)
                {
                    break;
                }
                off += n;
                len -= n;
            }
            if (off > watermarkMaxSize)
            {
                throw new UploadSizeTooLargeException();
            }
            byte[] data = Arrays.copyOf(buffer, off);
            accountWatermarkService.setWatermarkByAccountId(accountId, data);
        }
        catch (IOException e)
        {
            LOGGER.warn("error occur when read watermark from client", e);
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.SET_WATERMARK_ERR,
                description,
                null);
            throw new InvalidParamException(e);
        }
        catch (UploadSizeTooLargeException e)
        {
            LOGGER.warn("watermark too large!", e);
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.SET_WATERMARK_ERR,
                description,
                null);
            throw e;
            // TODO: handle exception
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.SET_WATERMARK,
            description,
            null);
        response.setHeader("Connection", "close");
    }
    
}
