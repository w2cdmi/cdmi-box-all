package com.huawei.sharedrive.app.teamspace.domain;

import java.io.Serializable;

/**
 * 团队空间扩展属性域对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-5-12
 * @see
 * @since
 */
public class TeamSpaceAttribute implements Serializable
{
    public static final String UPLOAD_NOTICE_ENABLE = "enable";
    
    public static final String UPLOAD_NOTICE_DISABLE = "disable";
    
//    public static final String HIGH_PRIORITY = "high";
//    
//    public static final String MEDIUM_PRIORITY = "medium";
//    
//    public static final String LOW_PRIORITY = "low";
 
    public static final String AUTO_PREVIEW_ENABLE = "enable";
    
    public static final String AUTO_PREVIEW_DISABLE = "disable";
    
//    public static final String HIGH_PRIORITY_VALUE = "2";
//    
//    public static final String MEDIUM_PRIORITY_VALUE = "1";
//    
//    public static final String LOW_PRIORITY_VALUE = "0";
// 
    public static final String AUTO_PREVIEW_ENABLE_VALUE = "1";
    
    public static final String AUTO_PREVIEW_DISABLE_VALUE = "0";
    
    private static final long serialVersionUID = 4260306419764569121L;
    
//    private long attributeId;
//    
    private long cloudUserId;
    
    private String name;
    
    private String value;
    
    public TeamSpaceAttribute()
    {
        
    }
    
    public TeamSpaceAttribute(String name, String value)
    {
        this.name = name;
        this.value = value;
    }
    
//    public TeamSpaceAttribute(long cloudUserId, String name) {
//		super();
//		this.cloudUserId = cloudUserId;
//		this.name = name;
//	}
//
	public TeamSpaceAttribute(long cloudUserId, String name, String value) {
		super();
		this.cloudUserId = cloudUserId;
		this.name = name;
		this.value = value;
	}
//
//	public TeamSpaceAttribute(long attributeId, long cloudUserId, String name, String value) {
//		super();
//		this.attributeId = attributeId;
//		this.cloudUserId = cloudUserId;
//		this.name = name;
//		this.value = value;
//	}

	public String getName()
    {
        return name;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }

	public long getCloudUserId() {
		return cloudUserId;
	}

	public void setCloudUserId(long cloudUserId) {
		this.cloudUserId = cloudUserId;
	}
    
}
