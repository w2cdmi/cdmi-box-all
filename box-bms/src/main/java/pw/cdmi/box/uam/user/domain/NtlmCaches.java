package pw.cdmi.box.uam.user.domain;

import java.io.Serializable;

public class NtlmCaches implements Serializable
{
    private static final long serialVersionUID = -8131345362006602170L;
    
    private byte[] serverChallenge;
    
    private String currentServer;
    
    private String currentNetBios;
    
    public byte[] getServerChallenge()
    {
        return serverChallenge == null ? null : serverChallenge.clone();
    }
    
    public void setServerChallenge(byte[] serverChallenge)
    {
        this.serverChallenge = (serverChallenge == null ? null : serverChallenge.clone());
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
    
}
