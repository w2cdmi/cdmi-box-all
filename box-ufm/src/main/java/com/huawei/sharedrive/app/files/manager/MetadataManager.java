package com.huawei.sharedrive.app.files.manager;

import javax.servlet.http.HttpServletResponse;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.synchronous.SyncVersionRsp;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

public interface MetadataManager
{
    SyncVersionRsp getMetadataFile(UserToken user, INode syncFolder, String fileRootDir,
        HttpServletResponse response, boolean isNeedZip) throws BaseRunException;
    
}
