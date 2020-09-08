package pw.cdmi.box.app.system.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.app.core.alarm.CSNodeAlarm;
import pw.cdmi.box.app.system.service.CSAlarmService;
import pw.cdmi.common.alarm.Alarm;
import pw.cdmi.common.alarm.AlarmHelper;
import pw.cdmi.common.cache.CacheClient;
@Component("CSAlarmService")
public class CSAlarmServiceImpl implements CSAlarmService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CSAlarmServiceImpl.class);
	@Autowired
    private AlarmHelper alarmHelper;
	
	public void sendUfm2CSAlarm(CSNodeAlarm csalarm, String node) {
		LOGGER.debug("enter method  sendUfm2CSAlarm , the convertservice alarm is  [ " + csalarm.toString() + " ]");
		Alarm alarm = null;
		alarm = new CSNodeAlarm(csalarm, node);
		CacheClient cacheClient = alarmHelper.getCacheClient();
		Alarm a = (Alarm)cacheClient.getCache(alarm.getHostName());
		//alarmType=0 故障告警，AlarmType=2 恢复告警
		if(csalarm.getAlarmType() == 0)
		{
			if(a == null)
			{
				//如果故障告警时，告警队列中没有告警，则发送告警，并将告警放到队列中，如果有告警，不再发送告警
				cacheClient.setCache(csalarm.getHostName(), alarm);
				alarmHelper.sendAlarm(alarm);
			}
		}
		else
		{
			if(a != null)
			{
				//如果恢复告警时，告警队列中有告警，则发送恢复告警，并将其从告警队列中删除，如果没有告警，不发送恢复告警
				LOGGER.debug("begin to recover alarm,the alarm is :" + a.toString());
				cacheClient.deleteCache(csalarm.getHostName());
				alarmHelper.sendRecoverAlarm(alarm);
			}
		}
		
		LOGGER.debug("exit method  sendUfm2CSAlarm ");
	}

}
