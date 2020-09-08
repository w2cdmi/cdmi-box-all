package com.huawei.sharedrive.uam.enterprise.manager;

import java.util.List;

import pw.cdmi.common.domain.AccountConfig;
public interface AccountConfigManager
{
    void configEmailReceive(long accountId, String[] arrConfig);
    
    void configEmailSend(long accountId, String[] arrConfig);
    
    AccountConfig get(long accountId, String name);
    
    List<AccountConfig> list(long accountId);

//	void configfunctionParameter(long accountId, String[] arrConfig);

	void createOrUpdate(AccountConfig accountConfig);

	void createOrUpdate(long accountId, String name, String value);

	List<AccountConfig> getAll(long accountId);
}
