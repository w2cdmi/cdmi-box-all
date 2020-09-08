package pw.cdmi.box.uam.log.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.log.service.CheckLoginTimesAlarm;
import pw.cdmi.common.alarm.Alarm;
import pw.cdmi.common.alarm.AlarmHelper;
import pw.cdmi.core.alarm.LoginCheckAlarm;

@Component
public class CheckLoginTimesAlarmImpl implements CheckLoginTimesAlarm
{
    
    public static final Logger LOGGER = LoggerFactory.getLogger(CheckLoginTimesAlarmImpl.class);
    
    @Autowired
    private AlarmHelper alarmHelper;
    
    @Autowired
    private LoginCheckAlarm loginCheckAlarm;
    
    @Override
    public void sendSystemLoginTimes(long times, String checkType)
    {
        LoginCheckAlarm alarm = new LoginCheckAlarm(loginCheckAlarm, times);
        alarm.setAlarmID(LoginCheckAlarm.SYSTEM_LOGING_ID);
        sendAlarm(alarm);
    }
    
    @Override
    public void sendUserLoginTimes(long times, String checkType)
    {
        LoginCheckAlarm alarm = new LoginCheckAlarm(loginCheckAlarm, times);
        alarm.setAlarmID(LoginCheckAlarm.USER_LOGING_ID);
        sendAlarm(alarm);
        
    }
    
    private void sendAlarm(Alarm alarm)
    {
        LOGGER.info("send Alarm CheckLoginTmes, id "+alarm.getAlarmID()+"," + alarm.getParameter());
        alarmHelper.sendAlarm(alarm);
    }
}
