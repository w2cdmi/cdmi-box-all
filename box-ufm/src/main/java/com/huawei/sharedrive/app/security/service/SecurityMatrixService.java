package com.huawei.sharedrive.app.security.service;

import java.util.Map;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.security.domain.CheckEngine;

public interface SecurityMatrixService
{
    /**
     * @param userToken
     * @param ownerId
     * @param nodeId
     * @param destOwnerId
     * @param method
     * @throws BaseRunException
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    void checkSecurityMatrix(UserToken userToken, Long ownerId, Long nodeId, Long destOwnerId,
        SecurityMethod method, Map<String, String> headerCustomMap) throws BaseRunException;
    
    /**
     * @param userToken
     * @param ownerId
     * @param nodeId
     * @param method
     * @throws BaseRunException
     */
    void checkSecurityMatrix(UserToken userToken, Long ownerId, Long nodeId, SecurityMethod method,
        Map<String, String> headerCustomMap) throws BaseRunException;
    
    /**
     * 获取文件的安全类型
     * 
     * @param userToken
     * @return
     */
    byte getFileSecurityId(UserToken userToken);
    
    /**
     * 安全矩阵是否开启
     * 
     * @return
     */
    boolean isSecurityMatrixEnable();
    
    int getUserSecurityId(UserToken userToken, long ownerId);
    
    CheckEngine getCheckEngine();
}
