package com.huawei.sharedrive.uam.enterpriseuser.domain;

import java.io.Serializable;

public class ImportEnterpriseUserRecord implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private int totalUserNumber;
    
    private int newUserNumber;
    
    private int failUserNumber;
    
    private int updateUserNumber;
    
    private int newAccountUser;
    
    private int failAccountUser;
    
    private int updateAccountUser;
    
    private int totalAccountUser;
    
    public int getTotalUserNumber()
    {
        return totalUserNumber;
    }
    
    public void setTotalUserNumber(int totalUserNumber)
    {
        this.totalUserNumber = totalUserNumber;
    }
    
    public int getNewUserNumber()
    {
        return newUserNumber;
    }
    
    public void setNewUserNumber(int newUserNumber)
    {
        this.newUserNumber = newUserNumber;
    }
    
    public int getFailUserNumber()
    {
        return failUserNumber;
    }
    
    public void setFailUserNumber(int failUserNumber)
    {
        this.failUserNumber = failUserNumber;
    }
    
    public int getUpdateUserNumber()
    {
        return updateUserNumber;
    }
    
    public void setUpdateUserNumber(int updateUserNumber)
    {
        this.updateUserNumber = updateUserNumber;
    }
    
    public int getNewAccountUser()
    {
        return newAccountUser;
    }
    
    public void setNewAccountUser(int newAccountUser)
    {
        this.newAccountUser = newAccountUser;
    }
    
    public int getFailAccountUser()
    {
        return failAccountUser;
    }
    
    public void setFailAccountUser(int failAccountUser)
    {
        this.failAccountUser = failAccountUser;
    }
    
    public int getUpdateAccountUser()
    {
        return updateAccountUser;
    }
    
    public void setUpdateAccountUser(int updateAccountUser)
    {
        this.updateAccountUser = updateAccountUser;
    }
    
    public int getTotalAccountUser()
    {
        return totalAccountUser;
    }
    
    public void setTotalAccountUser(int totalAccountUser)
    {
        this.totalAccountUser = totalAccountUser;
    }
}
