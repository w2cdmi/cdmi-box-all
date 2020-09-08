package pw.cdmi.box.uam.system.domain;

import java.io.Serializable;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class MailServer implements Serializable
{
    private static final long serialVersionUID = 5040414680003662430L;
    
    private long id;
    
    @NotBlank
    @Size(min = 1, max = 255)
    private String server;
    
    @Min(value = 1)
    @Max(value = 65535)
    private int port;
    
    @NotBlank
    @Size(min = 5, max = 255)
    private String senderMail;
    
    @NotBlank
    @Size(min = 1, max = 255)
    private String senderName;
    
    @Size(max = 255)
    private String testMail;
    
    private boolean enableAuth;
    
    @Size(max = 255)
    private String authUsername;
    
    @Size(max = 127)
    private String authPassword;
    
    private String authPasswordEncodeKey;
    
    private boolean defaultFlag;
    
    private String mailSecurity;
    
    @Digits(fraction = 0, integer = 10)
    private int sslPort;
    
    private String appId;
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public String getServer()
    {
        return server;
    }
    
    public void setServer(String server)
    {
        this.server = server;
    }
    
    public int getPort()
    {
        return port;
    }
    
    public void setPort(int port)
    {
        this.port = port;
    }
    
    public String getSenderMail()
    {
        return senderMail;
    }
    
    public void setSenderMail(String senderMail)
    {
        this.senderMail = senderMail;
    }
    
    public String getSenderName()
    {
        return senderName;
    }
    
    public void setSenderName(String senderName)
    {
        this.senderName = senderName;
    }
    
    public boolean isEnableAuth()
    {
        return enableAuth;
    }
    
    public void setEnableAuth(boolean enableAuth)
    {
        this.enableAuth = enableAuth;
    }
    
    public String getAuthUsername()
    {
        return authUsername;
    }
    
    public void setAuthUsername(String authUsername)
    {
        this.authUsername = authUsername;
    }
    
    public String getAuthPassword()
    {
        return authPassword;
    }
    
    public void setAuthPassword(String authPassword)
    {
        this.authPassword = authPassword;
    }
    
    public boolean isDefaultFlag()
    {
        return defaultFlag;
    }
    
    public void setDefaultFlag(boolean defaultFlag)
    {
        this.defaultFlag = defaultFlag;
    }
    
    public String getMailSecurity()
    {
        return mailSecurity;
    }
    
    public void setMailSecurity(String mailSecurity)
    {
        this.mailSecurity = mailSecurity;
    }
    
    public int getSslPort()
    {
        return sslPort;
    }
    
    public void setSslPort(int sslPort)
    {
        this.sslPort = sslPort;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public String getTestMail()
    {
        return testMail;
    }
    
    public void setTestMail(String testMail)
    {
        this.testMail = testMail;
    }
    
    public String getAuthPasswordEncodeKey()
    {
        return authPasswordEncodeKey;
    }
    
    public void setAuthPasswordEncodeKey(String authPasswordEncodeKey)
    {
        this.authPasswordEncodeKey = authPasswordEncodeKey;
    }
}
