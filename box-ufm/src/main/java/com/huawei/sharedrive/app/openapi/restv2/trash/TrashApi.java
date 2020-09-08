package com.huawei.sharedrive.app.openapi.restv2.trash;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
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
import com.huawei.sharedrive.app.exception.*;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FolderService;
import com.huawei.sharedrive.app.files.service.TrashServiceV2;
import com.huawei.sharedrive.app.files.service.UserStatisticsService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.ListFolderRequest;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderLists;
import com.huawei.sharedrive.app.openapi.domain.trash.RestoreItemRequest;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.hwcustom.HeaderPacker;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/v2/trash/{ownerId}")
@Api(description = "回收站操作接口")
public class TrashApi
{
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private FolderService folderService;
    
    
    @Autowired
    private TrashServiceV2 trashServiceV2;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    UserStatisticsService userStatisticsService;
    
    @Autowired
    private MessageSource messageSource;

    /**
     * 删除指定回收站内容
     * 
     * @param ownerId
     * @param nodeId
     * @param token
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{nodeId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除指定回收站内容")
    @ResponseBody
    public void deleteTrashItem(@PathVariable Long ownerId, @PathVariable Long nodeId,
        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        INode inode = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, nodeId);
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            // 获取Inode信息,检测文件状态
            inode = fileBaseService.getINodeInfoCheckStatus(ownerId, nodeId, INode.STATUS_TRASH);
            trashServiceV2.deleteTrashItem(userToken, inode);
            // 刷新数据库
            userStatisticsService.RefreshStatisticsInfoAndCache(ownerId, -inode.getSize(), -1);
            
        }
        catch (RuntimeException t)
        {
            String keyword = null;
            String parentId = null;
            if (inode != null)
            {
                keyword = StringUtils.trimToEmpty(inode.getName());
                parentId = String.valueOf(inode.getParentId());
            }
            String[] logParams = new String[]{String.valueOf(ownerId), parentId};
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.DELETE_TRASH_ITEM_ERR,
                logParams,
                keyword);
            throw t;
        }
    }

    /**
     * 清空回收站内容
     *
     * @param ownerId
     * @param nodeId
     * @param token
     * @throws BaseRunException
     */
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @ResponseBody
    public void cleanTrash(@PathVariable Long ownerId, @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException {
        UserToken userToken = null;
        try {
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);

            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);

            //获取删除的文件总大小和数量
            INode node = trashServiceV2.cleanTrash(userToken, ownerId);

            // 刷新数据库
            userStatisticsService.RefreshStatisticsInfoAndCache(ownerId, -node.getSize(), node.getFileCount());
        } catch (RuntimeException t) {
            //此事件没有对应的处理哭
            String[] logParams = new String[]{String.valueOf(ownerId), "all"};
            fileBaseService.sendINodeEvent(userToken, EventType.OTHERS, null, null, UserLogType.DELETE_TRASH_ITEM_ERR, logParams, "trash");
            throw t;
        }
    }

    /**
     * 列举回收站
     * 
     * @param ownerId
     * @param listFolderRequest
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "获得回收站内容列表")
    @ResponseBody
    public ResponseEntity<RestFolderLists> listTrashItems(@PathVariable Long ownerId,
        @RequestBody(required = false) ListFolderRequest listFolderRequest,
        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = null;
        try
        {
            if (null == ownerId)
            {
                String message = "ownerId is null";
                throw new InvalidParamException(message);
            }
            if (listFolderRequest != null)
            {
                listFolderRequest.checkParameter();
            }
            else
            {
                listFolderRequest = new ListFolderRequest();
            }
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(request);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            FileINodesList list = trashServiceV2.listTrashItems(userToken,
                ownerId,
                listFolderRequest.getLimit(),
                listFolderRequest.getOffset(),
                listFolderRequest.getOrder(),
                listFolderRequest.getThumbnail(),
                listFolderRequest.getWithExtraType());
            RestFolderLists folderList = new RestFolderLists(list, userToken.getDeviceType(),messageSource,request.getLocale());
            return new ResponseEntity<RestFolderLists>(folderList, HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            String[] logParams = new String[]{String.valueOf(ownerId), null};
            String keyword = "List Trash Owner:" + ownerId;
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.LIST_TRASH_ITEMS_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
    
    /**
     * 还原指定内容
     * 
     * @param ownerId
     * @param nodeId
     * @param token
     * @param request
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{nodeId}", method = RequestMethod.PUT)
    @ApiOperation(value = "还原指定内容")
    @ResponseBody
    public void restoreTrashItem(@PathVariable Long ownerId, @PathVariable Long nodeId,
        @RequestHeader("Authorization") String token,
        @RequestBody(required = false) RestoreItemRequest request, HttpServletRequest requestServlet) throws BaseRunException
    {
        UserToken userToken = null;
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(ownerId, nodeId);
            if (request != null)
            {
                request.checkParameter();
            }
            else
            {
                request = new RestoreItemRequest();
            }
            Map<String, String> headerCustomMap = HeaderPacker.getCustomHeaderMap(requestServlet);
            userToken = userTokenHelper.checkTokenAndGetUserForV2(token, headerCustomMap);
            
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
            
            INode parent = null;
            if (null != request.getDestFolderId())
            {
                parent = new INode(ownerId, request.getDestFolderId());
            }
            
            INode node = new INode(ownerId, nodeId);
            try
            {
                trashServiceV2.restoreTrashItem(userToken, node, null, parent);
            }
            catch (FilesNameConflictException e)
            {
                if (request.getAutoRename())
                {
                    restoreWithNewName(userToken, ownerId, nodeId, parent);
                }
                else
                {
                    // 如果不自动重命名，则抛出异常
                    throw e;
                }
            }
        }
        catch (RuntimeException t)
        {
            INode nodeLog = fileBaseService.getINodeInfoCheckStatus(ownerId, nodeId, INode.STATUS_TRASH);
            String keyword = null;
            String parentId = null;
            if (nodeLog != null)
            {
                keyword = StringUtils.trimToEmpty(nodeLog.getName());
                parentId = String.valueOf(nodeLog.getParentId());
            }
            String[] logParams = new String[]{String.valueOf(ownerId), parentId};
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.RESTORE_TRASH_ITEM_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
    
    private void restoreWithNewName(UserToken user, Long ownerId, Long nodeId, INode parent)
        throws NumberFormatException, BaseRunException
    {
        String newName = null;
        INode node = folderService.getNodeNoCheckStatus(user, ownerId, nodeId);
        
        int i = 1;
        
        // checkStyle HW7297:MINOR : Dont apply Array Allocation and Object Allocation
        // inside loop
        BaseRunException exception = null;
        while (true)
        {
            newName = FilesCommonUtils.getNewName(node.getType(), node.getName(), i);
            try
            {
                trashServiceV2.restoreTrashItem(user, node, newName, parent);
                break;
            }
            catch (FilesNameConflictException e)
            {
                i++;
            }
            catch (NoSuchParentException e)
            {
                exception = new NoSuchParentException(ErrorCode.NO_SUCH_PARENT.getCode(),
                    ErrorCode.NO_SUCH_PARENT.getMessage());
                throw exception;
            }
        }
    }
}
