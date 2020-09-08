package com.huawei.sharedrive.app.plugins.preview.service.impl;

import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.authapp.service.AuthAppService;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.exception.BusinessErrorCode;
import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.dataserver.service.DCUrlManager;
import com.huawei.sharedrive.app.dataserver.thrift.client.FileObjectThriftServiceClient;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.exception.InvalidFileTypeException;
import com.huawei.sharedrive.app.exception.NoSuchFileException;
import com.huawei.sharedrive.app.files.dao.WaitingDeleteObjectDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.WaitingDeleteObject;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.plugins.preview.dao.PreviewObjectDao;
import com.huawei.sharedrive.app.plugins.preview.domain.AccountWatermark;
import com.huawei.sharedrive.app.plugins.preview.domain.PreviewObject;
import com.huawei.sharedrive.app.plugins.preview.exception.FileConvertFailedException;
import com.huawei.sharedrive.app.plugins.preview.exception.FileInConvertException;
import com.huawei.sharedrive.app.plugins.preview.service.AccountWatermarkService;
import com.huawei.sharedrive.app.plugins.preview.service.FilePreviewService;
import com.huawei.sharedrive.app.plugins.preview.util.PreviewFileUtil;
import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.domain.User;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pw.cdmi.common.preview.domain.ConvertTask;
import pw.cdmi.common.preview.domain.ConvertTaskParser;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.uam.domain.AuthApp;

import javax.annotation.PostConstruct;
import javax.jms.*;
import java.util.Date;
import java.util.List;

@Service("defaultFilePreviewService")
@Lazy
public class DefaultFilePreviewServiceImpl implements FilePreviewService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFilePreviewServiceImpl.class);
    private static final String PREVIEW_CONVERT_TIMEOUT_CONF_KEY = "preview.convert.timeout";

    @Autowired
    private PreviewObjectDao previewObjectDao;
    
    @Autowired
    private WaitingDeleteObjectDAO waitingDeleteObjectDAO;
    
    @Autowired
    private DCManager dcManager;
    
    @Autowired
    private DssDomainService dssDomainService;
    
    @Value("${activemq.broker.url}")
    private String jmsUrl;
    
    @Value("${activemq.preview.convert.job.queue}")
    private String jobQueue;

    private Session session = null;
    
    private MessageProducer jobProducer = null;

    @Autowired
    private AccountWatermarkService accountWatermarkService;

    @Autowired
    private AuthAppService authAppService;

    private long convertTimeout;

    @Autowired
    private DCUrlManager dcUrlManager;

    @Value("${preview.convert.auto}")
    private boolean isAutoConvert;

    @Autowired
    private PreviewFileUtil previewFileUtil;

    @Autowired
    private SystemConfigDAO systemConfigDAO;

    @Autowired
    private UserDAOV2 userDAO;

    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private INodeACLService iNodeACLService;

    @PostConstruct
    public void init() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(jmsUrl);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination jobDestination = session.createQueue(jobQueue);
        jobProducer = session.createProducer(jobDestination);
        LOGGER.info("File Preview Service init successfully");

        //预览转换超时时间
        convertTimeout = Long.parseLong(systemConfigDAO.get(PREVIEW_CONVERT_TIMEOUT_CONF_KEY).getValue());
    }

    @Override
    public String getPreviewUrl(UserToken userToken, INode node) {
        if (!PreviewFileUtil.isSupportPreview(node)) {
            throw new InvalidFileTypeException("not support file type:" + node.getName().substring(node.getName().lastIndexOf('.')));
        }
        long userId = iNodeACLService.vaildINodeOperACL(userToken, node, AuthorityMethod.GET_PREVIEW.name());
        String fileNameSuffix = PreviewFileUtil.getFileNameSuffix(node);
        User ownedUser = userDAO.get(node.getOwnedBy());
        long accountId = ownedUser.getAccountId();
        String objectId = node.getObjectId();

        PreviewObject previewObject = getAndSendTask(objectId, accountId, fileNameSuffix, ConvertTask.PRIORITY_HIGH);
        if (previewObject.getStatus() == PreviewObject.STATUS_CREATING) {
            return checkConvertTimeout(userId, ownedUser, previewObject, fileNameSuffix);
        } else if (previewObject.getStatus() == PreviewObject.STATUS_FAILED) {
            throw new FileConvertFailedException();
        }

        //检查是否需要水印
        AccountWatermark watermark = accountWatermarkService.getWatermarkByAccountId(accountId);
        if (watermark != null && watermark.getLastConfigTime() != null && watermark.getLastConfigTime().after(previewObject.getCreatedAt())) {
            updateConvertRestart(objectId, accountId, fileNameSuffix);
            throw new FileInConvertException();
        }

        return buildPreviewObjectDownloadUrl(userId, ownedUser, previewObject);
    }

    private String checkConvertTimeout(long opUserId, User ownedUser, PreviewObject previewObject, String fileNameSuffix) {
        Date convertStartTime = previewObject.getConvertStartTime();
        Date now = new Date();
        //大于超时时间
        if (convertStartTime != null && (now.getTime() - convertStartTime.getTime()) > convertTimeout) {
            PreviewObject object = updateConvertStartTimeToNow(previewObject.getSourceObjectId(), previewObject.getAccountId(), fileNameSuffix);
            if (object.getStatus() != PreviewObject.STATUS_CREATING) {
                return buildPreviewObjectDownloadUrl(opUserId, ownedUser, object);
            }
        }
        throw new FileInConvertException();
    }


    private String buildPreviewObjectDownloadUrl(long opUserId, User ownUser, PreviewObject previewObject) throws FileConvertFailedException {
        if (previewObject.getStatus() == PreviewObject.STATUS_FAILED) {
            throw new FileConvertFailedException();
        }

        String storageObjectId = previewObject.getStorageObjectId();

        // 获取QOS端口
        AuthApp authApp = authAppService.getByAuthAppID(ownUser.getAppId());
        Integer qosPort = authApp != null ? authApp.getQosPort() : null;

        // 获取资源组信息
        DataAccessURLInfo info = dcUrlManager.getDownloadURL(previewObject.getResourceGroupId(), qosPort);
        String nodeLastModified = DateUtils.dateToString(DateUtils.RFC822_DATE_FORMAT, previewObject.getCreatedAt());

        UserToken token = userTokenHelper.createTokenDataServer(opUserId, storageObjectId, Authorize.AuthorityMethod.GET_OBJECT, ownUser.getId(), nodeLastModified);

        return info.getDownloadUrl() + token.getToken() + "/" + storageObjectId + "/preview";
    }

    @Override
    public void startConvertTask(INode node) {
        if (!isAutoConvert) {
            LOGGER.debug("Auto Convert is not enabled. ignore it.");
            return;
        }

        if (!previewFileUtil.isPreviewEnable(node)) {
            LOGGER.info("The preview function of the node is not enabled. ownerId={}", node.getOwnedBy());
            return;
        }

        if (!PreviewFileUtil.isSupportPreview(node)) {
            LOGGER.info("The file is not supported to preview. ownerId={}, nodeId={}, name={}", node.getOwnedBy(), node.getId(), node.getName());
            return;
        }

        String sourceObjectId = node.getObjectId();

        String fileNameSuffix = PreviewFileUtil.getFileNameSuffix(node);
        User ownedUser = userDAO.get(node.getOwnedBy());
        long accountId = ownedUser.getAccountId();

        //
        getAndSendTask(sourceObjectId, accountId, fileNameSuffix, ConvertTask.PRIORITY_LOW);
    }

    protected PreviewObject getAndSendTask(String sourceObjectId, long accountId, String fileNameSuffix, int taskPriority) {
        PreviewObject previewObject = previewObjectDao.get(sourceObjectId, accountId);
        if (previewObject == null) {
            previewObject = new PreviewObject();
            previewObject.setSourceObjectId(sourceObjectId);
            previewObject.setAccountId(accountId);
            previewObject.setConvertStartTime(new Date());
            previewObject.setStatus(PreviewObject.STATUS_CREATING);
            try {
                previewObjectDao.create(previewObject);
                this.sendConvertTask(sourceObjectId, accountId, fileNameSuffix, taskPriority);
            } catch (ConcurrencyFailureException e) {
                if (e.getRootCause().getMessage().startsWith("Duplicate entry")) {
                    LOGGER.debug("preview object may be created by other", e);
                    return this.getAndSendTask(sourceObjectId, accountId, fileNameSuffix, taskPriority);
                }
            }
        }
        return previewObject;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PreviewObject updateConvertStartTimeToNow(String sourceObjectId, long accountId, String fileNameSuffix) {
        PreviewObject previewObject = previewObjectDao.selectForUpdate(sourceObjectId, accountId);
        if (previewObject == null) {
            String message = "can not found preview object by " + sourceObjectId + '/' + accountId;
            throw new NoSuchFileException(message);
        }
        if (previewObject.getStatus() != PreviewObject.STATUS_CREATING) {
            return previewObject;
        }
        previewObject.setConvertStartTime(new Date());
        previewObjectDao.updateConvertStartTime(previewObject);
        this.sendConvertTask(sourceObjectId, accountId, fileNameSuffix, ConvertTask.PRIORITY_HIGH);
        return previewObject;
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PreviewObject updateConvertResult(String sourceObjectId, long accountId, Date createdAt, byte status, String storageObjectId, int resourceGroupId, long size, String md5, String blockMD5) {
        PreviewObject previewObject = previewObjectDao.selectForUpdate(sourceObjectId, accountId);
        if (previewObject == null) {
            String message = "can not found preview object by " + sourceObjectId + '/' + accountId;
            throw new NoSuchFileException(message);
        }
        String oldStorageObjectId = previewObject.getStorageObjectId();
        int oldResourceGroupId = previewObject.getResourceGroupId();
        previewObject.setCreatedAt(createdAt);
        previewObject.setStatus(status);
        previewObject.setStorageObjectId(storageObjectId);
        previewObject.setResourceGroupId(resourceGroupId);
        previewObject.setSize(size);
        previewObject.setMd5(md5);
        previewObject.setBlockMD5(blockMD5);
        previewObjectDao.updateConvertResult(previewObject);
        if (StringUtils.isNotBlank(oldStorageObjectId)) {
            deleteFromDss(oldResourceGroupId, oldStorageObjectId);
        }
        return previewObject;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PreviewObject updateConvertRestart(String sourceObjectId, long accountId, String fileNameSuffix) {
        PreviewObject previewObject = previewObjectDao.selectForUpdate(sourceObjectId, accountId);
        if (previewObject == null) {
            String message = "can not found preview object by " + sourceObjectId + '/' + accountId;
            throw new NoSuchFileException(message);
        }
        byte status = previewObject.getStatus();
        if (status == PreviewObject.STATUS_CREATING) {
            return previewObject;
        }
        String oldStorageObjectId = previewObject.getStorageObjectId();
        int oldResourceGroupId = previewObject.getResourceGroupId();
        previewObject.setBlockMD5(null);
        previewObject.setConvertStartTime(new Date());
        previewObject.setCreatedAt(null);
        previewObject.setMd5(null);
        previewObject.setResourceGroupId(0);
        previewObject.setSize(0);
        previewObject.setStatus(PreviewObject.STATUS_CREATING);
        previewObject.setStorageObjectId(null);
        previewObjectDao.updateConvertRestart(previewObject);
        this.sendConvertTask(sourceObjectId, accountId, fileNameSuffix, ConvertTask.PRIORITY_HIGH);
        if (StringUtils.isNotBlank(oldStorageObjectId)) {
            deleteFromDss(oldResourceGroupId, oldStorageObjectId);
        }
        return previewObject;
    }

    private void sendConvertTask(String sourceObjectId, long accountId, String fileNameSuffix, int priority) {
        ConvertTask task = new ConvertTask();
        task.setSourceObjectId(sourceObjectId);
        task.setAccountId(accountId);
        task.setSourceFileSuffix(fileNameSuffix);
        task.setPriority(priority);
        task.setStorageObjectId("");
        byte[] data = ConvertTaskParser.convertTaskToBytes(task);
        try {
            BytesMessage message = session.createBytesMessage();
            message.writeBytes(data);
            jobProducer.send(message, DeliveryMode.PERSISTENT, priority, 0);
        } catch (JMSException e) {
            LOGGER.error("error occur when send convert task", e);
            throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "error occur when send convert task", e);
        }
    }

    public void deleteByObjectId(String sourceObjectId) {
        List<PreviewObject> list = previewObjectDao.getAllBySourceObjectId(sourceObjectId);
        for (PreviewObject previewObject : list) {
            deletePreviewObject(previewObject);
        }
    }

    private void deletePreviewObject(PreviewObject previewObject) {
        int resourceGroupId = previewObject.getResourceGroupId();
        String storageObjectId = previewObject.getStorageObjectId();
        String sourceObjectId = previewObject.getSourceObjectId();
        long accountId = previewObject.getAccountId();
        previewObjectDao.delete(sourceObjectId, accountId);
        deleteFromDss(resourceGroupId, storageObjectId);
    }

    private void deleteFromDss(int resourceGroupId, String objectId) {
        if (StringUtils.isBlank(objectId)) {
            return;
        }
        ResourceGroup group = dcManager.getCacheResourceGroup(resourceGroupId);
        String domain = dssDomainService.getDomainByDssId(group);
        FileObjectThriftServiceClient client = null;
        try {
            client = new FileObjectThriftServiceClient(domain, group.getManagePort());
            client.deleteFileObject(objectId);
        } catch (TException e) {
            LOGGER.error("can not delete object " + objectId, e);
            WaitingDeleteObject waitingDeleteObject = new WaitingDeleteObject();
            waitingDeleteObject.setObjectId(objectId);
            waitingDeleteObject.setResourceGroupId(resourceGroupId);
            waitingDeleteObject.setCreatedAt(new Date());
            waitingDeleteObjectDAO.create(waitingDeleteObject);
        } finally {
            if (null != client) {
                client.close();
            }
        }
    }
}
