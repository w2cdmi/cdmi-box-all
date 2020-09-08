package com.huawei.sharedrive.app.plugins.preview.manager.impl;

import com.huawei.sharedrive.app.authapp.service.AuthAppService;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.dataserver.service.DCUrlManager;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.domain.PersistentEvent;
import com.huawei.sharedrive.app.event.manager.PersistentEventManager;
import com.huawei.sharedrive.app.event.service.PersistentEventConsumer;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.NoSuchFileException;
import com.huawei.sharedrive.app.exception.PreviewNotSupportedException;
import com.huawei.sharedrive.app.files.dao.ObjectReferenceDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.oauth2.domain.PreviewObjectToken;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.PreviewObjectPreUploadResponse;
import com.huawei.sharedrive.app.plugins.preview.manager.FilePreviewManager;
import com.huawei.sharedrive.app.plugins.preview.service.FilePreviewService;
import com.huawei.sharedrive.app.plugins.preview.util.PreviewFileUtil;
import com.huawei.sharedrive.app.user.domain.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.RandomGUID;
import pw.cdmi.core.utils.SpringContextUtil;
import pw.cdmi.file.service.CloudFilesService;
import pw.cdmi.uam.domain.AuthApp;

import javax.annotation.PostConstruct;
import java.util.Date;

/************************************************************
 * @Description: <pre>文件预览插件管理，负责插件的生命周期管理，及业务调用入口</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/3/10
 ************************************************************/
@Service("filePreviewManager")
@Lazy(false)
public class FilePreviewManagerImpl implements FilePreviewManager, PersistentEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(FilePreviewManagerImpl.class);
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private DCUrlManager dcUrlManager;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private ObjectReferenceDAO objectReferenceDAO;
    
//    @Autowired
//    private PreviewFileUtil previewFileUtil;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private PersistentEventManager persistentEventManager;

    @Value("${preview.convert.auto}")
    private boolean isAutoConvert;

    @Value("${preview.server}")
    private String previewServer = "idocv";

    @Autowired
    private CloudFilesService cloudFilesService;

    @PostConstruct
    public void init() {
        //注册Consumer， 处理 activemq.persistent.event.queue 队列下的消息
        persistentEventManager.registerConsumer(this);
        logger.info("File Preview Manger init successfully");
    }

    @Override
    public void consumeEvent(PersistentEvent event) {
        if (!isAutoConvert) {
            return;
        }

        INode node = fileBaseService.getINodeInfo(event.getOwnedBy(), event.getNodeId());
        if (node == null) {
            logger.warn("Can't start convert task, the node is not found. ownerId={}, nodeId={}, name={}", event.getOwnedBy(), event.getNodeId(), event.getNodeName());
            return;
        }

//        if (!previewFileUtil.isPreviewEnable(node)) {
//            logger.info("This account has disabled the preview function. ownerId={}, nodeId={}, name={}", event.getOwnedBy(), event.getNodeId(), event.getNodeName());
//        }

        //启动预览转换任务
        getFilePreviewService().startConvertTask(node);
    }

    @Override
    public EventType[] getInterestedEvent()
    {
        return new EventType[]{EventType.INODE_PRELOAD_END};
    }

    @Override
    public String getPreviewUrl(UserToken opUser, INode node) {
        //用户账号是否开户预览功能
//        if (!previewFileUtil.isPreviewEnable(node)) {
//            throw new ForbiddenException();
//        }

        String filename = node.getName();
        String ext = getFileExt(filename);
        if(isDocFile(ext)) {
            //文档，返回文档预览服务器提供的地址
            return getFilePreviewService().getPreviewUrl(opUser, node);
        } else if(isImageFile(ext) || isAudioFile(ext) || isVideoFile(ext)) {
            //图片、音频、视频，返回GET地址
            return getDownloadUrl(opUser, node);
        } else {
            throw new PreviewNotSupportedException();
        }
    }

    private boolean isDocFile(String ext) {
        return ext.equals("doc") || ext.equals("docx") || ext.equals("xls") || ext.equals("xlsx") || ext.equals("ppt") || ext.equals("pptx") || ext.equals("txt") || ext.equals("pdf");
    }

    private boolean isImageFile(String ext) {
        return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("jpeg2000") || ext.equals("png") || ext.equals("gif") || ext.equals("bmp") || ext.equals("tiff") || ext.equals("psd");
    }

    private boolean isAudioFile(String ext) {
        return ext.equals("mp3") || ext.equals("wav") || ext.equals("mp4");
    }

    private boolean isVideoFile(String ext) {
        return  ext.equals("ogg") || ext.equals("ogv") || ext.equals("webm") || ext.equals("mp4") || ext.equals("avi") || ext.equals("rm") || ext.equals("rmvb") || ext.equals("mov") || ext.equals("wmv") || ext.equals("flv");
    }

    private String getFileExt(String filename) {
        int index = filename.lastIndexOf('.');
        if (index != -1) {
            return filename.substring(index + 1).trim().toLowerCase();
        }

        return "";
    }

    private String getDownloadUrl(UserToken opUser, INode node) {
        String url = null;
        //如果指定了云存储访问接口，使用云存储系统提供的预鉴权接口。
        if(cloudFilesService != null) {
            url = cloudFilesService.getDownloadUrl(node.getObjectId());

            if(url != null) {
                return url;
            }
        }

        //如果未配置cloudFilesService或未查询到数据，仍然走原来的流程。
        DataAccessURLInfo info = fileBaseService.getDownURLByNearAccess(opUser.getRegionId(), opUser.getCloudUserId(), node);
        return info.getDownloadUrl();
    }

    @Override
    public void startConvertTask(INode node) {
        FilePreviewService filePreviewService = getFilePreviewService();
        if(filePreviewService != null) {
            filePreviewService.startConvertTask(node);
        } else {
            logger.error("Can't find the File Preview Plugin.");
        }
    }

    @Override
    public void deleteByObjectId(String objectId) {
        FilePreviewService filePreviewService = getFilePreviewService();
        if(filePreviewService != null) {
            filePreviewService.deleteByObjectId(objectId);
        }
    }

    private FilePreviewService getFilePreviewService() {
        if(StringUtils.isNotBlank(previewServer)) {
            switch (previewServer.toLowerCase()) {
                case "idocv":
                    return (FilePreviewService) SpringContextUtil.getBean("idocvFilePreviewService");
                case "image":
                    return (FilePreviewService) SpringContextUtil.getBean("imageFilePreviewService");
                case "default":
                    return (FilePreviewService) SpringContextUtil.getBean("defaultFilePreviewService");
            }
        }

        return (FilePreviewService) SpringContextUtil.getBean("defaultFilePreviewService");
    }

    public String getSourceObjectDownloadUrl(String appId, String objectId) throws BaseRunException {
        DataAccessURLInfo dataAccessUrlInfo = buildObjectDownloadDownURL(appId, objectId);
        return dataAccessUrlInfo.getDownloadUrl();
    }

    public PreviewObjectPreUploadResponse preUploadPreviewObject(String appId, String sourceObjectId,
                                                                 long accountId, Date convertRealStartTime) throws BaseRunException {
        // 获取QOS端口
        AuthApp authApp = authAppService.getByAuthAppID(appId);
        Integer qosPort = authApp != null ? authApp.getQosPort() : null;
        ObjectReference objectReference = objectReferenceDAO.get(sourceObjectId);
        if (objectReference == null) {
            String message = "File not exist, objectId id:" + sourceObjectId;
            throw new NoSuchFileException(message);
        }

        DataAccessURLInfo uploadURLInfo = dcUrlManager.getUploadURL(objectReference.getResourceGroupId(),
                qosPort);

        PreviewObjectPreUploadResponse rsp = new PreviewObjectPreUploadResponse();

        buildPreviewObjectUploadURL(rsp,
                uploadURLInfo,
                sourceObjectId,
                accountId,
                convertRealStartTime,
                objectReference.getResourceGroupId());

        return rsp;
    }

    public void updatePreviewObjectFailed(String sourceObjectId, long accountId, Date createdAt) {
//        filePreviewService.updateConvertResult(sourceObjectId,
//                accountId,
//                createdAt,
//                PreviewObject.STATUS_FAILED,
//                null,
//                0,
//                0,
//                null,
//                null);
    }

    private DataAccessURLInfo buildObjectDownloadDownURL(String appId, String objectId)
            throws BaseRunException {
        // 获取QOS端口
        AuthApp authApp = authAppService.getByAuthAppID(appId);
        Integer qosPort = authApp != null ? authApp.getQosPort() : null;
        ObjectReference objectReference = objectReferenceDAO.get(objectId);
        if (objectReference == null) {
            String message = "File not exist, objectId id:" + objectId;
            throw new NoSuchFileException(message);
        }
        // 获取资源组信息
        DataAccessURLInfo urlinfo = dcUrlManager.getDownloadURL(objectReference.getResourceGroupId(), qosPort);
        String nodeLastModified = DateUtils.dateToString(DateUtils.RFC822_DATE_FORMAT, new Date());
        UserToken token = userTokenHelper.createTokenDataServer(User.SYSTEM_USER_ID,
                objectId,
                AuthorityMethod.GET_OBJECT,
                0,
                nodeLastModified);
        urlinfo.setDownloadUrl(
                urlinfo.getDownloadUrl() + token.getToken() + "/" + objectId + "/" + objectId);
        return urlinfo;
    }

    private String buildObjectId() {
        return new RandomGUID().getValueAfterMD5();
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void buildPreviewObjectUploadURL(PreviewObjectPreUploadResponse rsp,
                                             DataAccessURLInfo uploadURLInfo, String sourceObjectId, long accountId, Date convertRealStartTime,
                                             int resourceGroupId) throws BaseRunException {
        // 设置对象ID
        String storageObjectId = buildObjectId();

        PreviewObjectToken temp = new PreviewObjectToken();
        temp.setAccountId(accountId);
        temp.setSourceObjectId(sourceObjectId);
        temp.setConvertRealStartTime(convertRealStartTime);
        temp.setResourceGroupId(resourceGroupId);
        PreviewObjectToken token = userTokenHelper.createTokenDataServer(AuthorityMethod.UPLOAD_OBJECT,
                storageObjectId,
                temp);

        String url = uploadURLInfo.getUploadUrl();
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        // 组装URL，token在URL中
        url = url + token.getToken() + "/" + storageObjectId;
        rsp.setUploadUrl(url);
    }
}
