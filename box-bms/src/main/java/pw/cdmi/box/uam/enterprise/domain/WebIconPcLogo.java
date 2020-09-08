package pw.cdmi.box.uam.enterprise.domain;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class WebIconPcLogo implements Serializable
{
    private static final long serialVersionUID = 6715844470358523896L;
    
    private Long accountId;
    
    @NotBlank
    @Size(min = 1, max = 120)
    private String titleCh;
    
    @NotBlank
    @Size(min = 1, max = 120)
    private String titleEn;
    
    private byte[] webLogo;
    
    private byte[] pcLogo;
    
    private byte[] icon;
    
    private String webLogoFormatName;
    
    private String pcLogoFormatName;
    
    private String iconFormatName;
    
    public Long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }
    
    public String getTitleCh()
    {
        return titleCh;
    }
    
    public void setTitleCh(String titleCh)
    {
        this.titleCh = titleCh;
    }
    
    public String getTitleEn()
    {
        return titleEn;
    }
    
    public void setTitleEn(String titleEn)
    {
        this.titleEn = titleEn;
    }
    
    public byte[] getWebLogo()
    {
        return webLogo == null ? null : webLogo.clone();
    }
    
    public void setWebLogo(byte[] webLogo)
    {
        this.webLogo = (webLogo == null ? null : webLogo.clone());
    }
    
    public byte[] getPcLogo()
    {
        return pcLogo == null ? null : pcLogo.clone();
    }
    
    public void setPcLogo(byte[] pcLogo)
    {
        this.pcLogo = (pcLogo == null ? null : pcLogo.clone());
    }
    
    public byte[] getIcon()
    {
        return icon == null ? null : icon.clone();
    }
    
    public void setIcon(byte[] icon)
    {
        this.icon = (icon == null ? null : icon.clone());
    }
    
    public String getWebLogoFormatName()
    {
        return webLogoFormatName;
    }
    
    public void setWebLogoFormatName(String webLogoFormatName)
    {
        this.webLogoFormatName = webLogoFormatName;
    }
    
    public String getPcLogoFormatName()
    {
        return pcLogoFormatName;
    }
    
    public void setPcLogoFormatName(String pcLogoFormatName)
    {
        this.pcLogoFormatName = pcLogoFormatName;
    }
    
    public String getIconFormatName()
    {
        return iconFormatName;
    }
    
    public void setIconFormatName(String iconFormatName)
    {
        this.iconFormatName = iconFormatName;
    }
    
}
