package pw.cdmi.box.disk.httpclient.rest.request;

import java.io.Serializable;

public class NtlmGetChallengeRequest implements Serializable
{
    private static final long serialVersionUID = 4328857837198893922L;
    
    private String usernameHash;
    
    private String key;
    
    private String appId;
    
    private String challenge;
    
    public String getUsernameHash()
    {
        return usernameHash;
    }
    
    public void setUsernameHash(String usernameHash)
    {
        this.usernameHash = usernameHash;
    }
    
    public String getKey()
    {
        return key;
    }
    
    public void setKey(String key)
    {
        this.key = key;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public String getChallenge()
    {
        return challenge;
    }
    
    public void setChallenge(String challenge)
    {
        this.challenge = challenge;
    }
    
}
