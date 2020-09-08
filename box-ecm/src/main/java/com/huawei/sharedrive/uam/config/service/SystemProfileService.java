package com.huawei.sharedrive.uam.config.service;

import com.huawei.sharedrive.uam.config.domain.EnterpriseAccountProfile;

public interface SystemProfileService {
    //生成系统级企业账号限额配置，如果没有生成默认配置
    EnterpriseAccountProfile buildEnterpriseAccountProfile(String appId);
}
