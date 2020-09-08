package com.huawei.sharedrive.isystem.mirror.service;

import java.util.List;

import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicySiteInfo;
import com.huawei.sharedrive.isystem.plugin.domain.DCTreeNode;

public interface CopyPolicyService
{
    
    /**
     * 获取策略配置
     * 
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
     * 创建复制策略
     * 
     * @param policy
     */
    void createCopyPolicy(CopyPolicy policy);
    
    /**
     * 修改复制策略
     * 
     * @param policy
     */
    void modifyCopyPolicy(CopyPolicy policy);
    
    /**
     * 修改复制策略的站点信息
     * 
     * @param policy
     * @param lstCopyPolicyDataSiteInfo
     */
    void modifyCopyPolicySiteInfo(CopyPolicy policy, List<CopyPolicySiteInfo> lstCopyPolicyDataSiteInfo);
    
    /**
     * 修改复制策略的执行时间
     * 
     * @param policy
     */
    void modifyCopyPolicyExeTime(CopyPolicy policy);
    
    /**
     * 修改复制策略状态
     * 
     * @param policy
     */
    void modifyCopyPolicyState(CopyPolicy policy);
    
    /**
     * 删除复制策略
     * 
     * @param policy
     */
    void deleteCopyPolicy(CopyPolicy policy);
    
    /**
     * 获取App的复制策略
     * 
     * @param appId
     * @return
     */
    List<CopyPolicy> getAppCopyPolicy(String appId);

    List<DCTreeNode> getTreeNode(Integer id);
    
}
