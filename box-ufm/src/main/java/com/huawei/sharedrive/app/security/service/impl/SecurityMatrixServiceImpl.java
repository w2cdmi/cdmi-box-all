package com.huawei.sharedrive.app.security.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.authapp.service.AuthAppService;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.exception.NoSuchUserException;
import com.huawei.sharedrive.app.exception.SecurityMatixException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.security.RestSceurityAccreditRequest;
import com.huawei.sharedrive.app.openapi.domain.security.RestSecurityAccreditResponse;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityScanTask;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityStatus;
import com.huawei.sharedrive.app.plugins.scan.manager.SecurityScanManager;
import com.huawei.sharedrive.app.security.client.SecurityRestClient;
import com.huawei.sharedrive.app.security.domain.CheckEngine;
import com.huawei.sharedrive.app.security.domain.Operation;
import com.huawei.sharedrive.app.security.service.SecurityMatrixHelper;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.security.service.SecurityMethod;
import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.Constants;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.uam.domain.AuthApp;

@Component
@Service("securityMatrixService")
public class SecurityMatrixServiceImpl implements SecurityMatrixService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityMatrixServiceImpl.class);
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;

    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Resource
    private RestClient uamClientService;
    
    @Resource
    private TeamSpaceService teamSpaceService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SecurityRestClient securityRestClient;
    
    @Autowired
    private SecurityScanManager securityScanManager;
    
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public void checkSecurityMatrix(UserToken userToken, Long ownerId, Long nodeId, Long destOwnerId,
        SecurityMethod method, Map<String, String> headerCustomMap) throws BaseRunException
    {
        if (ownerId.equals(destOwnerId))
        {
            return;
        }
        
        if (!isSecurityMatrixEnable())
        {
            return;
        }
        
        INode node = getNode(ownerId, nodeId);
        
        CheckEngine engine = getCheckEngine();
        LOGGER.info("Security matrix check engine: {}", engine);
        if (CheckEngine.HUAWEI == engine)
        {
            checkForHuawei(userToken, ownerId, destOwnerId, method, node, headerCustomMap);
        }
        else
        {
            // TODO 外链鉴权
            if (StringUtils.isNotBlank(userToken.getLinkCode()) && StringUtils.isBlank(userToken.getToken()))
            {
                return;
            }
            int spaceSecRoleId = getUserSecurityId(userToken, ownerId);
            int targetSecRoleId = 0;
            if (destOwnerId != null)
            {
                targetSecRoleId = getUserSecurityId(userToken, destOwnerId);
            }
            standardCheck(userToken,
                node.getSecurityId(),
                spaceSecRoleId,
                targetSecRoleId,
                transToOperation(method));
        }
    }
    
    @Override
    public void checkSecurityMatrix(UserToken userToken, Long ownerId, Long nodeId, SecurityMethod method,
        Map<String, String> headerCustomMap) throws BaseRunException
    {
        if (!isSecurityMatrixEnable())
        {
            return;
        }
        
        INode node = getNode(ownerId, nodeId);
        CheckEngine engine = getCheckEngine();
        LOGGER.info("Security matrix check engine: {}", engine);
        
        if (engine == CheckEngine.HUAWEI)
        {
            checkForHuawei(userToken, ownerId, method, node, headerCustomMap);
        }
        else
        {
            // TODO 外链鉴权
            if (StringUtils.isNotBlank(userToken.getLinkCode()) && StringUtils.isBlank(userToken.getToken()))
            {
                return;
            }
            // 这里的spaceSecRoleId就是取的user_account_xxx 表中的roleid 
            int spaceSecRoleId = getUserSecurityId(userToken, ownerId);
            
            standardCheck(userToken, node.getSecurityId(), spaceSecRoleId, transToOperation(method));
        }
        
    }
    
    @Override
    public byte getFileSecurityId(UserToken userToken)
    {
        String ip = SecurityMatrixHelper.getRealIP();
        Byte securityId = securityRestClient.getFileSecurityId(userToken, ip);
        LOGGER.info("Get file security id. User:{}, real ip:{}, security id:{}",
            userToken.getCloudUserId(),
            ip,
            securityId);
        return securityId == null ? INode.SECURITY_ID_UNSET : securityId;
    }
    
    @Override
    public int getUserSecurityId(UserToken userToken, long ownerId)
    {
        User user = userService.get(ownerId);
        if (user == null)
        {
            throw new SecurityMatixException("checkSecurityMatrix return false, user is null");
        }
        
        if (user.getSecurityId() != User.SECURITY_ID_UNSET)
        {
            return user.getSecurityId();
        }
        
        Integer securityId = null;
        if (user.getType() == User.USER_TYPE_TEAMSPACE)
        {
            TeamSpace teamspace = teamSpaceService.getTeamSpaceNoCheck(ownerId);
            
            if (teamspace == null)
            {
                throw new SecurityMatixException("checkSecurityMatrix return false, teamspace is null");
            }
            user = userService.get(teamspace.getOwnerBy());
            
            if (user == null)
            {
                throw new SecurityMatixException("checkSecurityMatrix return false, user is null");
            }
            
        }
        securityId = securityRestClient.getUserSecurityId(userToken, user);
        
        if (securityId != null)
        {
            userService.updateSecurityId(user.getAccountId(), user.getId(), securityId);
            return securityId;
        }
        
        return User.SECURITY_ID_UNSET;
    }
    
    @Override
    public boolean isSecurityMatrixEnable()
    {
        try
        {
            SystemConfig config = systemConfigDAO.get("matrix.security.check");
            if (config != null)
            {
                return Boolean.parseBoolean(config.getValue());
            }
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage());
        }
        return false;
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void check(UserToken userToken, INode node, Long destOwnerId, SecurityMethod method,
        SecurityStatus status, Map<String, String> headerCustomMap) throws SecurityMatixException
    {
        long begain = System.currentTimeMillis();
        String authUrl = null;
        String appId = userToken.getAppId();
        AuthApp authApp = authAppService.getByAuthAppID(appId);
        if (authApp != null)
        {
            authUrl = authApp.getAuthUrl();
        }
        if (StringUtils.isEmpty(authUrl))
        {
            // 兼容华为云盘版本，优化URL调用
            authUrl = PropertiesUtils.getProperty("token.check.address.uam", null);
        }
        
        if (StringUtils.isEmpty(authUrl))
        {
            User user = userService.get(node.getOwnedBy());
            if (user == null)
            {
                throw new NoSuchUserException("can not found user " + node.getOwnedBy());
            }
            authApp = authAppService.getByAuthAppID(user.getAppId());
            if (authApp != null)
            {
                authUrl = authApp.getAuthUrl();
            }
        }
        
        if (StringUtils.isEmpty(authUrl))
        {
            throw new SecurityMatixException("authUrl is null");
        }
        
        if (!"/".equals(authUrl.substring(authUrl.length() - 1, authUrl.length())))
        {
            authUrl = authUrl + '/';
        }
        
        Map<String, String> headerMap = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        headerMap.put("Authorization", userToken.getToken());
        if(null != headerCustomMap)
        {
            headerMap.putAll(headerCustomMap);
        }
        String[] permissions = {method.name()};
        RestSceurityAccreditRequest request = new RestSceurityAccreditRequest();
        request.setOnwerCloudUserId(node.getOwnedBy());
        request.setiNodeId(node.getId());
        if (destOwnerId != null)
        {
            request.setTargetOwnerId(destOwnerId);
        }
        request.setType(node.getType());
        request.setPermissions(permissions);
        
        request.setKiaStatusBySecurityStatus(status);
        
        if (StringUtils.isEmpty(SecurityMatrixHelper.getRealIP()))
        {
            LOGGER.debug("x-real-ip is null");
        }
        else
        {
            headerMap.put(Constants.HTTP_X_REAL_IP, SecurityMatrixHelper.getRealIP());
        }
        
        TextResponse response = uamClientService.performJsonPostTextResponseByUri(authUrl
            + "api/v2/security/accredit", headerMap, request);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            RestSecurityAccreditResponse result = JsonUtils.stringToObject(content,
                RestSecurityAccreditResponse.class);
            
            LOGGER.info("End [ " + response.getStatusCode() + ' ' + (System.currentTimeMillis() - begain)
                + " ]");
            if (result == null || !result.isCanAccess())
            {
                throw new SecurityMatixException("checkSecurityMatrix return false, oper:" + method.name());
            }
            return;
        }
        
        LOGGER.info("End [ " + response.getStatusCode() + ' ' + (System.currentTimeMillis() - begain) + " ]");
        
        throw new SecurityMatixException("checkSecurityMatrix failed, " + response.getResponseBody());
    }
    
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void checkForHuawei(UserToken userToken, Long ownerId, Long destOwnerId, SecurityMethod method,
        INode node, Map<String, String> headerCustomMap)
    {
        // 如果是团队空间的资源，根据安全矩阵需要，要替换成团队空间拥有者的clouduserid
        TeamSpace teamspace = teamSpaceService.getTeamSpaceNoCheck(ownerId);
        if (teamspace != null)
        {
            node.setOwnedBy(teamspace.getOwnerBy());
        }
        
        if (destOwnerId != null)
        {
            teamspace = teamSpaceService.getTeamSpaceNoCheck(destOwnerId);
            if (teamspace != null)
            {
                destOwnerId = teamspace.getOwnerBy();
            }
        }
        
        SecurityStatus status = null;
        
        // 除文件夹列举和创建操作，其他都都要做安全标示
        if (StringUtils.isNotBlank(node.getObjectId()) && SecurityMethod.FOLDER_LIST != method
            && SecurityMethod.FOLDER_CREATE != method)
        {
            int kLabel = securityScanManager.sendScanTask(node, SecurityScanTask.PRIORITY_HIGH);
            status = SecurityStatus.getSecurityStatus(kLabel);
            LOGGER.info("SecurityStatus: {} ", status);
        }
        
        check(userToken, node, destOwnerId, method, status, headerCustomMap);
    }
    
    private void checkForHuawei(UserToken userToken, Long ownerId, SecurityMethod method, INode node, Map<String, String> headerCustomMap)
    {
        // 如果是团队空间的资源，根据安全矩阵需要，要替换成团队空间拥有者的clouduserid
        TeamSpace teamspace = teamSpaceService.getTeamSpaceNoCheck(ownerId);
        if (teamspace != null)
        {
            node.setOwnedBy(teamspace.getOwnerBy());
        }
        
        SecurityStatus status = null;
        
        if (StringUtils.isNotBlank(node.getObjectId()) && node.getStatus() != INode.STATUS_CREATING
            && SecurityMethod.FOLDER_LIST != method && SecurityMethod.FOLDER_CREATE != method)
        {
        	int kLabel = securityScanManager.sendScanTask(node, SecurityScanTask.PRIORITY_HIGH);
            status = SecurityStatus.getSecurityStatus(kLabel);
            LOGGER.info("SecurityStatus: {} ", status);
        }
        
        check(userToken, node, null, method, status, headerCustomMap);
    }
    
    private void doStandardCheck(UserToken userToken, Operation operation, byte securityId,
        int spaceSecRoleId, Integer targetSecRoleId)
    {
        Boolean isAllowed = securityRestClient.isOperationAllowed(operation,
            securityId,
            spaceSecRoleId,
            targetSecRoleId,
            userToken);
        if (isAllowed == null || !isAllowed)
        {
            LOGGER.info("checkSecurityMatrix failed, user:{}, operation:{}, security id:{}",
                userToken.getCloudUserId(),
                operation,
                securityId);
            throw new SecurityMatixException(
                "checkSecurityMatrix failed, user:{}, operation:{}, security id:{}");
        }
    }
    
    /**
     * 获取安全矩阵校验方式
     * 
     * @return
     */
    @Override
    public CheckEngine getCheckEngine()
    {
        SystemConfig systemConfig = systemConfigDAO.get("matrix.security.check.engine");
        CheckEngine engine = systemConfig == null ? CheckEngine.STANDARD
            : CheckEngine.getCheckEngine(systemConfig.getValue());
        return engine;
    }
    
    private INode getNode(Long ownerId, Long nodeId) throws BaseRunException
    {
        INode node = null;
        if (nodeId == null || nodeId == INode.FILES_ROOT)
        {
            node = new INode();
            node.setOwnedBy(ownerId);
        }
        else
        {
            node = fileBaseService.getINodeInfo(ownerId, nodeId);
            if (null == node)
            {
                String msg = " node not exist, owner id :" + ownerId + ",node id: " + nodeId;
                throw new NoSuchItemsException(msg);
            }
        }
        return node;
    }
    
    private void standardCheck(UserToken userToken, byte securityId, int spaceSecRoleId, int targetSecRoleId,
        Operation operation) throws BaseRunException
    {
        LOGGER.info("Security matrix standard check. user id:{}, security id:{} , space role id: {} , target role id: {}, operation: {}",
            userToken.getCloudUserId(),
            securityId,
            spaceSecRoleId,
            targetSecRoleId,
            operation);
        if (operation == null)
        {
            return;
        }
        doStandardCheck(userToken, operation, securityId, spaceSecRoleId, targetSecRoleId);
        
    }
    
    private void standardCheck(UserToken userToken, byte securityId, int spaceSecRoleId, Operation operation)
        throws BaseRunException
    {
        LOGGER.info("Security matrix standard check. user id:{}, security id:{} , space role id: {}, operation: {}",
            userToken.getCloudUserId(),
            securityId,
            spaceSecRoleId,
            operation);
        if (operation == null)
        {
            return;
        }
        doStandardCheck(userToken, operation, securityId, spaceSecRoleId, null);
        
    }
    
    private Operation transToOperation(SecurityMethod method)
    {
        switch (method)
        {
            case FOLDER_LIST:
                return Operation.BROWSER;
            case NODE_COPY:
                return Operation.COPY;
            case FOLDER_CREATE:
                return Operation.CREATE;
            case NODE_DELETE:
                return Operation.DELETE;
            case FILE_DOWNLOAD:
                return Operation.DOWNLOAD;
            case NODE_SETLINK:
                return Operation.LINK;
            case FILE_PREVIEW:
                return Operation.PREVIEW;
            case NODE_SETSHARE:
                return Operation.SHARE;
            case FILE_UPLOAD:
                return Operation.UPLOAD;
            case NODE_INFO:
            case NODE_RENAME:
                return null;
            default:
                throw new InvalidParamException("Unknown operation " + method);
        }
    }
    
}
