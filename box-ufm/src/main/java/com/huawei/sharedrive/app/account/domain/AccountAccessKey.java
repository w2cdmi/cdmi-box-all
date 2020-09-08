package com.huawei.sharedrive.app.account.domain;

import java.io.Serializable;
import java.util.Date;

public class AccountAccessKey implements Serializable
{
    
    private static final long serialVersionUID = -5432398849848078935L;

    public static final String CACHE_KEY_PREFIX_ID = "account_access_key_id_";
    
    /** 接入凭证ID */
    private String id;
    
    /** 接入密钥 */
    private String secretKey;
    
    /** 接入密钥的工作密钥 */
    private String secretKeyEncodeKey;
    
    /** 企业ID */
    private long accountId;
    
    /** 创建时间 */
    private Date createdAt;
    
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

    public String getSecretKeyEncodeKey() {
        return secretKeyEncodeKey;
    }

    public void setSecretKeyEncodeKey(String secretKeyEncodeKey) {
        this.secretKeyEncodeKey = secretKeyEncodeKey;
    }

    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (createdAt == null)
        {
            this.createdAt = null;
        }
        else
        {
            this.createdAt = (Date) createdAt.clone();
        }
    }
    
}
