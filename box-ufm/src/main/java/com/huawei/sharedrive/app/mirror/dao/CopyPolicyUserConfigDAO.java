package com.huawei.sharedrive.app.mirror.dao;

import com.huawei.sharedrive.app.mirror.domain.CopyPolicyUserConfig;

import pw.cdmi.box.dao.BaseDAO;

public interface CopyPolicyUserConfigDAO extends BaseDAO<CopyPolicyUserConfig, Integer>
{
    
   /**
    * 获取 类型
    * @param policy
    * @param userType
    * @param userId
    */
    CopyPolicyUserConfig getCopyPolicyUserConfig(int policy,int userType,long userId);
    
    
    /**
     * 删除具体用户的策略配置
     * @param config
     * @return
     */
    int delete(CopyPolicyUserConfig config);
    
    /**
     * 删除某策略的所有用户配置
     * @param policy
     * @return
     */
    int deleteByPolicy(int policy);
    
    /**
     * 删除某策略的类型配置
     * @param policy
     * @param userType
     * @return
     */
    int deleteByPolicyAndUserType(int policy,int userType);
    
    
    
    
}
