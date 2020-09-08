/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.core.alarm;

import pw.cdmi.common.alarm.Alarm;

public class CommunicateFailedAlarm extends Alarm
{
    private static final long serialVersionUID = 571563324313439835L;
    private String toServiceName;
    
    public CommunicateFailedAlarm(String alarmID, int alarmType, int alarmLevel, String serviceName, String toServiceName)
    {
        super(alarmID, alarmType, alarmLevel, serviceName);
        this.toServiceName = toServiceName;
    }
    
    public CommunicateFailedAlarm(CommunicateFailedAlarm alarm)
    {
        this(alarm.getAlarmID(), alarm.getAlarmType(), alarm.getAlarmLevel(), alarm.getServiceName(), alarm.getToServiceName());
    }

    @Override
    public String getKey()
    {
        StringBuilder sb = new StringBuilder(String.valueOf(this.getAlarmID()))
        .append(this.getHostName());
        return sb.toString();
    }

    public String getToServiceName()
    {
        return toServiceName;
    }

    public void setToServiceName(String toServiceName)
    {
        this.toServiceName = toServiceName;
    }

    @Override
    public String getParameter()
    {
        StringBuilder sb = new StringBuilder(this.getHostName())
        .append(PARAMETERS_SPLIT)
        .append(this.getServiceName())
        .append(PARAMETERS_SPLIT)
        .append(this.toServiceName);
        return sb.toString();
    }
    
    
}
