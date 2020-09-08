package pw.cdmi.box.disk.httpclient.rest.request;

import java.io.Serializable;

public class NtlmGetChallengeResponse implements Serializable
{
    private static final long serialVersionUID = -9163262380383972437L;
    
    private String challenge;
    
    public String getChallenge()
    {
        return challenge;
    }
    
    public void setChallenge(String challenge)
    {
        this.challenge = challenge;
    }
    
}
