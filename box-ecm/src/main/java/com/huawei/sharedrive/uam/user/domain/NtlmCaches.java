package com.huawei.sharedrive.uam.user.domain;

import java.io.Serializable;

public class NtlmCaches implements Serializable
{
    private static final long serialVersionUID = -8131345362006602170L;
    
    private byte[] serverChallenge;
    
    private String currentServer;
    
    private String currentNetBios;
    
    private String ip;
    
    public byte[] getServerChallenge()
    {
        if (null != serverChallenge)
        {
            return serverChallenge.clone();
        }
        return new byte[]{};
    }
    
    public void setServerChallenge(byte[] serverChallenge)
    {
        if (null != serverChallenge)
        {
            this.serverChallenge = serverChallenge.clone();
        }
        else
        {
            this.serverChallenge = null;
        }
    }
    
    public String getCurrentServer()
    {
        return currentServer;
    }
    
    public void setCurrentServer(String currentServer)
    {
        this.currentServer = currentServer;
    }
    
    public String getCurrentNetBios()
    {
        return currentNetBios;
    }
    
    public void setCurrentNetBios(String currentNetBios)
    {
        this.currentNetBios = currentNetBios;
    }
    
    public String getIp()
    {
        return ip;
    }
    
    public void setIp(String ip)
    {
        this.ip = ip;
    }
    
}
