package com.huawei.sharedrive.uam.enterpriseadminlog.manager.impl;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.EnterpriseAdminLog;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.QueryCondition;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.service.AdminLogService;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;

@Service
public class AdminLogManagerImpl implements AdminLogManager
{
    @Autowired
    private AdminLogService adminLogService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminLogManagerImpl.class);
    
    @Override
    public void saveAdminLog(LogOwner owner, AdminLogType logType, String[] paramsDesc)
    {
        EnterpriseAdminLog adminLog = adminLogService.getAdminLog(owner);
        if (null == adminLog && AdminLogType.KEY_ADMIN_LOGIN_ERROR.equals(logType))
        {
            LOGGER.error("Anonymous user:" + owner.getLoginName() + " fail to login at " + owner.getIp());
        }
        adminLogService.saveAdminLog(adminLog, logType, paramsDesc);
    }
    
    @Override
    public Page<EnterpriseAdminLog> getSyncLog(QueryCondition qc, Locale locale)
    {
        List<EnterpriseAdminLog> list = adminLogService.queryList(qc, qc.getPageRequest().getLimit());
        int totalCount = adminLogService.getCount(qc);
        Page<EnterpriseAdminLog> pagelist = new PageImpl<EnterpriseAdminLog>(list, qc.getPageRequest(),
            totalCount);
        for (EnterpriseAdminLog iter : list)
        {
            iter.setOperatDesc(HtmlUtils.htmlEscape(iter.getOperatDesc()));
            iter.setAppId(null);
            iter.setAuthServerId(null);
            iter.setEnterpriseId(null);
            iter.setIp(null);
            iter.setOperatDescKey(null);
            iter.setLevel(iter.getLevel());
            iter.setLoginName(null);
        }
        return pagelist;
    }
    
}
