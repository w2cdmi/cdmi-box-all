package com.huawei.sharedrive.app.plugins.scan.domain;

/**
 * 文件病毒扫描状态枚举值。病毒扫描状态按位组织，每两位表示一种状态；
 * 低位表示是否处理（0表示未处理，1表示处理），高位表示处理结果（0表示非病毒文件，1表示是病毒文件）
 * 
 * @author sunxuekun
 * @version 
 * @see
 * @since
 */
public enum KsoftSecurityStatus
{
    /** 未完成病毒扫描 */
    KSOFT_UMCOMPLETED("failed", 0),
    
    /** 已完成病毒扫描, 非病毒文件 */
    KSOFT_COMPLETED_SECURE("false", 1),
    
    /** 已完成病毒扫描, 病毒文件 */
    KSOFT_COMPLETED_INSECURE("true", 3);
    
    private String desc;

    private int status;
    
    
    private KsoftSecurityStatus(String desc, int status)
    {
        this.desc = desc;
        this.status = status;
    }
    
    public static KsoftSecurityStatus getSecurityStatus(int status)
    {
        for (KsoftSecurityStatus securityStatus : KsoftSecurityStatus.values())
        {
            if (securityStatus.getStatus() == status)
            {
                return securityStatus;
            }
        }
        return null;
    }
    
    public static KsoftSecurityStatus getSecurityStatus(String desc)
    {
        for (KsoftSecurityStatus securityStatus : KsoftSecurityStatus.values())
        {
            if (securityStatus.getDesc().equals(desc))
            {
                return securityStatus;
            }
        }
        return null;
    }
    
    public int getStatus()
    {
        return status;
    }

    public String getDesc()
    {
        return desc;
    }
    
}
