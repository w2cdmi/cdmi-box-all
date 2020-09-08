package com.huawei.sharedrive.app.dataserver.thrift.impl;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.ObjectUpdateInfo;
import com.huawei.sharedrive.app.files.service.FileService;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.plugins.preview.domain.PreviewObject;
import com.huawei.sharedrive.app.plugins.preview.service.FilePreviewService;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.thrift.app2dc.FileObject;
import com.huawei.sharedrive.thrift.app2dc.FileObjectThriftService.Iface;
import com.huawei.sharedrive.thrift.app2dc.TBusinessException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.core.utils.JsonUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FileObjectThriftServiceImpl implements Iface
{
    
    private static Logger logger = LoggerFactory.getLogger(FileObjectThriftServiceImpl.class);
    
    @Autowired
    private FileService fileService;
    
    @Override
    public void abortUpload(String objectId, String callBackKey) throws TException {
        LoggerUtil.regiestThreadLocalLog();

        logger.info("dc callback abortUpload, fileObjectId:" + objectId + ", callBackKey:" + callBackKey);
        Map<String, String> result = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        boolean isFileUploaded = isFileUploaded(callBackKey, result);
        if (isFileUploaded) {
            abortUploadObject(objectId, result);
        } else {
            // TODO 处理预览对象上传中断
            logger.warn("receive a preview object upload abort message: " + objectId + ", " + callBackKey);
        }
    }

    // add by: guoz : 這里是新文件在dss上傳存儲后，更新ufm對應數據庫記錄的回調。
    @Override
    public void updateFileObject(FileObject fileObject, String callBackKey) throws TException {
        LoggerUtil.regiestThreadLocalLog();
        logger.info("dc callback, fileObject:" + ToStringBuilder.reflectionToString(fileObject) + ", callBackKey:" + callBackKey);
        Map<String, String> result = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        boolean isObjectCallback = isFileUploaded(callBackKey, result);
        if (isObjectCallback) {
            updateObject(fileObject, result);
        } else {
            updatePreviewObjectNormal(fileObject, result);
        }
    }
    
    /**
     * 将callbackkey json数据填充到map中,并返回是否为普通对象上传回调
     * 
     * @param callBackKey
     * @param result
     * @return true为普通对象上传,false为预览对象上传
     * @throws TException
     */
    private boolean isFileUploaded(String callBackKey, Map<String, String> result) throws TException {
        try {
            JsonUtils.fillJsonMap(callBackKey, result);
        } catch (IOException e) {
            logger.error("error occur when parse callback key " + callBackKey, e);
            throw new TException("error occur when parse callback key " + callBackKey, e);
        }
        String callbackType = result.get(UserTokenHelper.KEY_CALLBACK_TYPE);
        if (StringUtils.isBlank(callbackType)) {
            return true;
        }
        if (UserTokenHelper.CALLBACK_TYPE_OBJECT.equals(callbackType)) {
            return true;
        }
        return false;
    }
    
    private void abortUploadObject(String objectId, Map<String, String> result) throws TException
    {
        try
        {
            long ownedId = Long.parseLong(String.valueOf(result.get(UserTokenHelper.KEY_CALLBACK_OWNERID)));
            fileService.abortUpload(objectId, ownedId);
        }
        catch (BaseRunException e)
        {
            logger.error(e.getMessage(), e);
            // 更新异常需要返回给DSS
            throw new TBusinessException(e.getHttpcode().value(), "" + e);
        }
    }
    
    private void updateObject(FileObject fileObject, Map<String, String> result) throws TBusinessException
    {
        ObjectUpdateInfo objectInfo = new ObjectUpdateInfo();
        objectInfo.setObjectId(fileObject.objectID);
        objectInfo.setSha1(StringUtils.isNotBlank(fileObject.sha1) ? fileObject.sha1 : "");
        objectInfo.setLength(fileObject.length);
        objectInfo.setStoragePath(fileObject.storagePath);
        try
        {
            long ownedId = Long.parseLong(result.get(UserTokenHelper.KEY_CALLBACK_OWNERID));
            objectInfo.setOwnerId(ownedId);
            fileService.updateObjectInfo(objectInfo);
        }
        catch (BaseRunException e)
        {
            logger.error(e.getMessage(), e);
            // 更新异常需要返回给DSS
            throw new TBusinessException(e.getHttpcode().value(), "" + e);
        }
    }

    private void updatePreviewObjectNormal(FileObject fileObject, Map<String, String> result) throws TException {
        //TODO: #PREVIEW# 暂不更新预览对象
/*
        try {
            String sourceObjectId = result.get(UserTokenHelper.KEY_CALLBACK_SOURCE_OBJECT_ID);
            long accountId = Long.parseLong(result.get(UserTokenHelper.KEY_CALLBACK_ACCOUNT_ID));
            long convertRealStartTime = Long.parseLong(result.get(UserTokenHelper.KEY_CALLBACK_CONVERT_REAL_START_TIME));
            Date createdAt = new Date(convertRealStartTime);
            int resourceGroupId = Integer.parseInt(result.get(UserTokenHelper.KEY_CALLBACK_RESOURCE_GROUP_ID));

            Map<String, String> md5Map = FilesCommonUtils.parseMD5(fileObject.sha1 == null ? "" : fileObject.sha1);
            String md5 = md5Map.get("MD5");
            String blockMd5 = md5Map.get("BlockMD5");

            filePreviewService.updateConvertResult(sourceObjectId,
                    accountId,
                    createdAt,
                    PreviewObject.STATUS_NORMAIL,
                    fileObject.getObjectID(),
                    resourceGroupId,
                    fileObject.getLength(),
                    md5,
                    blockMd5);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new TException(e.getMessage(), e);
        }
*/
    }
    
}
