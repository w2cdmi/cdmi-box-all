package com.huawei.sharedrive.app.plugins.preview.service;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.plugins.preview.domain.PreviewObject;

import java.util.Date;

public interface FilePreviewService {
    /**
     * 获取预览地址
     */
    String getPreviewUrl(UserToken userToken, INode node);

    //启动转换任务
    void startConvertTask(INode node);

    void deleteByObjectId(String objectId);
/*

    int getDssIdByObjectId(String objectId);

    PreviewObject getAndSendTask(String sourceObjectId, long accountId, String fileNameSuffix, int taskPriority);

    PreviewObject updateConvertStartTimeToNow(String sourceObjectId, long accountId, String fileNameSuffix);

    @SuppressWarnings("PMD.ExcessiveParameterList")
    PreviewObject updateConvertResult(String sourceObjectId, long accountId, Date createdAt, byte status,
                                      String storageObjectId, int resourceGroupId, long size, String md5, String blockMd5);

    PreviewObject updateConvertRestart(String sourceObjectId, long accountId, String fileNameSuffix);

*/
}