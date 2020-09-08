
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.config.service.impl;

import com.huawei.sharedrive.uam.config.domain.EnterpriseAccountProfile;
import com.huawei.sharedrive.uam.config.service.AccountProfileService;
import com.huawei.sharedrive.uam.config.service.SystemProfileService;
import com.huawei.sharedrive.uam.enterprise.dao.AccountConfigDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.common.domain.AccountConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/************************************************************
 * @Description:
 * <pre>账号级配置实现类</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2018/2/10
 ************************************************************/
@Service
public class AccountProfileServiceImpl implements AccountProfileService {
    Logger logger = LoggerFactory.getLogger(AccountProfileServiceImpl.class);

    @Autowired
    private AccountConfigDao accountConfigDao;

    @Autowired
    private SystemProfileService systemProfileService;

    @Override
    public EnterpriseAccountProfile buildEnterpriseAccountProfile(String appId, long accountId) {
        EnterpriseAccountProfile profile = new EnterpriseAccountProfile();

        //查询系统级配置
        EnterpriseAccountProfile systemProfile = systemProfileService.buildEnterpriseAccountProfile(appId);

        //查询当前账号配置
        List<AccountConfig> configList = accountConfigDao.listWithPrefix(accountId, "customer.enterprise");
        Map<String, String> map = convert(configList);

        //企业成员数
        String key = "customer.enterprise.maxUserAmount";
        String value = map.get(key);
        if(StringUtils.isNotBlank(value)) {
            try {
                profile.setMaxUserAmount(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                logger.error("Invalid parameter: name={}, value={}.", key, value);
                profile.setMaxUserAmount(systemProfile.getMaxUserAmount());
            }
        } else {
            profile.setMaxUserAmount(systemProfile.getMaxUserAmount());
        }

        //团队空间数量
        key = "customer.enterprise.maxTeamspaceAmount";
        value = map.get(key);
        if(StringUtils.isNotBlank(value)) {
            try {
                profile.setMaxTeamspaceAmount(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                logger.error("Invalid parameter: name={}, value={}.", key, value);
                profile.setMaxTeamspaceAmount(systemProfile.getMaxTeamspaceAmount());
            }
        } else {
            profile.setMaxTeamspaceAmount(systemProfile.getMaxTeamspaceAmount());
        }

        //总空间容量
        key = "customer.enterprise.maxTeamspaceQuota";
        value = map.get(key);
        if(StringUtils.isNotBlank(value)) {
            try {
                profile.setMaxTeamspaceQuota(Long.parseLong(value));
            } catch (NumberFormatException e) {
                logger.error("Invalid parameter: name={}, value={}.", key, value);
                profile.setMaxTeamspaceQuota(systemProfile.getMaxTeamspaceQuota());
            }
        } else {
            profile.setMaxTeamspaceQuota(systemProfile.getMaxTeamspaceQuota());
        }

        //用户空间容量
        key = "customer.enterprise.maxUserQuota";
        value = map.get(key);
        if(StringUtils.isNotBlank(value)) {
            try {
                profile.setMaxUserQuota(Long.parseLong(value));
            } catch (NumberFormatException e) {
                logger.error("Invalid parameter: name={}, value={}.", key, value);
                profile.setMaxUserQuota(systemProfile.getMaxUserQuota());
            }
        } else{
            profile.setMaxUserQuota(systemProfile.getMaxUserQuota());
        }

        //共享空间容量
        key = "customer.enterprise.maxShareQuota";
        value = map.get(key);
        if(StringUtils.isNotBlank(value)) {
            try {
                profile.setMaxShareQuota(Long.parseLong(value));
            } catch (NumberFormatException e) {
                logger.error("Invalid parameter: name={}, value={}.", key, value);
                profile.setMaxShareQuota(systemProfile.getMaxShareQuota());
            }
        } else {
            profile.setMaxShareQuota(systemProfile.getMaxShareQuota());
        }

        return profile;
    }

    protected Map<String, String> convert(List<AccountConfig> configList) {
        Map<String, String> map = new HashMap<>();
        for (AccountConfig config : configList) {
            map.put(config.getName(), config.getValue());
        }

        return map;
    }
}
