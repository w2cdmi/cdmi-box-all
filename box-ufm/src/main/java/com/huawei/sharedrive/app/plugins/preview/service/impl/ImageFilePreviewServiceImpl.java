package com.huawei.sharedrive.app.plugins.preview.service.impl;

import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.authapp.service.AuthAppService;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.DCUrlManager;
import com.huawei.sharedrive.app.dataserver.service.ResourceGroupService;
import com.huawei.sharedrive.app.exception.InvalidFileTypeException;
import com.huawei.sharedrive.app.exception.NoSuchUserException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.manager.FilesInnerManager;
import com.huawei.sharedrive.app.mirror.domain.MirrorObject;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.plugins.preview.service.FilePreviewService;
import com.huawei.sharedrive.app.plugins.preview.util.PreviewFileUtil;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pw.cdmi.box.app.convertservice.domain.ImgObject;
import pw.cdmi.box.app.convertservice.domain.TaskBean;
import pw.cdmi.box.app.convertservice.service.ConvertService;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.uam.domain.AuthApp;

import java.util.Date;

/**
 * 将文件转换成图片进行预览
 */
@Service("imageFilePreviewService")
@Lazy
public class ImageFilePreviewServiceImpl implements FilePreviewService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageFilePreviewServiceImpl.class);

    @Autowired
    private FilesInnerManager filesInnerManager;

    @Autowired
    private ConvertService convertService;

    @Autowired
    private AuthAppService authAppService;

    @Autowired
    private DCUrlManager dcUrlManager;

    @Autowired
    private ResourceGroupService resourceGroupService;

    @Autowired
    private UserDAOV2 userDAO;

    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private INodeACLService iNodeACLService;

    /**
     * 获取文件预览地址
     */
    @Override
    public String getPreviewUrl(UserToken userToken, INode inode) {
        //文件是否支持预览操作
        if (!PreviewFileUtil.isSupportPreview(inode)) {
            throw new InvalidFileTypeException("not support file type:" + inode.getName().substring(inode.getName().lastIndexOf('.')));
        }
        
        long userId = iNodeACLService.vaildINodeOperACL(userToken, inode, AuthorityMethod.GET_PREVIEW.name());

        String previewObject = inode.getObjectId();

        User user = userDAO.get(inode.getOwnedBy());
        if (null == user) {
            LOGGER.error("user no exist,userid :" + inode.getOwnedBy() + ",inode id:" + inode.getId());
            throw new NoSuchUserException("CloudUserId:" + inode.getOwnedBy());
        }

        AuthApp authApp = authAppService.getByAuthAppID(user.getAppId());
        Integer qosPort = authApp != null ? authApp.getQosPort() : null;

        int resourceGroupId = inode.getResourceGroupId();
        ResourceGroup group = resourceGroupService.getResourceGroup(resourceGroupId);

        //
        MirrorObject mirrorObject = filesInnerManager.getBestObjectMirrorShip(authApp, group.getRegionId(), resourceGroupId, inode);
        if (mirrorObject != null) {
            ImgObject imageObject = this.convertService.getImage(mirrorObject.getDestObjectId());
            if (imageObject != null) {
                resourceGroupId = mirrorObject.getDestResourceGroupId();
                previewObject = mirrorObject.getDestObjectId();
            }
        }

        MirrorObject priorityMirrorObject = filesInnerManager.getResourceGroupByPriority(resourceGroupId, inode);
        if (priorityMirrorObject != null) {
            ImgObject imageObject = this.convertService.getImage(priorityMirrorObject.getDestObjectId());
            if (imageObject != null) {
                resourceGroupId = priorityMirrorObject.getDestResourceGroupId();
                previewObject = priorityMirrorObject.getDestObjectId();
            }
        }

        ImgObject imageObject = this.convertService.getImage(previewObject);
        if (imageObject != null) {
            DataAccessURLInfo info = dcUrlManager.getPreviewUrl(resourceGroupId, qosPort);
            String nodeLastModified = DateUtils.dateToString(DateUtils.RFC822_DATE_FORMAT, new Date());
            UserToken token = userTokenHelper.createTokenDataServer(userId, imageObject.getImageObjectId(), Authorize.AuthorityMethod.GET_OBJECT, inode.getOwnedBy(), nodeLastModified);

            return info.getPreviewUrl() + token.getToken() + '/' + imageObject.getImageObjectId() + "/imagePreview";
        }

        return null;
    }

    @Override
    public void startConvertTask(INode node) {
        TaskBean task = new TaskBean();
        task.setObjectId(node.getObjectId());
        task.setOwneId(String.valueOf(node.getOwnedBy()));

        convertService.addTask(task);
    }

    @Override
    public void deleteByObjectId(String objectId) {
        convertService.deleteImageObject(objectId);
    }
}
