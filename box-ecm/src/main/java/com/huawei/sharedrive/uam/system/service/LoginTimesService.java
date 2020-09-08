package com.huawei.sharedrive.uam.system.service;

import pw.cdmi.common.domain.SystemLoginTimesConfig;
import pw.cdmi.common.domain.UserLoginTimesConfig;

public interface LoginTimesService
{
    SystemLoginTimesConfig getSystemLoginTimesConfig();
    
    UserLoginTimesConfig getUserLoginTimesConfig();
    
    void saveSystemLoginTimesConfig(SystemLoginTimesConfig systemLoginTimesConfig);
    
    void saveUserLoginTimesConfig(UserLoginTimesConfig userLoginTimesConfig);
}
