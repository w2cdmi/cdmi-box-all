/**
 * 
 */
package pw.cdmi.box.app.system.service;

import org.springframework.stereotype.Component;

import pw.cdmi.box.app.core.alarm.CSNodeAlarm;

/**
 * @author weikai
 * 
 */

public interface CSAlarmService
{
    
    /**
     *  UFM查询ConvertService的状态并发送告警
     * 
     * @return
     */
    void sendUfm2CSAlarm(CSNodeAlarm alarm ,String node);
}
