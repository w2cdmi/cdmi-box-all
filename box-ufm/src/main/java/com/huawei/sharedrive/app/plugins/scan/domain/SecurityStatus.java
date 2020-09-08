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
 * 文件安全状态枚举值。文件安全状态按位组织，每两位表示一种状态； 低位表示是否处理（0表示未处理，1表示处理），高位表示处理结果（0表示非KIA文件，1表示是KIA文件）
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-4-21
 * @see
 * @since
 */
public enum SecurityStatus
{
	/** 未完成KIA扫描 */
    SECURITY_COMPLETED("allfalse", -1),
	
    /** 未完成KIA扫描 */
    KIA_UMCOMPLETED("failed", 0),
    
    /** 已完成KIA扫描, 非KIA文件 */
    KIA_COMPLETED_SECURE("false", 1),
    
    /** 已完成KIA扫描, KIA文件 */
    KIA_COMPLETED_INSECURE("true", 3),
	
	/** 未完成KIA扫描 */
    KSOFT_UMCOMPLETED("KsoftFailed", 4),
    
	/** 未完成KIA扫描 */
    KSOFT_COMPLETED_SECURE("KsoftFalse", 5),
    
	/** 未完成KIA扫描 */
    KSOFT_COMPLETED_INSECURE("KsoftTrue", 6);
    
	private static final int MASK_VALUE = 3;
	
	private static final int MASK_LENGTH = 2;
	
    private String desc;
    
    private int status;
    
    private SecurityStatus(String desc, int status)
    {
        this.desc = desc;
        this.status = status;
    }
    
    public static SecurityStatus getSecurityStatus(int status)
    {
    	int kiaStatus = status & MASK_VALUE;
    	
        for (SecurityStatus securityStatus : SecurityStatus.values())
        {
            if (securityStatus.getStatus() == kiaStatus)
            {
                return securityStatus;
            }
        }
        return null;
    }
    
    public static SecurityStatus getSecurityStatus(String desc)
    {
        for (SecurityStatus securityStatus : SecurityStatus.values())
        {
            if (securityStatus.getDesc().equals(desc))
            {
                return securityStatus;
            }
        }
        return null;
    }
    
    public static SecurityStatus getKsoftSecurityStatus(int status)
    {
    	int ksoftStatus = (status >> MASK_LENGTH) & MASK_VALUE;
    	
    	if( 0 == ksoftStatus)
    	{
    		return SecurityStatus.KSOFT_UMCOMPLETED;
    	}
       
    	if( 1 == ksoftStatus)
    	{
    		return SecurityStatus.KSOFT_COMPLETED_SECURE;
    	}
    	
    	if( 3 == ksoftStatus)
    	{
    		return SecurityStatus.KSOFT_COMPLETED_INSECURE;
    	}
    	
        return null;
    }
    
    public static SecurityStatus getKsoftSecurityStatus(String desc)
    {
        for (SecurityStatus securityStatus : SecurityStatus.values())
        {
            if (securityStatus.getDesc().equals(desc))
            {
                return securityStatus;
            }
        }
        return null;
    }
    
    public String getDesc()
    {
        return desc;
    }
    
    public int getStatus()
    {
        return status;
    }
    
}
