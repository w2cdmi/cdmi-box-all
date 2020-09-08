package com.huawei.sharedrive.app.openapi.restv2.folder;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.NoSuchFolderException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FolderService;
import com.huawei.sharedrive.app.files.service.FolderServiceV2;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.ListFolderRequest;
import com.huawei.sharedrive.app.openapi.domain.node.RestFileInfo;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderLists;
import com.huawei.sharedrive.app.openapi.rest.FilesCommonApi;
import com.huawei.sharedrive.app.openapi.restv2.folder.v1vo.RestFileInfoV1;
import com.huawei.sharedrive.app.openapi.restv2.folder.v1vo.RestFolderInfoV1;
import com.huawei.sharedrive.app.openapi.restv2.folder.v1vo.RestFolderListsV1;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.security.service.SecurityMethod;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

import pw.cdmi.box.domain.Order;

/**
 * 文件夹API Rest接口, 提供文件夹列举, 复制, 移动, 删除等操作
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-4-30
 * @see
 * @since
 */
@Controller
@RequestMapping(value = "/api/v1/folders/{ownerId}")
public class FoldeV1rApi extends FilesCommonApi
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FoldeV1rApi.class);
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private FolderService folderService;
    
    @Autowired
    private FolderServiceV2 folderServiceV2;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private SecurityMatrixService securityMatrixService;
//    
//    /**
//     * 
//     * 列举文件夹
//     * 
//     * @param ownerId
//     * @param folderId
//     * @param offset
//     * @param limit
//     * @param orderby
//     * @param des
//     * @param token
//     * @return
//     * @throws BaseRunException
//     */
//    @RequestMapping(value = "/{folderId}/items", method = RequestMethod.GET)
//    @ResponseBody
//    @SuppressWarnings("PMD.ExcessiveParameterList")
//    public ResponseEntity<RestFolderListsV1> listFolderbyFolderID(@PathVariable Long ownerId,
//        @PathVariable Long folderId, Long offset, Integer limit, String orderby, String des,
//        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
//    {/*
//        UserToken userToken = null;
//        INode node = null;
//        try
//        {
//            
//            FilesCommonUtils.checkNonNegativeIntegers(ownerId, folderId);
//            ListFolderRequest listFolderRequest = new ListFolderRequest();
//            listFolderRequest.setLimit(limit);
//            listFolderRequest.setOffset(offset);
//            if(StringUtils.isNotEmpty(orderby))
//            {
//                List<Order> orderlist = new ArrayList<Order>(1);
//                Order order = new Order();
//                order.setField(orderby);
//                if(StringUtils.equalsIgnoreCase(des, "des"))
//                {
//                    order.setDesc(true);
//                }
//                else
//                {
//                    order.setDesc(false);
//                }
//                orderlist.add(order);
//                listFolderRequest.setOrder(orderlist);
//                List<Thumbnail> thumbnailList = new ArrayList<Thumbnail>(1);
//                Thumbnail tempThumbnail = new Thumbnail();
//                tempThumbnail.setHeight(32);
//                tempThumbnail.setWidth(32);
//                thumbnailList.add(tempThumbnail);
//                listFolderRequest.setThumbnail(thumbnailList);
//            }
//            if (token.startsWith(UserTokenHelper.LINK_PREFIX))
//            {
//                String date = request.getHeader("Date");
//                userToken = userTokenHelper.getLinkToken(token, date);
//                userTokenHelper.assembleUserToken(ownerId, userToken);
//            }
//            else
//            {
//                userToken = userTokenHelper.checkTokenAndGetUserForV2(token, null);
//            }
//            
//            // 用户状态校验
//            userTokenHelper.checkUserStatus(userToken.getAppId(), ownerId);
//            
//            securityMatrixService.checkSecurityMatrix(userToken,
//                ownerId,
//                folderId,
//                SecurityMethod.FOLDER_LIST,
//                null);
//            List<Order> orderList = listFolderRequest.getOrder();
//            List<Thumbnail> thumbnailList = listFolderRequest.getThumbnail();
//            if (folderId == INode.FILES_ROOT)
//            {
//                node = new INode(ownerId, folderId);
//            }
//            else
//            {
//                try
//                {
//                    node = folderService.getNodeInfo(userToken, ownerId, folderId);
//                }
//                catch (NoSuchItemsException e)
//                {
//                    LOGGER.error("Folder not exists [ " + ownerId + "; " + folderId + " ]");
//                    throw new NoSuchFolderException(e);
//                }
//                if (null == node)
//                {
//                    LOGGER.error("Folder not exists [ " + ownerId + "; " + folderId + " ]");
//                    throw new NoSuchFolderException();
//                }
//            }
//            node.setStatus(INode.STATUS_NORMAL);
//            FileINodesList reList = folderServiceV2.listNodesByFilter(userToken,
//                node,
//                offset,
//                limit,
//                orderList,
//                thumbnailList,
//                listFolderRequest.getWithExtraType(),
//                null);
//            RestFolderLists folderList = new RestFolderLists(reList, userToken.getDeviceType());
//            fillListUserInfo(folderList);
//            RestFolderListsV1 result = convertTo(folderList, userToken.getDeviceType());
//            return new ResponseEntity<RestFolderListsV1>(result, HttpStatus.OK);
//        }
//        catch (BaseRunException t)
//        {
//            if (node != null)
//            {
//                String keyword = StringUtils.trimToEmpty(node.getName());
//                String[] logParams = new String[]{String.valueOf(ownerId),
//                    String.valueOf(folderId != INode.FILES_ROOT ? node.getParentId() : INode.FILES_ROOT)};
//                fileBaseService.sendINodeEvent(userToken,
//                    EventType.OTHERS,
//                    null,
//                    null,
//                    UserLogType.LIST_FOLDER_ERR,
//                    logParams,
//                    keyword);
//            }
//            
//            throw t;
//        }
//    */}

    private RestFolderListsV1 convertTo(RestFolderLists folderList, int deviceType)
    {
        RestFolderListsV1 result = new RestFolderListsV1();
        result.setLimit(folderList.getLimit());
        result.setOffset(folderList.getOffset());
        result.setTotalCount(folderList.getTotalCount());
        if(CollectionUtils.isNotEmpty(folderList.getFolders()))
        {
            List<RestFolderInfoV1> folders = convertToFolderList(folderList.getFolders());
            result.setFolders(folders);
        }
        if(CollectionUtils.isNotEmpty(folderList.getFiles()))
        {
            List<RestFileInfoV1> files = convertToFileList(folderList.getFiles(), deviceType);
            result.setFiles(files);
        }
        return result;
    }

    private List<RestFolderInfoV1> convertToFolderList(List<RestFolderInfo> folders)
    {
        if(CollectionUtils.isEmpty(folders))
        {
            return null;
        }
        List<RestFolderInfoV1> v1Folders = new ArrayList<RestFolderInfoV1>(folders.size());
        RestFolderInfoV1 tempV1; 
        for(RestFolderInfo tempV2Folder: folders)
        {
            tempV1 = new RestFolderInfoV1(tempV2Folder);
            v1Folders.add(tempV1);
        }
        return v1Folders;
    }
    
    private List<RestFileInfoV1> convertToFileList(List<RestFileInfo> files, int deviceType)
    {
        if(CollectionUtils.isEmpty(files))
        {
            return null;
        }
        List<RestFileInfoV1> v1Files = new ArrayList<RestFileInfoV1>(files.size());
        RestFileInfoV1 tempV1; 
        for(RestFileInfo tempV2File: files)
        {
            tempV1 = new RestFileInfoV1(tempV2File, deviceType);
            v1Files.add(tempV1);
        }
        return v1Files;
    }
    
}
