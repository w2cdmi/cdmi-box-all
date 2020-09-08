package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;

public class RestNtlmGetChallengeResponse implements Serializable
{
    private static final long serialVersionUID = -2980939423459232855L;
    
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
