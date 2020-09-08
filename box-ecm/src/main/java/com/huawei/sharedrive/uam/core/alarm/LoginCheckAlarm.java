package com.huawei.sharedrive.uam.core.alarm;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.common.alarm.Alarm;

public class LoginCheckAlarm extends Alarm
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 2607779973580139626L;
    
    public static final String USER_LOGING_ID = "0x100B02230101L";
    
    public static final String SYSTEM_LOGING_ID = "0x100B02230100L";
    
    private long loinMaxSize;
    
    public LoginCheckAlarm(String alarmID, int alarmType, int alarmLevel, String serviceName)
    {
        super(alarmID, alarmType, alarmLevel, serviceName);
    }
    
    public LoginCheckAlarm(Alarm alarm, long loinMaxSize)
    {
        super(alarm.getAlarmID(), alarm.getAlarmType(), alarm.getAlarmLevel(), alarm.getServiceName());
        this.loinMaxSize = loinMaxSize;
    }
    
    @Override
    public String getKey()
    {
        StringBuilder sb = new StringBuilder(String.valueOf(this.getAlarmID())).append(this.getHostName())
            .append(this.getServiceName())
            .append(this.getLoinMaxSize())
            .append(System.currentTimeMillis());
        return sb.toString();
    }
    
    @Override
    public String getParameter()
    {
        StringBuilder sb = new StringBuilder(StringUtils.trimToEmpty(this.getHostName())).append(PARAMETERS_SPLIT)
            .append(StringUtils.trimToEmpty(this.getServiceName()))
            .append(PARAMETERS_SPLIT)
            .append(this.getLoinMaxSize());
        return sb.toString();
    }
    
    public long getLoinMaxSize()
    {
        return loinMaxSize;
    }
    
    public void setLoinMaxSize(long loinMaxSize)
    {
        this.loinMaxSize = loinMaxSize;
    }
    
}
