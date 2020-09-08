package com.huawei.sharedrive.app.openapi.domain.account;

import com.huawei.sharedrive.app.account.domain.AccountAccessKey;

import pw.cdmi.core.utils.DateUtils;

public class RestAccountAccessKey
{
    /** 接入凭证ID */
    private String id;
    
    /** 接入密钥 */
    private String secretKey;
    
    /** 企业ID */
    private Long accountId;
    
    /** 创建时间 */
    private Long createdAt;
    
    public RestAccountAccessKey(AccountAccessKey accessKey)
    {
        id = accessKey.getId();
        secretKey = accessKey.getSecretKey();
        accountId = accessKey.getAccountId();
        createdAt = DateUtils.getDateTime(accessKey.getCreatedAt()) / 1000 * 1000;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getSecretKey()
    {
        return secretKey;
    }
    
    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }
    
    public Long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }
    
    public Long getCreatedAt()
    {
        return createdAt;
    }
    
    public void setCreatedAt(Long createdAt)
    {
        this.createdAt = createdAt;
    }
    
}
