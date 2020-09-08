/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.core.alarm;

import pw.cdmi.common.alarm.Alarm;

public class LackOfSpaceAlarm extends Alarm
{
    private static final long serialVersionUID = 3067568453556327928L;
    private String maxUtilization;
    private String spaceTag;
    
    public LackOfSpaceAlarm(String alarmID, int alarmType, int alarmLevel, String serviceName)
    {
        super(alarmID, alarmType, alarmLevel, serviceName);
        
    }
    
    public LackOfSpaceAlarm(LackOfSpaceAlarm alarm, String spaceTag, String maxUtilization)
    {
        this(alarm.getAlarmID(), alarm.getAlarmType(), alarm.getAlarmLevel(), alarm.getServiceName());
        this.setSpaceTag(spaceTag);
        this.setMaxUtilization(maxUtilization);
    }

    @Override
    public String getKey()
    {
        StringBuilder sb = new StringBuilder(String.valueOf(this.getAlarmID()))
        .append(spaceTag);
        return sb.toString();
    }

    public String getMaxUtilization()
    {
        return maxUtilization;
    }

    @Override
    public String getParameter()
    {
        StringBuilder sb = new StringBuilder(spaceTag)
        .append(PARAMETERS_SPLIT)
        .append(maxUtilization);
        return sb.toString();
    }

    public String getSpaceTag()
    {
        return spaceTag;
    }

    public void setMaxUtilization(String maxUtilization)
    {
        this.maxUtilization = maxUtilization;
    }

    public void setSpaceTag(String spaceTag)
    {
        this.spaceTag = spaceTag;
    }
}
