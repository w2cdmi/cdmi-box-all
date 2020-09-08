package com.huawei.sharedrive.app.group.domain;

import java.util.ArrayList;
import java.util.List;

public final class GroupConstants
{
    public static final String DIRECT_DEFAULT = "DESC";
    
    public static final String OPER_APP = "app";
    
    public static final String RANGE_DEFAULT = "mine";
    
    public static final int MAXMEMBERS_DEFAULT = 99999999;
    
    public static final int REQUEST_MAXMEMBERS = -1;
    
    public static final int GROUP_LIMIT_DEFAULT = 1000;
    
    public static final long GROUP_OFFSET_DEFAULT = 0;
    
    public static final long GROUP_PARENT_DEFAULT = 0;
    
    public static final byte GROUP_ROLE_ADMIN = 0;
    
    public static final byte GROUP_ROLE_MANAGER = 1;
    
    public static final byte GROUP_ROLE_MEMBER = 2;
    
    public static final byte GROUP_STATUS_DEFAULT = 0;
    
    public static final byte GROUP_STATUS_DISABLE = 1;
    
    public static final byte GROUP_TYPE_DEFAULT = 0;
    
    public static final byte GROUP_TYPE_PUBLIC = 1;
    
    public static final byte GROUP_USERTYPE_GROUP = 1;
    
    public static final byte GROUP_USERTYPE_USER = 0;
    
    public static final String ROLE_ADMIN = "admin";
    
    public static final String ROLE_MANAGER = "manager";
    
    public static final String ROLE_MEMBER = "member";
    
    public static final String STATUS_DISABLE = "disable";
    
    public static final String STATUS_ENABLE = "enable";
    
    public static final String TYPE_DEFAULT = "modifiedAt";
    
    public static final String TYPE_ALL = "all";
    
    public static final String TYPE_PRIVATE = "private";
    
    public static final String TYPE_PUBLIC = "public";
    
    public static final String USERTYPE_GROUP = "group";
    
    public static final String USERTYPE_USER = "user";
    
    public static final String CACHE_MEMBER_GROUP = "mb_g_";
    
    public static final long DEFAULT_OFFSET = 0L;
    
    public static final int DEFAULT_LENGTH = 10000;
    
    private static List<String> allRoleList = null;
    
    private GroupConstants()
    {
    }
    
    public static boolean belongAllRole(String role)
    {
        if (null != allRoleList)
        {
            return allRoleList.contains(role);
        }
        synchronized (GroupConstants.class)
        {
            if (null == allRoleList)
            {
                allRoleList = new ArrayList<String>(4);
                allRoleList.add(ROLE_ADMIN);
                allRoleList.add(ROLE_MANAGER);
                allRoleList.add(ROLE_MEMBER);
                allRoleList.add(TYPE_ALL);
            }
        }
        return allRoleList.contains(role);
    }
    
    private static List<String> allTypeList = null;
    
    public static boolean belongAllType(String type)
    {
        if (null != allTypeList)
        {
            return allTypeList.contains(type);
        }
        synchronized (GroupConstants.class)
        {
            if (null == allTypeList)
            {
                allTypeList = new ArrayList<String>(4);
                allTypeList.add(TYPE_PRIVATE);
                allTypeList.add(TYPE_PUBLIC);
                allTypeList.add(TYPE_ALL);
            }
        }
        return allTypeList.contains(type);
    }
    
    private static List<String> userTypeList = null;
    
    public static boolean belongUserType(String userType)
    {
        if (null != userTypeList)
        {
            return userTypeList.contains(userType);
        }
        synchronized (GroupConstants.class)
        {
            if (null == userTypeList)
            {
                userTypeList = new ArrayList<String>(4);
                userTypeList.add(USERTYPE_GROUP);
                userTypeList.add(USERTYPE_USER);
            }
        }
        return userTypeList.contains(userType);
    }
    
    public static String getRoleStr(byte role)
    {
        String roleStr = null;
        switch (role)
        {
            case 0:
                roleStr = ROLE_ADMIN;
                break;
            case 1:
                roleStr = ROLE_MANAGER;
                break;
            case 2:
                roleStr = ROLE_MEMBER;
                break;
            default:
                roleStr = String.valueOf(role);
                break;
        
        }
        return roleStr;
    }
    
    public static final String LIST_ROLE = "true";
    
}
