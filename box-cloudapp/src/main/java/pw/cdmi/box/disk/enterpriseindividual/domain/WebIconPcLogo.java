package pw.cdmi.box.disk.enterpriseindividual.domain;

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
        return webLogo != null ? webLogo.clone() : new byte[0]; 
    }
    
    public void setWebLogo(byte[] webLogo)
    {
        if (webLogo != null)
        {
            this.webLogo = webLogo.clone();
        }
    }
    
    public byte[] getPcLogo()
    {
        return pcLogo != null ? pcLogo.clone() : new byte[0];
    }
    
    public void setPcLogo(byte[] pcLogo)
    {
        if (pcLogo != null)
        {
            this.pcLogo = pcLogo.clone();
        }
    }
    
    public byte[] getIcon()
    {
        return icon != null ? icon.clone() : new byte[0];
    }
    
    public void setIcon(byte[] icon)
    {
        if (icon != null)
        {
            this.icon = icon.clone();
        }
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
