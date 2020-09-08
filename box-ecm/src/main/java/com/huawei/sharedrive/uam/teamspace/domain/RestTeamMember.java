package com.huawei.sharedrive.uam.teamspace.domain;

public class RestTeamMember {
    private String id;

    private String loginName;

    private String type;

    private String name;

    private String description;
    
    public final static String ID_PUBLIC = "1";
    
    public static final String TYPE_USER = "user";
    
    public static final String TYPE_GROUP = "group";
    
    public static final String TYPE_DEPT = "department";
    
    public static final String TYPE_LINK = "link";
    
    public static final String TYPE_TEAM = "team";
    
    public static final String TYPE_SYSTEM = "system";
    
    public static final String TYPE_PUBLIC = "public";
    
	public static final String TYPE_SECRET = "secret";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
