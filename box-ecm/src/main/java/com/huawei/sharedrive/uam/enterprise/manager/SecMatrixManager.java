package com.huawei.sharedrive.uam.enterprise.manager;

import com.huawei.sharedrive.uam.exception.NoSuchUserException;
import com.huawei.sharedrive.uam.openapi.domain.secmatrix.FileSecResponse;
import com.huawei.sharedrive.uam.openapi.domain.secmatrix.GetSecRoleResponse;

public interface SecMatrixManager
{
    @SuppressWarnings("PMD.ExcessiveParameterList")
    boolean judgeFileMatrix(long accountId, Integer secRole, Integer spaceSecRole, String ip, Byte fileSecId,
        String operation, Integer deviceType);
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    boolean judgeSpaceMatrix(long accountId, Integer secRole, String ip, Integer spaceSecRole,
        String operation, Integer deviceType);
    
    boolean judgeFileCopyMatrix(long accountId, Integer secRole, Integer targetRole, String operation);
    
    GetSecRoleResponse getSecRole(int accountId, long cloudUserId) throws NoSuchUserException;
    
    FileSecResponse getFileSecId(long accountId, Integer secRole, String realIp, int deviceType);
}
