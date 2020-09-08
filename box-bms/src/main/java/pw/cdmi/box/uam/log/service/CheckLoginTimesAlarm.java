package pw.cdmi.box.uam.log.service;

public interface CheckLoginTimesAlarm
{
    
    void sendSystemLoginTimes(long times,String checkType);
    
    void sendUserLoginTimes(long times,String checkType);
}
