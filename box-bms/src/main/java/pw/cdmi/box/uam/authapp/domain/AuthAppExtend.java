package pw.cdmi.box.uam.authapp.domain;

import pw.cdmi.uam.domain.AuthApp;

public class AuthAppExtend extends AuthApp
{
    
    private static final long serialVersionUID = -6702248875596432506L;
    
    private String[] includedAppId;
    
    private String[] excludeAppId;
    
    private boolean filePreview;
    
    private long accountId;
    
    public String[] getIncludedAppId()
    {
        return includedAppId == null ? null : includedAppId.clone();
    }
    
    public void setIncludedAppId(String[] includedAppId)
    {
        this.includedAppId = (includedAppId == null ? null : includedAppId.clone());
    }
    
    public String[] getExcludeAppId()
    {
        return excludeAppId == null ? null : excludeAppId.clone();
    }
    
    public void setExcludeAppId(String[] excludeAppId)
    {
        this.excludeAppId = (excludeAppId == null ? null : excludeAppId.clone());
    }
    
    public boolean isFilePreview()
    {
        return filePreview;
    }
    
    public void setFilePreview(boolean filePreview)
    {
        this.filePreview = filePreview;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
}
