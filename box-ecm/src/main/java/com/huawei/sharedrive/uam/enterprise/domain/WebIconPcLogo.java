package com.huawei.sharedrive.uam.enterprise.domain;

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
    
    private String corprightEN;
    
    private String corprightCN;
    
    
    public String getCorprightEN() {
		return corprightEN;
	}

	public void setCorprightEN(String corprightEN) {
		this.corprightEN = corprightEN;
	}

	public String getCorprightCN() {
		return corprightCN;
	}

	public void setCorprightCN(String corprightCN) {
		this.corprightCN = corprightCN;
	}

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
        if (null != webLogo)
        {
            return webLogo.clone();
        }
        return new byte[]{};
    }
    
    public void setWebLogo(byte[] webLogo)
    {
        if (null != webLogo)
        {
            this.webLogo = webLogo.clone();
        }
        else
        {
            this.webLogo = null;
        }
    }
    
    public byte[] getPcLogo()
    {
        if (null != pcLogo)
        {
            return pcLogo.clone();
        }
        return new byte[]{};
    }
    
    public void setPcLogo(byte[] pcLogo)
    {
        if (null != pcLogo)
        {
            this.pcLogo = pcLogo.clone();
        }
        else
        {
            this.pcLogo = null;
        }
    }
    
    public byte[] getIcon()
    {
        if (null != icon)
        {
            return icon.clone();
        }
        return new byte[]{};
    }
    
    public void setIcon(byte[] icon)
    {
        if (null != icon)
        {
            this.icon = icon.clone();
        }
        else
        {
            this.icon = null;
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
