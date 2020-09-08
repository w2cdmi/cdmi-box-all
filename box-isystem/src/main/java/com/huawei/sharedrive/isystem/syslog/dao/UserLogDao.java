package com.huawei.sharedrive.isystem.syslog.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.adminlog.domain.QueryCondition;

import pw.cdmi.common.log.UserLog;

public interface UserLogDao
{

    String insert(UserLog userLog);


    void updateUserLog(UserLog userLog);


    List<UserLog> getListUserLog(QueryCondition filter);


    int getTotals(QueryCondition filter);
    
}
