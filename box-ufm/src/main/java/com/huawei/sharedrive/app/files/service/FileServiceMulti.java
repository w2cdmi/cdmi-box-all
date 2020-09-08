/**
 * 
 */
package com.huawei.sharedrive.app.files.service;

import java.util.List;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.node.FileMultiPreUploadResponse;

public interface FileServiceMulti
{
    /**
     * 批量于预上传
     * 
     * @param user
     * @param fileNodeList
     * @return
     * @throws BaseRunException
     */
    FileMultiPreUploadResponse preUploadFile(UserToken user, List<INode> fileNodeList, Long tokenTimeout) throws BaseRunException;
}
