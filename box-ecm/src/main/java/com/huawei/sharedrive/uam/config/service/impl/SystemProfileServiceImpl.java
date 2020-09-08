
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.config.service.impl;

import com.huawei.sharedrive.uam.config.domain.EnterpriseAccountProfile;
import com.huawei.sharedrive.uam.config.service.SystemProfileService;
import com.huawei.sharedrive.uam.system.dao.SystemConfigDAO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.common.domain.SystemConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/************************************************************
 * @Description:
 * <pre>系统配置信息实现类</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2018/2/10
 ************************************************************/
@Service
public class SystemProfileServiceImpl implements SystemProfileService {
    Logger logger = LoggerFactory.getLogger(SystemProfileServiceImpl.class);

    @Autowired
    private SystemConfigDAO systemConfigDAO;

    @Override
    public EnterpriseAccountProfile buildEnterpriseAccountProfile(String appId) {
        EnterpriseAccountProfile profile = new EnterpriseAccountProfile();

        List<SystemConfig> configList = systemConfigDAO.getByPrefix(appId, null, "basicconfig.enterprise");
        Map<String, String> map = convert(configList);

        //企业成员数
        String key = "basicconfig.enterprise.maxUserAmount";
        String value = map.get(key);
        if(StringUtils.isNotBlank(value)) {
            try {
                profile.setMaxUserAmount(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                logger.error("Invalid parameter: name={}, value={}.", key, value);
            }
        }

        //团队空间数量
        key = "basicconfig.enterprise.maxTeamspaceAmount";
        value = map.get(key);
        if(StringUtils.isNotBlank(value)) {
            try {
                profile.setMaxTeamspaceAmount(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                logger.error("Invalid parameter: name={}, value={}.", key, value);
            }
        }

        //总空间容量
        key = "basicconfig.enterprise.maxTeamspaceQuota";
        value = map.get(key);
        if(StringUtils.isNotBlank(value)) {
            try {
                profile.setMaxTeamspaceQuota(Long.parseLong(value));
            } catch (NumberFormatException e) {
                logger.error("Invalid parameter: name={}, value={}.", key, value);
            }
        }

        //用户空间容量
        key = "basicconfig.enterprise.maxUserQuota";
        value = map.get(key);
        if(StringUtils.isNotBlank(value)) {
            try {
                profile.setMaxUserQuota(Long.parseLong(value));
            } catch (NumberFormatException e) {
                logger.error("Invalid parameter: name={}, value={}.", key, value);
            }
        }

        //共享空间容量
        key = "basicconfig.enterprise.maxShareQuota";
        value = map.get(key);
        if(StringUtils.isNotBlank(value)) {
            try {
                profile.setMaxShareQuota(Long.parseLong(value));
            } catch (NumberFormatException e) {
                logger.error("Invalid parameter: name={}, value={}.", key, value);
            }
        }

        return profile;
    }

    protected Map<String, String> convert(List<SystemConfig> configList) {
        Map<String, String> map = new HashMap<>();
        for (SystemConfig config : configList) {
            map.put(config.getId(), config.getValue());
        }

        return map;
    }
}
