package com.huawei.sharedrive.uam.adminlog.dao;

import pw.cdmi.common.domain.SystemConfig;

public interface UserLogConfigDao
{
    SystemConfig getByPriKey(String appId, String id);
}
