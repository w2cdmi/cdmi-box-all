package com.huawei.sharedrive.app.files.service;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

public interface NodeCopyService
{
    INode copyFileNodeToFolder(UserToken user, INode srcFile, INode destFolder) throws BaseRunException;
    
    INode copyFolderItemToFolder(UserToken user, INode srcFolder, INode destFolder);
    
    INode copyFolderNodeToFolder(UserToken user, INode srcFolder, INode destFolder) throws BaseRunException;
    
    INode copyNodesToFolderByRecursive(UserToken user, INode srcNode, INode destFolder, long syncVersionNum)
        throws BaseRunException;
}
