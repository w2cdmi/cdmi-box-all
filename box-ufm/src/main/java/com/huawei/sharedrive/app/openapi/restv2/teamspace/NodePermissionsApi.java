package com.huawei.sharedrive.app.openapi.restv2.teamspace;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.app.acl.domain.ACL;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.service.INodeACLManger;
import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestNodePermissionInfo;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;

/**
 * 节点访问控制权限项对外接口
 * 
 * @author t00159390
 * 
 */
@Controller
@RequestMapping(value = "/api/v2/permissions/{ownerId}")
public class NodePermissionsApi
{
    @Autowired
    private INodeACLManger nodeACLManger;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取指定用户对某资源的权限项信息
     * 
     * @param ownerId
     * @param nodeId
     * @param userId
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{nodeId}/{userId}", method = RequestMethod.GET)
    @ResponseBody
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public ResponseEntity<RestNodePermissionInfo> getNodePermissions(@PathVariable Long ownerId,
        @PathVariable Long nodeId, @PathVariable Long userId, @RequestHeader("Authorization") String token,
        String type, HttpServletRequest requestServlet) throws BaseRunException
    {
        UserToken userToken = null;
        try
        {
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(requestServlet);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, nodeId, userId);
            
            ACL iNodeACL = nodeACLManger.getINodePermissionsByUser(userToken,
                ownerId,
                nodeId,
                userId,
                INodeACL.TYPE_USER);
            if (iNodeACL == null)
            {
                throw new BusinessException("Invalid ACL");
            }
            return new ResponseEntity<RestNodePermissionInfo>(new RestNodePermissionInfo(ownerId, nodeId,
                iNodeACL), HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            String keyword = null;
            String loginName = null;
            INode node = fileBaseService.getINodeInfo(ownerId, nodeId);
            User user = userService.get(null, userId);
            if (user != null)
            {
                loginName = user.getLoginName();
            }
            String[] logParams = new String[]{StringUtils.trimToEmpty(loginName), String.valueOf(nodeId)};
            if (node != null)
            {
                keyword = StringUtils.trimToEmpty(node.getName());
            }
            else if (user != null)
            {
                keyword = user.getLoginName();
            }
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_NODE_PERMISSION_ERR,
                logParams,
                keyword);
            throw t;
        }
        
    }
    
    /**
     * 获取指定用户对某资源的权限项信息
     * 
     * @param ownerId
     * @param nodeId
     * @param userId
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{nodeId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<RestNodePermissionInfo> getNodePermissions(@PathVariable Long ownerId,
        @PathVariable Long nodeId, @RequestHeader("Authorization") String token, HttpServletRequest request)
        throws BaseRunException
    {
        UserToken userToken = null;
        try
        {
            ACL iNodeACL = null;
            
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, nodeId);
            if (token.startsWith(UserTokenHelper.LINK_PREFIX))
            {
                String date = request.getHeader("Date");
                userToken = userTokenHelper.getLinkToken(token, date);
                userTokenHelper.assembleUserToken(ownerId, userToken);
                iNodeACL = nodeACLManger.getINodePermissionsByLink(userToken,
                    ownerId,
                    nodeId,
                    userToken.getLinkCode());
            }
            else
            {
                Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
                userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
                iNodeACL = nodeACLManger.getINodePermissionsByUser(userToken,
                    ownerId,
                    nodeId,
                    userToken.getId(),
                    INodeACL.TYPE_USER);
            }
            
            if (iNodeACL == null)
            {
                throw new BusinessException("Invalid ACL");
            }
            return new ResponseEntity<RestNodePermissionInfo>(new RestNodePermissionInfo(ownerId, nodeId,
                iNodeACL), HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            String keyword = null;
            String loginName = null;
            INode node = fileBaseService.getINodeInfo(ownerId, nodeId);
            if (userToken != null)
            {
                if (StringUtils.isBlank(userToken.getLinkCode()))
                {
                    loginName = userToken.getLoginName();
                }
                else
                {
                    loginName = userToken.getLinkCode();
                }
            }
            String[] logParams = new String[]{StringUtils.trimToEmpty(loginName), String.valueOf(nodeId)};
            if (node != null)
            {
                keyword = StringUtils.trimToEmpty(node.getName());
            }
            else
            {
                keyword = loginName;
            }
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_NODE_PERMISSION_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
}
