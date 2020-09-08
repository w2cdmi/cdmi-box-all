package com.huawei.sharedrive.app.mirror.service;

import java.util.List;

import com.huawei.sharedrive.app.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.app.mirror.domain.CopyPolicyUserConfig;


public interface CopyPolicyService
{
    
    /**
     * 检查用户是否存在复制策略
     * @param policy
     * @param ownerId
     * @return
     */
    boolean checkUserCopyPolicy(int policy,long ownerId);
    
    /**
     * 获取策略配置
     * @return
     */
    List<CopyPolicy> listCopyPolicy();
    
    /**
     * 
     * @param policyId
     * @return
     */
    CopyPolicy getCopyPolicy(int policyId);
    
    /**
     * 是不是teamspace类型
     * @param policy
     * @return
     */
    boolean isTeamSpaceType(int policy);
    
    /**
     * 获取一个user
     * @param accountId
     * @param id
     * @return
     */
    CopyPolicyUserConfig getOneUserConfigOrderByACS(long userId, int policy);
}
