package com.huawei.sharedrive.uam.log.service;

import com.huawei.sharedrive.uam.log.domain.OperateType;

import pw.cdmi.common.log.SystemLog;

public interface SystemLoginLogService
{
    void create(SystemLog systemLoginLog, OperateType operateType, String[] paramsType);
}
