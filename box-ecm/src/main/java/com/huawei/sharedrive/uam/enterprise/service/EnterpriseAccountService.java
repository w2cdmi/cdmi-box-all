package com.huawei.sharedrive.uam.enterprise.service;

import java.util.List;

import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

public interface EnterpriseAccountService
{
    
    List<EnterpriseAccount> getByEnterpriseId(long enterpriseId);
    
    int deleteByAccountId(long accountId);
    
    void create(EnterpriseAccount enterpriseAccount);
    
    List<String> getAppByEnterpriseId(long enterpriseId);
    
    List<Long> getAccountIdByEnterpriseId(long enterpriseId);
    
    EnterpriseAccount getByEnterpriseApp(long enterpriseId, String authAppId);
    
    List<EnterpriseAccount> getAppContextByEnterpriseId(long enterpriseId);
    
    EnterpriseAccount getByAccessKeyId(String accessKeyId);

    EnterpriseAccount getByAccountId(long accountId);
  //新增按企业设置登录密码复杂度
    void setPwdLevelByEnterpriseId(long enterpriseId,int pwdLevel);
    
    void modifyPwdLevelByEnterpriseId(long enterpriseId,int pwdLevel);
    
    String getPwdLevelByEnterpriseId(long enterpriseId);

	void update(EnterpriseAccount enterpriseAccount);
}
