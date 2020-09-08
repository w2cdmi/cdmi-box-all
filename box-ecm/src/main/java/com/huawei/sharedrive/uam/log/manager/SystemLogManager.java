package com.huawei.sharedrive.uam.log.manager;

import javax.servlet.http.HttpServletRequest;

import com.huawei.sharedrive.uam.log.domain.OperateDescription;
import com.huawei.sharedrive.uam.log.domain.OperateType;

import pw.cdmi.common.log.SystemLog;

public interface SystemLogManager
{
    
    String saveSystemLog(SystemLog systemLog, OperateType operateType, OperateDescription operateDescription,
        String[] paramsType, String[] paramsDescription);
    
    String saveSystemLog(SystemLog systemLog, OperateType operateType, OperateDescription operateDescription,
        String[] paramsDescription);
    
    String save(HttpServletRequest request, OperateType operateType, OperateDescription operateDescription,
        String[] paramsType, String[] paramsDescription);
    
    void updateSuccess(String id);
    
}
