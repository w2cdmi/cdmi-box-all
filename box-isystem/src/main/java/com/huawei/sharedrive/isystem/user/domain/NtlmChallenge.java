package com.huawei.sharedrive.isystem.user.domain;

import java.io.Serializable;

public class NtlmChallenge implements Serializable
{
    private static final long serialVersionUID = -8131345362006602170L;
    
    private String hashId;
    
    private byte[] serverChallenge;
    
    public String getHashId()
    {
        return hashId;
    }
    
    public void setHashId(String hashId)
    {
        this.hashId = hashId;
    }
    
    public byte[] getServerChallenge()
    {
        return serverChallenge != null ? serverChallenge.clone() : new byte[0];
    }
    
    public void setServerChallenge(byte[] serverChallenge)
    {
        if (serverChallenge != null)
        {
            this.serverChallenge = serverChallenge.clone(); 
        }
    }
    
}
