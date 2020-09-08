/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.core.alarm;

import pw.cdmi.common.alarm.Alarm;

public class StorageFailedAlarm extends Alarm
{
    private static final long serialVersionUID = 571563324313439835L;
    private String spaceTag;
    
    public StorageFailedAlarm(String alarmID, int alarmType, int alarmLevel, String serviceName)
    {
        super(alarmID, alarmType, alarmLevel, serviceName);
    }
    
    public StorageFailedAlarm(StorageFailedAlarm alarm, String spaceTag)
    {
        this(alarm.getAlarmID(), alarm.getAlarmType(), alarm.getAlarmLevel(), alarm.getServiceName());
        this.spaceTag = spaceTag;
    }

    @Override
    public String getKey()
    {
        StringBuilder sb = new StringBuilder(String.valueOf(this.getAlarmID()))
        .append(this.getHostName())
        .append(spaceTag);
        return sb.toString();
    }

    @Override
    public String getParameter()
    {
        StringBuilder sb = new StringBuilder(this.getHostName())
        .append(PARAMETERS_SPLIT)
        .append(spaceTag);
        return sb.toString();
    }

    public String getSpaceTag()
    {
        return spaceTag;
    }

    public void setSpaceTag(String spaceTag)
    {
        this.spaceTag = spaceTag;
    }
}
