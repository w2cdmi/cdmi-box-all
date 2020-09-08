package com.huawei.sharedrive.uam.user.domain;

import java.io.Serializable;
import java.util.Arrays;

@SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
public class UserImage implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -2998544504532632934L;
    
    private long userId;
    
    private long accountId;
    
    private byte[] userImage;
    
    private String imageFormatName;
    
    private String tableSuffix;
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public byte[] getUserImage()
    {
        if (null != userImage)
        {
            return userImage.clone();
        }
        return new byte[]{};
    }
    
    public void setUserImage(byte[] userImage)
    {
        if (null != userImage)
        {
            this.userImage = userImage.clone();
        }
        else
        {
            this.userImage = null;
        }
    }
    
    public String getImageFormatName()
    {
        return imageFormatName;
    }
    
    public void setImageFormatName(String imageFormatName)
    {
        this.imageFormatName = imageFormatName;
    }
    
    public String getTableSuffix()
    {
        return tableSuffix;
    }
    
    public void setTableSuffix(String tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
    @Override
    public String toString()
    {
        return "UserImage [userId=" + userId + ", accountId=" + accountId + ", userImage="
            + Arrays.toString(userImage) + ", imageFormatName=" + imageFormatName + "]";
    }
    
}
