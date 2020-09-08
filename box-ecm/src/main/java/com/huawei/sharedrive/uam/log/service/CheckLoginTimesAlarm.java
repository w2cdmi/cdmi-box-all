package com.huawei.sharedrive.uam.log.service;

import org.springframework.stereotype.Component;

@Component
public interface CheckLoginTimesAlarm
{
    void sendSystemLoginTimes(long times,String checkType);
    
    void sendUserLoginTimes(long times,String checkType);
}
