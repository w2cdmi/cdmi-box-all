package com.huawei.sharedrive.uam.config.service;

import com.huawei.sharedrive.uam.config.domain.EnterpriseAccountProfile;
import pw.cdmi.common.domain.AccountConfig;

import java.util.List;

public interface AccountProfileService {
    //生成企业账号限额配置
    EnterpriseAccountProfile buildEnterpriseAccountProfile(String appId, long accountId);
}
