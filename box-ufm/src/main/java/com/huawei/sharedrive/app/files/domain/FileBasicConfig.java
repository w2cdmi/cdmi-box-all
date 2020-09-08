package com.huawei.sharedrive.app.files.domain;

import java.io.Serializable;
import java.util.Map;

import pw.cdmi.core.utils.JsonUtils;

public class FileBasicConfig implements Serializable
{
    private static final long serialVersionUID = 7276460897253404583L;
    
    public static final String FILE_BASIC_CONFIG = "file.basic.config";
    
    public static final String KEY_OVERWRITE_ACL = "overwriteAcl";
    
    public static final String KEY_ALLOW_PATCH = "allowBatch";
    
    public static final Boolean DEFAULT_OVERWRITE_ACL = false;
    
    public static final Boolean DEFAULT_ALLOW_PATCH = true;
    // 是否删除子节点小于父目录的acl
    private Boolean overwriteAcl = DEFAULT_OVERWRITE_ACL;
    
    //是否允许多元数据删除，如果目录中有子节点则不允许删除
    private Boolean allowBatch = DEFAULT_ALLOW_PATCH;
    
    public Boolean getOverwriteAcl()
    {
        return overwriteAcl;
    }
    
    public void setOverwriteAcl(Boolean overwriteAcl)
    {
        this.overwriteAcl = overwriteAcl;
    }
    
    public Boolean getAllowBatch()
    {
        return allowBatch;
    }
    
    public void setAllowBatch(Boolean allowBatch)
    {
        this.allowBatch = allowBatch;
    }
    
    public static FileBasicConfig buildConfigFromMap(Map<String, String> configMap)
    {
        if(configMap == null)
        {
            return null;
        }
        
        FileBasicConfig basicConfig = new FileBasicConfig();
        basicConfig.setOverwriteAcl(configMap.get(KEY_OVERWRITE_ACL) == null ? DEFAULT_OVERWRITE_ACL
            : Boolean.parseBoolean(configMap.get(KEY_OVERWRITE_ACL)));
        
        basicConfig.setAllowBatch(configMap.get(KEY_ALLOW_PATCH) == null ? DEFAULT_ALLOW_PATCH
            : Boolean.parseBoolean(configMap.get(KEY_ALLOW_PATCH)));
        return basicConfig;
    }
    
    public static FileBasicConfig buildConfigFromJson(String configStr)
    {
        return JsonUtils.stringToObject(configStr, FileBasicConfig.class);
    }
    
    public static String getJson(FileBasicConfig basicConfig)
    {
        return JsonUtils.toJson(basicConfig);
    }
}
