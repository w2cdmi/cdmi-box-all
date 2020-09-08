package com.huawei.sharedrive.uam.enterpriseadminlog.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.dao.AdminLogDao;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.EnterpriseAdminLog;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.QueryCondition;
import com.huawei.sharedrive.uam.enterpriseadminlog.service.AdminLogService;
import com.huawei.sharedrive.uam.user.dao.AdminDAO;
import com.huawei.sharedrive.uam.user.domain.Admin;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.enterprise.Enterprise;

@Service
public class AdminLogServiceImpl implements AdminLogService
{
    @Autowired
    private EnterpriseManager enterpriseManager;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminLogServiceImpl.class);
    
    @Autowired
    private AdminLogDao adminLogDao;
    
    @Autowired
    private AdminDAO adminDAO;
    
    @Override
    public EnterpriseAdminLog getAdminLog(LogOwner owner)
    {
        Enterprise enterprise = null;
        EnterpriseAdminLog adminLog = new EnterpriseAdminLog();
        if (owner.getEnterpriseId() != null)
        {
            enterprise = enterpriseManager.getById(owner.getEnterpriseId());
        }
        else if (owner.getLoginName() != null)
        {
            Admin admin = adminDAO.getByLoginNameWithoutCache(owner.getLoginName());
            if (admin != null)
            {
                enterprise = enterpriseManager.getById(admin.getEnterpriseId());
            }
        }
        if (enterprise == null)
        {
            return null;
        }
        
        String loginName = StringUtils.isNotBlank(enterprise.getContactEmail())?enterprise.getContactEmail():enterprise.getContactPhone();
        adminLog.setEnterpriseId(enterprise.getId());
        adminLog.setIp(owner.getIp());
        adminLog.setLoginName(loginName);
        adminLog.setAppId(owner.getAppId());
        adminLog.setAuthServerId(owner.getAuthServerId());
        adminLog.setOperatType(owner.getOperatType());
        adminLog.setCreateTime(new Date());
        return adminLog;
    }
    
    @Override
    public void saveAdminLog(EnterpriseAdminLog adminLog, AdminLogType logType, String[] paramsDesc)
    {
        if (logType == null || adminLog == null)
        {
            return;
        }
        try
        {
            adminLog.setLevel(logType.getLevel());
            adminLog.setOperatDescKey(logType.getTypeCode());
            adminLog.setOperatDesc(logType.getDetails(paramsDesc));
            adminLog.setOperatLevel(logType.getOperatLevel());
            adminLogDao.createAdminLog(adminLog);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("Fail to save user log,log description length too long", e);
            String desc = logType.getDetails(paramsDesc);
            if (desc.length() > 1024)
            {
                desc = desc.substring(0, 1020) + "...";
            }
            adminLog.setOperatDesc(desc);
            adminLogDao.createAdminLog(adminLog);
        }
    }
    
    @Override
    public List<EnterpriseAdminLog> queryList(QueryCondition qc, Limit limit)
    {
        return adminLogDao.getEnterpriseAdminlog(qc, limit);
    }
    
    @Override
    public int getCount(QueryCondition qc)
    {
        return adminLogDao.getCount(qc);
    }
    
    @Override
    public int getFilterCount(long enterpriseId, String operateType)
    {
        return adminLogDao.getFilterCount(enterpriseId, operateType);
    }
    
    @Override
    public EnterpriseAdminLog getMinCreateTime(long enterpriseId, String operateType)
    {
        return adminLogDao.getMinCreateTime(enterpriseId, operateType);
    }
    
    @Override
    public void deleteImportUserRecordLog(EnterpriseAdminLog adminLog)
    {
        adminLogDao.deleteImportUserRecordLog(adminLog);
    }
}
