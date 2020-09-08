package pw.cdmi.box.disk.user.domain;

import java.io.Serializable;
import java.util.Arrays;

@SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
public class UserImage implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -2998544504532632934L;
    
    private long accountId;
    
    private String imageFormatName;
    
    private String tableSuffix;
    
    private long userId;
    
    private byte[] userImage;
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public String getImageFormatName()
    {
        return imageFormatName;
    }
    
    public String getTableSuffix()
    {
        return tableSuffix;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    @SuppressWarnings("PMD.ReturnEmptyArrayRatherThanNull")
    public byte[] getUserImage()
    {
        return userImage != null ? userImage.clone() : new byte[0];
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public void setImageFormatName(String imageFormatName)
    {
        this.imageFormatName = imageFormatName;
    }
    
    public void setTableSuffix(String tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public void setUserImage(byte[] userImage)
    {
        if (userImage != null)
        {
            this.userImage = userImage.clone();
        }
    }
    
    @Override
    public String toString()
    {
        return "UserImage [userId=" + userId + ", accountId=" + accountId + ", userImage="
            + Arrays.toString(userImage) + ", imageFormatName=" + imageFormatName + "]";
    }
    
}
