package com.huawei.sharedrive.app.core.alarm;

import pw.cdmi.common.alarm.Alarm;

public class DSSNodeOfflineAlarm extends Alarm
{
    private static final long serialVersionUID = 571563324313439835L;
    private String node;
    
    public DSSNodeOfflineAlarm(String alarmID, int alarmType, int alarmLevel, String serviceName)
    {
        super(alarmID, alarmType, alarmLevel, serviceName);
    }
    
    public DSSNodeOfflineAlarm(DSSNodeOfflineAlarm alarm, String node)
    {
        this(alarm.getAlarmID(), alarm.getAlarmType(), alarm.getAlarmLevel(), alarm.getServiceName());
        this.node = node;
    }

    @Override
    public String getKey()
    {
        StringBuilder sb = new StringBuilder(String.valueOf(this.getAlarmID()))
        .append(this.node);
        return sb.toString();
    }

    @Override
    public String getParameter()
    {
        return this.node;
    }
    
    
}
