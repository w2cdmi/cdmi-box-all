package com.huawei.sharedrive.uam.enterpriseadminlog.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.core.dao.util.HashTool;
import com.huawei.sharedrive.uam.enterpriseadminlog.dao.AdminLogDao;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.EnterpriseAdminLog;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.QueryCondition;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;

@SuppressWarnings({"deprecation", "unchecked"})
@Service
public class AdminLogDaoImpl extends AbstractDAOImpl implements AdminLogDao
{
    
    private static final int TABLE_COUNT = 100;
    
    @Value("${admin.import.user.log.count}")
    private int importUserLogCount;
    
    @Override
    public void createAdminLog(EnterpriseAdminLog adminLog)
    {
        adminLog.setTableSuffix(getTableSuffix(adminLog.getEnterpriseId()));
        if (null == adminLog.getId())
        {
            adminLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        }
        super.getUserLogTemplate().insert("EnterpriseAdminLog.insert", adminLog);
    }
    
    @Override
    public List<EnterpriseAdminLog> getEnterpriseAdminlog(QueryCondition qc, Limit limit)
    {
        Map<String, Object> params = new HashMap<String, Object>(10);
        params.put("tableSuffix", getTableSuffix(qc.getEnterpriseId()));
        params.put("filter", qc);
        params.put("limit", limit);
        List<EnterpriseAdminLog> list = getUserLogTemplate().queryForList("EnterpriseAdminLog.getEnterpriseAdminlog",
            params);
        return list;
    }
    
    @Override
    public int getCount(QueryCondition qc)
    {
        Map<String, Object> params = new HashMap<String, Object>(10);
        params.put("tableSuffix", getTableSuffix(qc.getEnterpriseId()));
        params.put("filter", qc);
        return (int) getUserLogTemplate().queryForObject("EnterpriseAdminLog.getCount", params);
    }
    
    public String getTableSuffix(long enterpriseId)
    {
        int table = (int) (HashTool.apply(String.valueOf(enterpriseId)) % TABLE_COUNT);
        return "_" + table;
    }
    
    @Override
    public int getFilterCount(long enterpriseId, String operateType)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("tableSuffix", getTableSuffix(enterpriseId));
        map.put("enterpriseId", enterpriseId);
        map.put("operateType", operateType);
        return (int) super.getUserLogTemplate().queryForObject("EnterpriseAdminLog.getFilterCount", map);
    }
    
    @Override
    public EnterpriseAdminLog getMinCreateTime(long enterpriseId, String operateType)
    {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("tableSuffix", getTableSuffix(enterpriseId));
        map.put("logCount", importUserLogCount);
        map.put("enterpriseId", enterpriseId);
        map.put("operateType", operateType);
        return (EnterpriseAdminLog) super.getUserLogTemplate()
            .queryForObject("EnterpriseAdminLog.getMinCreateTime", map);
    }
    
    @Override
    public void deleteImportUserRecordLog(EnterpriseAdminLog adminLog)
    {
        long enterpriseId = adminLog.getEnterpriseId();
        adminLog.setTableSuffix(getTableSuffix(enterpriseId));
        super.getUserLogTemplate().delete("EnterpriseAdminLog.deleteImportUserRecordLog", adminLog);
    }
    
}
