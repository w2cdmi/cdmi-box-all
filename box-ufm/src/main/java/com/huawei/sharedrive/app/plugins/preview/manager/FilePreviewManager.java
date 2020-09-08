package com.huawei.sharedrive.app.plugins.preview.manager;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.utils.Constants;

/************************************************************
 * @Description: <pre>文件预览插件管理，负责插件的生命周期管理，及业务调用入口</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/3/10
 ************************************************************/
public interface FilePreviewManager {
    String PREVIEW_CONVERT_APP_ID = Constants.APPID_PPREVIEW;

    /**
     * 获取预览地址
     */
    String getPreviewUrl(UserToken userToken, INode node);

    //生成预览地址（预先启动预览转化，提高第一次预览时的加载速度）
    void startConvertTask(INode node);

    //删除生成的预览信息
    void deleteByObjectId(String objectId);
}