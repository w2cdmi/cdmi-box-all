package com.huawei.sharedrive.uam.openapi.domain;

import java.io.Serializable;

public class RestSystemConfig implements Serializable
{
    public static final String OPTION_ALL = "all";
    
    public static final String OPTION_LINK_ACCESSKEY_RULE = "linkAccessKeyRule";
    
    public static final String OPTION_SYSTEM_MAX_VERSIONS = "systemMaxVersions";
    
    public static final String OPTION_BACKUP_WHITE_RULE = "backupAllowedRule";
    
    public static final String OPTION_BACKUP_BLACK_RULE = "backupForbiddenRule";
    
    private static final long serialVersionUID = -3978463497011467127L;
    
    private String option;
    
    private String value;
    
    public String getOption()
    {
        return option;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setOption(String option)
    {
        this.option = option;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
    
}
