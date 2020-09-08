/**
 * 常量类
 */
package com.huawei.sharedrive.isystem.util;

/**
 * @author d00199602
 *         
 */
public final class Constants
{
    private Constants()
    {
        
    }
    
    
    /**
     * UDS存储路径分隔符
     */
    public final static String UDS_STORAGE_SPLIT_CHAR = ":";
    
    /**
     * 普通管理员标识
     */
    public static final byte ROLE_COMMON_ADMIN = (byte) 1;
    
    /**
     * 超级管理员标识
     */
    public static final byte ROLE_SUPER_ADMIN = (byte) -1;
    
    /**
     * 账户来源类型：本地账户
     */
    public static final byte DOMAIN_TYPE_LOCAL = (byte) 1;
    
    /**
     * 验证码常量
     */
    public static final String HW_VERIFY_CODE_CONST = "HWVerifyCode";
    
    /**
     * 会话对象KEY
     */
    public static final String SESS_OBJ_KEY = "session.user.id";
    
    /**
     * 会话中缓存当前用户角色
     */
    public static final String SESS_ROLE_KEY = "session.user.roles";
    
    /** 缓存当前用户名 */
    public static final String SESS_USER_NAME = "name";
    
    public static final String DISPLAY_STAR_VALUE = "*****************************";
    
}
