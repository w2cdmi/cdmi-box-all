package com.huawei.sharedrive.app.files.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.app.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.TrashService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

import pw.cdmi.box.domain.Limit;

@Service("trashService")
public class TrashServiceImpl implements TrashService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TrashServiceImpl.class);
    
    @Autowired
    private INodeACLService iNodeACLService;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Override
    public FileINodesList listTrashItems(UserToken user, long ownerId, OrderV1 order, Limit limit)
        throws BaseRunException
    {
        if (null == user)
        {
            LOGGER.error("user is null");
            throw new BadRequestException();
        }
        
        INode filter = new INode();
        filter.setOwnedBy(ownerId);
        filter.setId(INode.FILES_ROOT);
        filter.setStatus(INode.STATUS_TRASH);
        // 权限校验
        iNodeACLService.vaildINodeOperACL(user, filter, AuthorityMethod.PUT_ALL.name());
        
        int total = iNodeDAO.getINodeCountByStatus(filter);
        List<INode> foldersINode = iNodeDAO.getINodeByStatus(filter, order, limit);
        
        FileINodesList folderLists = new FileINodesList();
        folderLists.setTotalCount(total);
        folderLists.setLimit(limit.getLength());
        folderLists.setOffset(limit.getOffset());
        
        List<INode> folderList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        List<INode> fileList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
        
        for (INode tmpNode : foldersINode)
        {
            if (FilesCommonUtils.isFolderType(tmpNode.getType()))
            {
                folderList.add(tmpNode);
            }
            else if (tmpNode.getType() == INode.TYPE_FILE)
            {
                if (FilesCommonUtils.isImage(tmpNode.getName()))
                {
                    DataAccessURLInfo dataAccessUrlInfo = fileBaseService.getINodeInfoDownURL(tmpNode.getOwnedBy(),
                        tmpNode);
                    tmpNode.setThumbnailUrl(dataAccessUrlInfo.getDownloadUrl()
                        + FileBaseServiceImpl.THUMBNAIL_PREFIX_SMALL);
                    DataAccessURLInfo bigDataAccessUrlInfo = fileBaseService.getINodeInfoDownURL(tmpNode.getOwnedBy(),
                        tmpNode);
                    tmpNode.setThumbnailBigURL(bigDataAccessUrlInfo.getDownloadUrl()
                        + FileBaseServiceImpl.THUMBNAIL_PREFIX_BIG);
                }
                fileList.add(tmpNode);
            }
        }
        
        folderLists.setFolders(folderList);
        folderLists.setFiles(fileList);
        
        return folderLists;
        
    }
    
}
