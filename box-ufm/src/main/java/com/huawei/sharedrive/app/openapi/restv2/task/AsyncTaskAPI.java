/**
 * 
 */
package com.huawei.sharedrive.app.openapi.restv2.task;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.*;
import com.huawei.sharedrive.app.files.service.job.AsyncJobServiceImpl;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.task.RequestAddAsyncTask;
import com.huawei.sharedrive.app.openapi.domain.task.RequestNode;
import com.huawei.sharedrive.app.openapi.domain.task.ResponseAddAsyncTask;
import com.huawei.sharedrive.app.openapi.domain.task.ResponseGetTask;
import com.huawei.sharedrive.app.plugins.scan.manager.SecurityCheckManager;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.security.service.SecurityMethod;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.share.service.LinkService;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
import io.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.common.util.signature.SignatureUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 异步任务接口类
 * 
 * @author l90003768
 * 
 */
@Controller
@RequestMapping(value = "/api/v2/tasks")
@Api(description = "异步任务接口类")
public class AsyncTaskAPI
{
    private static final String TASK_CLEAR_TRASH = "cleanTrash";
    
    private static final String TASK_COPY = "copy";
    
    private static final String TASK_DELETE = "delete";
    
    private static final String TASK_MOVE = "move";
    
    private static final String TASK_RESTORE_TRASH = "restoreTrash";
    
    @Autowired
    private AsyncJobService asyncJobService;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    FolderService folderService;
    
    @Autowired
    private SecurityCheckManager securityCheckManager;
    
    @Autowired
    private LinkService linkService;
    
    @Autowired
    private SecurityMatrixService securityMatrixService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    UserStatisticsService userStatisticsService;
    
    @Autowired
    private FileServiceV2 fileServiceV2;
    
    @RequestMapping(value = "/nodes", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> addTask(@RequestHeader("Authorization") String token,
        @RequestBody RequestAddAsyncTask restReq, HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        UserLogType userLogType = null;
        long fileSizes = 0L;
        long fileCounts = 0L;
        try
        {
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            checkRequest(restReq);
            ResponseAddAsyncTask task;
            String taskType = StringUtils.trimToEmpty(restReq.getType());
            if (TASK_DELETE.equals(taskType))
            {
                userLogType = UserLogType.DELETE_ASYNC_ERR;
                
                // 用户状态校验
                userTokenHelper.checkUserStatus(userToken.getAppId(), restReq.getSrcOwnerId());
                checkNodeList(restReq);
                checkNodesMatrixRight(userToken, restReq, SecurityMethod.NODE_DELETE, request);
                task = addDeleteJob(userToken, restReq);
            }
            else if (TASK_COPY.equals(taskType))
            {
                userLogType = UserLogType.COPY_ASYNC_ERR;
                // DTS2014111006696
                if (restReq.getDestOwnerId() == null)
                {
                    throw new InvalidParamException("destOwnerId is empty");
                }
                // 用户状态校验
                userTokenHelper.checkUserStatus(userToken.getAppId(), restReq.getSrcOwnerId());
                userTokenHelper.checkUserStatus(userToken.getAppId(), restReq.getDestOwnerId());
                checkCopyRequest(restReq);
                this.checkNodesCopyMatrixRight(userToken, restReq, SecurityMethod.NODE_COPY, request);
                
                //对文件进行杀毒判断及启动杀毒
                for(RequestNode rNode:restReq.getSrcNodeList())
                {
                	INode iNode = folderService.getNodeInfoCheckType(userToken, restReq.getSrcOwnerId(), rNode.getSrcNodeId(), INode.TYPE_ALL);
                	securityCheckManager.checkSecurityStatus(iNode, true, true);
                }
                fileBaseService.checkSpaceAndFileCount(restReq.getSrcOwnerId(), userToken.getAccountId(), fileSizes);
                
                this.checkNodesCopyMatrixRight(userToken, restReq, SecurityMethod.NODE_COPY, request);
                // 空间及文件数校验
                fileBaseService.checkSpaceAndFileCount(restReq.getDestOwnerId(), userToken.getAccountId());
                fillLinkUser(request, restReq, userToken);
                task = addCopyJob(userToken, restReq);
                // 刷新数据库
                userStatisticsService.RefreshStatisticsInfoAndCache(restReq.getSrcOwnerId(), fileSizes, fileCounts);
            }
            else if (TASK_MOVE.equals(taskType))
            {
                userLogType = UserLogType.MOVE_ASYNC_ERR;
                
                // DTS2014111006696
                if (restReq.getDestOwnerId() == null)
                {
                    throw new InvalidParamException("destOwnerId is empty");
                }
                // 用户状态校验
                userTokenHelper.checkUserStatus(userToken.getAppId(), restReq.getSrcOwnerId());
                userTokenHelper.checkUserStatus(userToken.getAppId(), restReq.getDestOwnerId());
                
                checkMoveRequest(restReq);
                
                //对文件进行杀毒判断及启动杀毒
                for(RequestNode rNode:restReq.getSrcNodeList())
                {
                	INode iNode = folderService.getNodeInfoCheckType(userToken, restReq.getSrcOwnerId(), rNode.getSrcNodeId(), INode.TYPE_ALL);
                	securityCheckManager.checkSecurityStatus(iNode, true, true);
                }
                
                task = addMoveJob(userToken, restReq);
            }
            else if (TASK_CLEAR_TRASH.equals(taskType))
            {
                userLogType = UserLogType.CLEAN_TRASH_ASYNC_ERR;
                
                // 用户状态校验
                userTokenHelper.checkUserStatus(userToken.getAppId(), restReq.getSrcOwnerId());
                
                task = addCleanTrashJob(userToken, restReq); 
            }
            else if (TASK_RESTORE_TRASH.equals(taskType))
            {
                userLogType = UserLogType.RESTORE_TRASH_ASYNC_ERR;
                
                // 用户状态校验
                userTokenHelper.checkUserStatus(userToken.getAppId(), restReq.getSrcOwnerId());
                
                task = addRestoreTrashJob(userToken, restReq);
            }
            else
            {
                throw new InvalidParamException("Invalid type " + restReq.getType());
            }
            
            return new ResponseEntity<ResponseAddAsyncTask>(task, HttpStatus.CREATED);
        }
        catch (RuntimeException e)
        {
            if (userLogType != null)
            {
                fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    userLogType,
                    null,
                    null);
            }
            throw e;
        }
    }
    
    @RequestMapping(value = "/nodes/{taskId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ResponseGetTask> getTaskStatus(@RequestHeader("Authorization") String token,
        @PathVariable String taskId)
    {
        
        UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
        if (StringUtils.isEmpty(taskId))
        {
            throw new InvalidParamException("task id is empty.");
        }
        ResponseGetTask result = asyncJobService.getTaskStatus(userToken, taskId);
        if (StringUtils.equals(AsyncJobServiceImpl.STATUS_DOING, result.getStatus()))
        {
            return new ResponseEntity<ResponseGetTask>(result, HttpStatus.OK);
        }
        String[] logParams;
        if (StringUtils.equals(AsyncJobServiceImpl.STATUS_NOT_FOUND, result.getStatus()))
        {
            logParams = new String[]{taskId, "success"};
        }
        else
        {
            logParams = new String[]{taskId, result.getStatus()};
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.ASYNC_TASK_STATUS,
            logParams,
            null);
        return new ResponseEntity<ResponseGetTask>(result, HttpStatus.OK);
    }
    
    /**
     * 添加异步清空回收站任务
     * 
     * @param userToken
     * @param requestParam
     * @return
     * @throws Exception
     */
    ResponseAddAsyncTask addCleanTrashJob(UserToken userToken, RequestAddAsyncTask requestParam)
        throws BaseRunException
    {
        String taskId = asyncJobService.asyncCleanTrash(userToken, requestParam);
        ResponseAddAsyncTask resp = new ResponseAddAsyncTask();
        resp.setId(taskId);
        resp.setType(requestParam.getType());
        return resp;
    }
    
    /**
     * 添加异步删除任务
     * 
     * @param userToken
     * @param requestParam
     * @return
     * @throws Exception
     */
    ResponseAddAsyncTask addCopyJob(UserToken userToken, RequestAddAsyncTask requestParam)
        throws BaseRunException
    {
        String taskId = asyncJobService.asyncCopy(userToken, requestParam, true);
        ResponseAddAsyncTask resp = new ResponseAddAsyncTask();
        resp.setId(taskId);
        resp.setType(requestParam.getType());
        return resp;
    }
    
    /**
     * 添加异步删除任务
     * 
     * @param userToken
     * @param requestParam
     * @return
     * @throws Exception
     */
    ResponseAddAsyncTask addDeleteJob(UserToken userToken, RequestAddAsyncTask requestParam)
        throws BaseRunException
    {
        String taskId = asyncJobService.asyncDelete(userToken, requestParam);
        ResponseAddAsyncTask resp = new ResponseAddAsyncTask();
        resp.setId(taskId);
        resp.setType(requestParam.getType());
        return resp;
    }
    
    /**
     * 添加异步删除任务
     * 
     * @param userToken
     * @param requestParam
     * @return
     * @throws Exception
     */
    ResponseAddAsyncTask addMoveJob(UserToken userToken, RequestAddAsyncTask requestParam)
        throws BaseRunException
    {
        checkMoveRequest(requestParam);
        String taskId = asyncJobService.asyncMove(userToken, requestParam);
        ResponseAddAsyncTask resp = new ResponseAddAsyncTask();
        resp.setId(taskId);
        resp.setType(requestParam.getType());
        return resp;
    }
    
    /**
     * 添加异步恢复回收站任务
     * 
     * @param userToken
     * @param requestParam
     * @return
     * @throws Exception
     */
    ResponseAddAsyncTask addRestoreTrashJob(UserToken userToken, RequestAddAsyncTask requestParam)
        throws BaseRunException
    
    {
        String taskId = asyncJobService.asyncRestoreTrash(userToken, requestParam);
        ResponseAddAsyncTask resp = new ResponseAddAsyncTask();
        resp.setId(taskId);
        resp.setType(requestParam.getType());
        return resp;
    }
    
    private void checkCopyRequest(RequestAddAsyncTask requestParam) throws BaseRunException
    {
        if (null == requestParam.getDestOwnerId())
        {
            throw new InvalidParamException("destOwnerId is null");
        }
        if (null == requestParam.getDestFolderId())
        {
            throw new InvalidParamException("destFolderId is null");
        }
        checkNodeList(requestParam);
    }
    
    private void checkMoveRequest(RequestAddAsyncTask requestParam) throws BaseRunException
    {
        checkCopyRequest(requestParam);
    }
    
    private void checkNodeList(RequestAddAsyncTask requestParam)
    {
        if (null == requestParam.getSrcNodeList())
        {
            throw new InvalidParamException("srcNodeList is null");
        }
        if (requestParam.getSrcNodeList().isEmpty())
        {
            throw new InvalidParamException("srcNodeList is empty");
        }
        if (requestParam.getSrcNodeList().get(0) == null)
        {
            throw new InvalidParamException("srcNodeList element is empty");
        }
        if (requestParam.getSrcNodeList().get(0).getSrcNodeId() == null)
        {
            throw new InvalidParamException("srcNodeList element is empty");
        }
    }
    
    private void checkNodesCopyMatrixRight(UserToken userToken, RequestAddAsyncTask restReq,
        SecurityMethod method, HttpServletRequest request) throws BaseRunException
    {
        long ownerId = restReq.getSrcOwnerId();
        Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
        for (RequestNode item : restReq.getSrcNodeList())
        {
            securityMatrixService.checkSecurityMatrix(userToken,
                ownerId,
                item.getSrcNodeId(),
                restReq.getDestOwnerId(),
                method,
                headerCustomMap);
        }
    }
    
    private void checkNodesMatrixRight(UserToken userToken, RequestAddAsyncTask restReq, SecurityMethod method, HttpServletRequest request)
        throws BaseRunException
    {
        long ownerId = restReq.getSrcOwnerId();
        Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
        for (RequestNode item : restReq.getSrcNodeList())
        {
            securityMatrixService.checkSecurityMatrix(userToken, ownerId, item.getSrcNodeId(), method, headerCustomMap);
        }
    }
    
    private void checkRequest(RequestAddAsyncTask restReq)
    {
        if (null == restReq)
        {
            throw new InvalidParamException("RequestAddAsyncTask is null");
        }
        if (restReq.getSrcOwnerId() == null)
        {
            throw new InvalidParamException("srcOwnerId is empty");
        }
        if (null == restReq.getAutoRename())
        {
            restReq.setAutoRename(false);
        }
    }
    
    /**
     * 填充外链用户的身份信息
     * 
     * @param restReq
     * @param userToken
     * @throws BaseRunException
     */
    private void fillLinkUser(HttpServletRequest request, RequestAddAsyncTask restReq, UserToken userToken)
        throws BaseRunException
    {
        if (null != restReq.getLink())
        {
            userToken.setLinkCode(restReq.getLink().getLinkCode());
            if (StringUtils.isNotEmpty(restReq.getLink().getPlainAccessCode()))
            {
                INodeLink nodeLink = linkService.getLinkByLinkCodeForClientV2(restReq.getLink().getLinkCode());
                String dateStr = request.getHeader("Date");

                if (SignatureUtils.getSignature(nodeLink.getPlainAccessCode(), dateStr)
                    .equals(restReq.getLink().getPlainAccessCode()))
                {
                    userToken.setPlainAccessCode(nodeLink.getPlainAccessCode());
                }
                userToken.setDate(request.getHeader("Date"));
            }
            
        }
    }
}
