/**
 * 
 */
package com.huawei.sharedrive.app.files.service;

import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

/**
 * 文件相关操作业务接口 V2
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-5-12
 * @see
 * @since
 */
public interface FileServiceV2
{
    /**
     * 获取文件信息
     * 
     * @param user
     * @param ownerId
     * @param fileId
     * @return
     * @throws BaseRunException
     */
    INode getFileInfo(UserToken userToken, long ownerId, long fileId) throws BaseRunException;
    
    /**
     * 获取文件缩略图地址
     * 
     * @param userToken
     * @param ownerId
     * @param fileId
     * @return
     * @throws BaseRunException
     */
    ThumbnailUrl getThumbnailUrl(UserToken userToken, INode node, Thumbnail thumbnail)
        throws BaseRunException;
    
    /**
     * 刷新上传地址
     * 
     * @param userToken
     * @param ownerId
     * @param uploadUrl
     * @return
     * @throws BaseRunException
     */
    String refreshUploadUrl(UserToken userToken, INode node, String uploadUrl) throws BaseRunException;

    /**
     * 恢复历史版本
     * 
     * @param userToken
     * @param versionNode
     * @return
     * @throws BaseRunException
     */
    INode restoreFileVersion(UserToken userToken, INode versionNode) throws BaseRunException;

}
