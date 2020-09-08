package com.huawei.sharedrive.app.common.systemtask.dao;
import java.util.List;

import com.huawei.sharedrive.app.common.systemtask.domain.UserDBInfo;

public interface UserDBInfoDAO
{
    List<UserDBInfo> listAllUserdbInfo();
}
