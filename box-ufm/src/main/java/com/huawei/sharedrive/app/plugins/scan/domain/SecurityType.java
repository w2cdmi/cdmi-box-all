/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.app.plugins.scan.domain;

/**
 * 安全扫描类型
 * 
 * @author  t90006461
 * @version  CloudStor CSE Service Platform Subproject, 2015-4-21
 * @see  
 * @since  
 */
public enum SecurityType
{
    
    /** KIA扫描 */
    CONFIDENTIAL("confidential"),
	
	/** KIA扫描 */
    KSOFT("ksoft");
    
    private String type;
    
    private SecurityType(String type)
    {
        this.type = type;
    }
    
    public static SecurityType getSecurityType(String type)
    {
        for (SecurityType securityType : SecurityType.values())
        {
            if (securityType.getType().equals(type))
            {
                return securityType;
            }
        }
        return null;
    }
    
    public String getType()
    {
        return type;
    }
    
}
