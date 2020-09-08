package com.huawei.sharedrive.app.teamspace.domain;

/**
 * 团队空间配置项枚举类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-5-9
 * @see
 * @since
 */
public enum TeamSpaceAttributeEnum
{
    /** 上传文件是否发送消息配置 */
    UPLOAD_NOTICE("uploadNotice"),
	
	/** 团队空间是否开启自动预览 */
	AUTO_PREVIEW("autoPreview");
    
    private String name;
    
    private TeamSpaceAttributeEnum(String name)
    {
        this.name = name;
    }
    
    public static TeamSpaceAttributeEnum getTeamSpaceConfig(String name)
    {
        for (TeamSpaceAttributeEnum config : TeamSpaceAttributeEnum.values())
        {
            if (config.getName().equals(name))
            {
                return config;
            }
        }
        return null;
    }
    
    public String getName()
    {
        return name;
    }
    
}
