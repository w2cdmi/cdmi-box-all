package com.huawei.sharedrive.app.mirror.domain;

import java.io.Serializable;

/**
 * 
 * @author c00287749
 * 
 */
public class CopyPolicyUserConfig implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 8836244451207006176L;

    private int policyId;
    
    private int userType;
    
    private long userId;
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public static final long DEFAULT_USER_ID = 0L;
    
    public static final int USER_TYPE_TEAMSPACE = 1;
    
    public static final int USER_TYPE_SINGLE = 0;
    
    public final static String CACHE_PREFIX = "COPYPOLICYUSERCONFIG_";
    
    public int getPolicyId()
    {
        return policyId;
    }
    
    public void setPolicyId(int policyId)
    {
        this.policyId = policyId;
    }
    
    public int getUserType()
    {
        return userType;
    }
    
    public void setUserType(int userType)
    {
        this.userType = userType;
    }
    
    /**
     * 缓存KEY
     * 
     * @return
     */
    public String getKey()
    {
        return CACHE_PREFIX + this.policyId + '_' + this.userType + '_' + this.userId;
    }
    
}
