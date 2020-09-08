package com.huawei.sharedrive.uam.enterprise.domain;

import java.io.Serializable;

import org.springframework.web.util.HtmlUtils;

@Deprecated
public class AccountBasicConfig implements Serializable
{
    private static final long serialVersionUID = -5686797209700515187L;
    
    public static int DEFAULTVERSONS=150;
    
    private long accountId;
    
    private String userSpaceQuota;
    
    private String userVersions;
    
    private boolean enableTeamSpace;
    
    private String maxTeamSpaces;
    
    private String teamSpaceQuota;
    
    private String teamSpaceVersions;
    
    private String maxVersionFileSize;
    
    private String multiVersionContent;
    
    public String getMaxVersionFileSize() {
		return maxVersionFileSize;
	}

	public void setMaxVersionFileSize(String maxVersionFileSize) {
		this.maxVersionFileSize = maxVersionFileSize;
	}

	public String getMultiVersionContent() {
		return multiVersionContent;
	}

	public void setMultiVersionContent(String multiVersionContent) {
		this.multiVersionContent = multiVersionContent;
	}

    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public String getUserSpaceQuota()
    {
        return userSpaceQuota;
    }
    
    public void setUserSpaceQuota(String userSpaceQuota)
    {
        this.userSpaceQuota = userSpaceQuota;
    }
    
    public String getUserVersions()
    {
        return userVersions;
    }
    
    public void setUserVersions(String userVersions)
    {
        this.userVersions = userVersions;
    }
    
    public String getMaxTeamSpaces()
    {
        return maxTeamSpaces;
    }
    
    public void setMaxTeamSpaces(String maxTeamSpaces)
    {
        this.maxTeamSpaces = maxTeamSpaces;
    }
    
    public String getTeamSpaceQuota()
    {
        return teamSpaceQuota;
    }
    
    public void setTeamSpaceQuota(String teamSpaceQuota)
    {
        this.teamSpaceQuota = teamSpaceQuota;
    }
    
    public String getTeamSpaceVersions()
    {
        return teamSpaceVersions;
    }
    
    public void setTeamSpaceVersions(String teamSpaceVersions)
    {
        this.teamSpaceVersions = teamSpaceVersions;
    }
    
    public boolean isEnableTeamSpace()
    {
        return enableTeamSpace;
    }
    
    public void setEnableTeamSpace(boolean enableTeamSpace)
    {
        this.enableTeamSpace = enableTeamSpace;
    }
    
    public AccountBasicConfig()
    {
        
    }
    
    public static void htmlEscape(AccountBasicConfig basicConfig)
    {
        basicConfig.setUserSpaceQuota(HtmlUtils.htmlEscape(basicConfig.getUserSpaceQuota()));
        basicConfig.setUserVersions(HtmlUtils.htmlEscape(basicConfig.getUserVersions()));
        boolean teamSpace = basicConfig.isEnableTeamSpace();
        String htmlEscape = HtmlUtils.htmlEscape(String.valueOf(teamSpace));
        basicConfig.setEnableTeamSpace(Boolean.parseBoolean(htmlEscape));
        basicConfig.setMaxTeamSpaces(HtmlUtils.htmlEscape(basicConfig.getMaxTeamSpaces()));
        basicConfig.setTeamSpaceQuota(HtmlUtils.htmlEscape(basicConfig.getTeamSpaceQuota()));
        basicConfig.setTeamSpaceVersions(HtmlUtils.htmlEscape(basicConfig.getTeamSpaceVersions()));
    }
    
    public AccountBasicConfig(AccountBasicConfig accountBasicConfig)
    {
        super();
        this.accountId = accountBasicConfig.getAccountId();
        this.userSpaceQuota = accountBasicConfig.getUserSpaceQuota();
        this.userVersions = accountBasicConfig.getUserVersions();
        this.enableTeamSpace = accountBasicConfig.isEnableTeamSpace();
        this.maxTeamSpaces = accountBasicConfig.getMaxTeamSpaces();
        this.teamSpaceQuota = accountBasicConfig.getTeamSpaceQuota();
        this.teamSpaceVersions = accountBasicConfig.getTeamSpaceVersions();
    }
    
}
