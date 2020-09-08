package com.huawei.sharedrive.app.openapi.restv2.file;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jms.Connection;
import javax.servlet.http.HttpServletResponse;

import com.huawei.sharedrive.app.authapp.service.AuthAppService;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.dataserver.service.DCUrlManager;
import com.huawei.sharedrive.app.exception.*;
import com.huawei.sharedrive.app.oauth2.service.Authorize;
import com.huawei.sharedrive.app.user.domain.User;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.dao.ObjectReferenceDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.ObjectDownloadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.PreviewObjectPreUploadRequest;
import com.huawei.sharedrive.app.openapi.domain.node.PreviewObjectPreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.SetObjectAttributeRequest;
import com.huawei.sharedrive.app.plugins.preview.domain.PreviewObject;
import com.huawei.sharedrive.app.plugins.preview.manager.FilePreviewManager;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityStatus;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityType;
import com.huawei.sharedrive.app.plugins.scan.manager.SecurityScanManager;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.Constants;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.uam.domain.AuthApp;

/**
 * 非标准接口,用于通过对象ID操作数据
 * 
 * @author q90003805
 * 
 */
@Controller
@RequestMapping(value = "/api/v2/objects")
@Api(description = "非标准接口,用于通过对象ID操作数据")
public class ObjectAPI
{
    private static final Set<String> DOWNLOAD_ALLOWED_APPID_SET = new HashSet<String>(
        BusinessConstants.INITIAL_CAPACITIES);
    
    static
    {
        DOWNLOAD_ALLOWED_APPID_SET.add(Constants.APPID_PPREVIEW);
        DOWNLOAD_ALLOWED_APPID_SET.add(Constants.APPID_SECURITYSCAN);
    }
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private FilePreviewManager filePreviewManager;
    
    @Autowired
    private SecurityScanManager securityScanManager;
    
    @Autowired
    private ObjectReferenceDAO objectReferenceDAO;
    
	@Autowired
	private INodeDAO iNodeDAO;

    @Autowired
    private AuthAppService authAppService;

    @Autowired
    private DCUrlManager dcUrlManager;

    /**
     * 下载对象内容
     * 
     * @param objectId
     * @param authorization
     * @param date
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{objectId}/contents", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getDownloadObj(@PathVariable("objectId") String objectId,
        @RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date)
        throws BaseRunException
    {
        String appId = userTokenHelper.checkAppSystemToken(authorization, date);
        String[] logMsgs = new String[]{String.valueOf(objectId)};
        String[] akArr = authorization.split(",");
        UserToken userToken = new UserToken();
        userToken.setAppId(appId);
        userToken.setLoginName(akArr[1]);
        if (!DOWNLOAD_ALLOWED_APPID_SET.contains(appId))
        {
            throw new ForbiddenException("app " + appId + " is not allowed to access this method");
        }
        
        // 安全扫描
        String downLoadUrl;
        try
        {
            checkScanStatus(objectId);
            
            downLoadUrl = buildObjectDownloadDownURL(appId, objectId).getDownloadUrl();
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_DOWNLOAD_OBJ_ERR,
                logMsgs,
                null);
            throw e;
        }
        
        // 发送日志
        
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_DOWNLOAD_OBJ,
            logMsgs,
            null);
        // 设置重定向到数据中心
        HttpHeaders header = new HttpHeaders();
        header.set("Connection", "close");
        header.set("Location", downLoadUrl);
        // 返回307响应码
        return new ResponseEntity<String>(null, header, HttpStatus.TEMPORARY_REDIRECT);
    }

    private DataAccessURLInfo buildObjectDownloadDownURL(String appId, String objectId)
            throws BaseRunException
    {
        // 获取QOS端口
        AuthApp authApp = authAppService.getByAuthAppID(appId);
        Integer qosPort = authApp != null ? authApp.getQosPort() : null;
        ObjectReference objectReference = objectReferenceDAO.get(objectId);
        if (objectReference == null)
        {
            String message = "File not exist, objectId id:" + objectId;
            throw new NoSuchFileException(message);
        }
        // 获取资源组信息
        DataAccessURLInfo urlinfo = dcUrlManager.getDownloadURL(objectReference.getResourceGroupId(), qosPort);
        String nodeLastModified = DateUtils.dateToString(DateUtils.RFC822_DATE_FORMAT, new Date());
        UserToken token = userTokenHelper.createTokenDataServer(User.SYSTEM_USER_ID,
                objectId,
                Authorize.AuthorityMethod.GET_OBJECT,
                0,
                nodeLastModified);
        urlinfo.setDownloadUrl(
                urlinfo.getDownloadUrl() + token.getToken() + "/" + objectId + "/" + objectId);
        return urlinfo;
    }

    /**
     * 上传预览对象
     * 
     * @param request
     * @param sourceObjectId
     * @param authorization
     * @param date
     * @return
     * @throws BaseRunException
     */
/*    @Deprecated
    @RequestMapping(value = "/{objectId}/preview", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<PreviewObjectPreUploadResponse> preUploadPreviewObj(
        @RequestBody PreviewObjectPreUploadRequest request, @PathVariable("objectId") String sourceObjectId,
        @RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date)
        throws BaseRunException
    {
        String appId = userTokenHelper.checkAppSystemToken(authorization, date);
        
        String[] logMsgs = new String[]{String.valueOf(sourceObjectId)};
        String[] akArr = authorization.split(",");
        UserToken userToken = new UserToken();
        userToken.setAppId(appId);
        userToken.setLoginName(akArr[1]);
        PreviewObject object = request.transToPreviewObject();
        PreviewObjectPreUploadResponse rsp = null;
        try
        {
            if (!Constants.APPID_PPREVIEW.equals(appId))
            {
                throw new ForbiddenException("app " + appId + " is not allowed to access this method");
            }
            rsp = filePreviewManager.preUploadPreviewObject(appId,
                sourceObjectId,
                object.getAccountId(),
                object.getCreatedAt());
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.PRE_UPLOAD_PREVIEW_ERR,
                logMsgs,
                null);
            throw e;
            
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.PRE_UPLOAD_PREVIEW,
            logMsgs,
            null);
        HttpHeaders header = new HttpHeaders();
        header.set("Connection", "close");
        
        ResponseEntity<PreviewObjectPreUploadResponse> response = new ResponseEntity<PreviewObjectPreUploadResponse>(
            rsp, header, HttpStatus.OK);
        
        return response;
    }
    */
    /**
     * 设置对象属性
     * 
     * @param request
     * @param objectId
     * @param authorization
     * @param date
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{objectId}/attributes", method = RequestMethod.PUT)
    @ResponseBody
    public void setOjbectAttribute(@RequestBody SetObjectAttributeRequest request,
        @PathVariable("objectId") String objectId, @RequestHeader("Authorization") String authorization,
        @RequestHeader("Date") String date) throws BaseRunException
    {
        String appId = userTokenHelper.checkAppSystemToken(authorization, date);
        if (!Constants.APPID_SECURITYSCAN.equals(appId))
        {
            throw new ForbiddenException("app " + appId + " is not allowed to access this method");
        }
        
        request.checkParameter();
        
        SecurityType securityType = SecurityType.getSecurityType(request.getName());
        if (securityType == null)
        {
            throw new InvalidParamException("Invalid attribute " + request.getName());
        }
        String[] logMsgs = new String[]{String.valueOf(objectId)};
        String[] akArr = authorization.split(",");
        UserToken userToken = new UserToken();
        userToken.setAppId(appId);
        userToken.setLoginName(akArr[1]);
        try
        {
            if (securityType == SecurityType.CONFIDENTIAL)
            {
               // int securityLabel = getSecurityLabel(request.getValue());
            	int securityLabel = Integer.parseInt(request.getValue());
                securityScanManager.updateSecurityLabel(securityType, securityLabel, objectId);
            }
            else
            {
                throw new InvalidParamException("Invalid attribute " + request.getName());
            }
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.SET_ATTRIBURE_OBJ_ERR,
                logMsgs,
                null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.SET_ATTRIBURE_OBJ,
            logMsgs,
            null);
    }
    
    /**
     * 标识预览转换失败
     * 
     * @param request
     * @param sourceObjectId
     * @param authorization
     * @param date
     * @throws BaseRunException
     */
/*    @Deprecated
    @RequestMapping(value = "/{objectId}/preview/failed", method = RequestMethod.PUT)
    @ResponseBody
    public void updatePreviewConvertFailed(@RequestBody PreviewObjectPreUploadRequest request,
        @PathVariable("objectId") String sourceObjectId,
        @RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date,
        HttpServletResponse response) throws BaseRunException
    {
        response.setHeader("Connection", "close");
        String appId = userTokenHelper.checkAppSystemToken(authorization, date);
        String[] logMsgs = new String[]{String.valueOf(sourceObjectId)};
        String[] akArr = authorization.split(",");
        UserToken userToken = new UserToken();
        userToken.setAppId(appId);
        userToken.setLoginName(akArr[1]);
        try
        {
            if (!Constants.APPID_PPREVIEW.equals(appId))
            {
                throw new ForbiddenException("app " + appId + " is not allowed to access this method");
            }
            PreviewObject object = request.transToPreviewObject();
            
            filePreviewManager.updatePreviewObjectFailed(sourceObjectId,
                object.getAccountId(),
                object.getCreatedAt());
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.PREVIEW_CONVERT,
                logMsgs,
                null);
            throw e;
        }
        
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.PREVIEW_CONVERT,
            logMsgs,
            null);
        
    }
    */
    private void checkScanStatus(String objectId)
    {
        if (!securityScanManager.isSecurityScanEnable())
        {
            return;
        }
        
        int secLabel = securityScanManager.getSecurityStatus(objectId);
        if (secLabel == 0)
        {
            return;
        }
        SecurityStatus status = SecurityStatus.getSecurityStatus(secLabel);
        switch (status)
        {
            case KIA_UMCOMPLETED:
                break;
            case KIA_COMPLETED_INSECURE:
                throw new ScannedForbiddenException("This file is not allowed to be downloaded");
            default:
                break;
        }
        SecurityStatus status2 = SecurityStatus.getKsoftSecurityStatus(secLabel);
        switch (status2)
        {
            case KSOFT_UMCOMPLETED:
                break;
            case KSOFT_COMPLETED_INSECURE:
                throw new ScannedForbiddenException("This file is not allowed to be downloaded");
            default:
                break;
        }
    }
    
    private void checkScanStatusByNode(INode node)
    {
        if (!securityScanManager.isSecurityScanEnable())
        {
            return;
        }
        
        int secLabel = Integer.parseInt(String.valueOf(node.getKiaLabel()));
        if (secLabel == 0)
        {
            return;
        }
        SecurityStatus status = SecurityStatus.getSecurityStatus(secLabel);
        switch (status)
        {
            case KIA_UMCOMPLETED:
                break;
            case KIA_COMPLETED_INSECURE:
                throw new ScannedForbiddenException("This file is not allowed to be downloaded");
            default:
                break;
        }
        SecurityStatus status2 = SecurityStatus.getKsoftSecurityStatus(secLabel);
        switch (status2)
        {
            case KSOFT_UMCOMPLETED:
                break;
            case KSOFT_COMPLETED_INSECURE:
                throw new ScannedForbiddenException("This file is not allowed to be downloaded");
            default:
                break;
        }
    }
    
/*
    @Deprecated
    @RequestMapping(value = "/{objectId}/downloadUrl", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ObjectDownloadResponse> getDownloadUrl(@PathVariable("objectId") String objectId,
                                                                 @RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date)
        throws BaseRunException
    {
        String appId = userTokenHelper.checkAppSystemToken(authorization, date);
        String[] logMsgs = new String[]{String.valueOf(objectId)};
        String[] akArr = authorization.split(",");
        UserToken userToken = new UserToken();
        userToken.setAppId(appId);
        userToken.setLoginName(akArr[1]);
        if (!DOWNLOAD_ALLOWED_APPID_SET.contains(appId))
        {
            throw new ForbiddenException("app " + appId + " is not allowed to access this method");
        }
        
        // 安全扫描
        String downLoadUrl;
        try
        {
            checkScanStatus(objectId);
            
            downLoadUrl = filePreviewManager.getSourceObjectDownloadUrl(appId, objectId);
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_DOWNLOAD_OBJ_ERR,
                logMsgs,
                null);
            throw e;
        }
        
        // 发送日志
        
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_DOWNLOAD_OBJ,
            logMsgs,
            null);
        HttpHeaders header = new HttpHeaders();
        header.set("Connection", "close");
        ObjectReference objectReference = objectReferenceDAO.get(objectId);
        ObjectDownloadResponse response = new ObjectDownloadResponse(downLoadUrl, objectReference.getSize());
        return new ResponseEntity<ObjectDownloadResponse>(response, header, HttpStatus.OK);
    }
*/

/*    @Deprecated
    @RequestMapping(value = "/{objectId}/{ownerId}/{nodeId}/downloadUrlForVirusForScaner", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ObjectDownloadResponse> getDownloadUrlForScaner(@PathVariable("objectId") String objectId,
                                                                          @PathVariable("ownerId") String ownerId, @PathVariable("nodeId") String nodeId,
                                                                          @RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date)
        throws BaseRunException
    {
        String appId = userTokenHelper.checkAppSystemToken(authorization, date);
        String[] logMsgs = new String[]{String.valueOf(objectId)};
        String[] akArr = authorization.split(",");
        UserToken userToken = new UserToken();
        userToken.setAppId(appId);
        userToken.setLoginName(akArr[1]);
        if (!DOWNLOAD_ALLOWED_APPID_SET.contains(appId))
        {
            throw new ForbiddenException("app " + appId + " is not allowed to access this method");
        }
        
        // 安全扫描
        String downLoadUrl;
        try
        {
        	INode node = iNodeDAO.get(Long.parseLong(ownerId), Long.parseLong(nodeId));
        	checkScanStatusByNode(node);
            
            downLoadUrl = filePreviewManager.getSourceObjectDownloadUrl(appId, objectId);
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null, 
                UserLogType.GET_DOWNLOAD_OBJ_ERR,
                logMsgs,
                null);
            throw e;
        }
        
        // 发送日志
        
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_DOWNLOAD_OBJ,
            logMsgs,
            null);
        HttpHeaders header = new HttpHeaders();
        header.set("Connection", "close");
        ObjectReference objectReference = objectReferenceDAO.get(objectId);
        ObjectDownloadResponse response = new ObjectDownloadResponse(downLoadUrl, objectReference.getSize());
        return new ResponseEntity<ObjectDownloadResponse>(response, header, HttpStatus.OK);
    }
    */
}
