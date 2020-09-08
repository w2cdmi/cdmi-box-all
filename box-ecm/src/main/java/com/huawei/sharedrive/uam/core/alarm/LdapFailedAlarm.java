package com.huawei.sharedrive.uam.core.alarm;

import pw.cdmi.common.alarm.Alarm;

public class LdapFailedAlarm extends Alarm
{
    private static final long serialVersionUID = 571563324313439835L;
    
    private String server;
    
    public LdapFailedAlarm(String alarmID, int alarmType, int alarmLevel, String serviceName)
    {
        super(alarmID, alarmType, alarmLevel, serviceName);
    }
    
    public LdapFailedAlarm(LdapFailedAlarm alarm, String server)
    {
        this(alarm.getAlarmID(), alarm.getAlarmType(), alarm.getAlarmLevel(), alarm.getServiceName());
        this.server = server;
    }
    
    @Override
    public String getKey()
    {
        StringBuilder sb = new StringBuilder(String.valueOf(this.getAlarmID())).append(this.server);
        return sb.toString();
    }
    
    @Override
    public String getParameter()
    {
        return this.server;
    }
    
}
