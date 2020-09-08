package com.huawei.sharedrive.app.account.service;

import com.huawei.sharedrive.app.account.domain.Account;

public interface AccountService
{
    void create(Account account);
    
    int update(Account account);
    
    /**
     * 通过企业二级域名获取企业
     * 
     * @param domain
     * @return
     */
    Account getByDomain(String domain);
    
    /***
     * 通过ID获取企业
     * 
     * @param accountId
     * @return
     */
    Account getById(long accountId);
    
    /**
     * 查询指定的AppId的Account,按升序方式查询
     * 
     * @param appid
     * @return
     */
    Account getOneAccountOrderByACS(String appId, long id);
    
    Account updateCurrentMember(Account account);
    
    Boolean isUserExceed(Account account);
    
    Boolean isTeamspaceExceed(Account account);
    
    Account updateCurrentTeamspace(Account account);
}
