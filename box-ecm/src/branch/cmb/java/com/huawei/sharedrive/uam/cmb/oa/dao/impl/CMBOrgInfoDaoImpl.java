package com.huawei.sharedrive.uam.cmb.oa.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.cmb.oa.dao.CMBOrgInfoDao;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBOrgInfo;
import com.huawei.sharedrive.uam.core.dao.impl.CacheableSqlMapClientDAO;
import com.huawei.sharedrive.uam.core.domain.Limit;

@Service
@SuppressWarnings({"deprecation", "unchecked"})
public class CMBOrgInfoDaoImpl extends CacheableSqlMapClientDAO implements CMBOrgInfoDao
{
    @Override
    public void insert(CMBOrgInfo cmbOrgInfo)
    {
        sqlMapClientTemplate.insert("CMBOrgInfo.insert", cmbOrgInfo);
    }
    
    @Override
    public void updateById(CMBOrgInfo cmbOrgInfo)
    {
        sqlMapClientTemplate.update("CMBOrgInfo.updateById", cmbOrgInfo);
    }
    
    @Override
    public CMBOrgInfo getById(Integer orgId)
    {
        if (null == orgId)
        {
            return null;
        }
        return (CMBOrgInfo) sqlMapClientTemplate.queryForObject("CMBOrgInfo.getById", orgId);
    }
    
    @Override
    public int getByFatherGroupIdCount(String fatherGroupId)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("fatherGroupId", fatherGroupId);
        return (int) sqlMapClientTemplate.queryForObject("CMBOrgInfo.getByFatherGroupIdCount", map);
    }
    
    @Override
    public List<CMBOrgInfo> getByFatherGroupId(String fatherGroupId, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("fatherGroupId", fatherGroupId);
        map.put("limit", limit);
        List<CMBOrgInfo> list = sqlMapClientTemplate.queryForList("CMBOrgInfo.getByFatherGroupId", map);
        return list;
    }
    
    @Override
    public List<String> getByExistsFatherGroupId(String fatherGroupId)
    {
        List<String> list = sqlMapClientTemplate.queryForList("CMBOrgInfo.getByExistsFatherGroupId",
            fatherGroupId);
        return list;
    }
}
