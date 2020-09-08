package com.huawei.sharedrive.uam.cmb.oa.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.cmb.oa.dao.CMBUserRelationInfoDao;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBUserRelationInfo;
import com.huawei.sharedrive.uam.core.dao.impl.CacheableSqlMapClientDAO;

@Service
@SuppressWarnings({"deprecation", "unchecked"})
public class CMBUserRelationInfoDaoImpl extends CacheableSqlMapClientDAO implements CMBUserRelationInfoDao
{
    @Override
    public void insert(CMBUserRelationInfo cmbUserRelationInfo)
    {
        sqlMapClientTemplate.insert("CMBUserRelationInfo.insert", cmbUserRelationInfo);
    }
    
    @Override
    public void updateById(CMBUserRelationInfo cmbUserRelationInfo)
    {
        sqlMapClientTemplate.update("CMBUserRelationInfo.updateById", cmbUserRelationInfo);
    }
    
    @Override
    public CMBUserRelationInfo getById(Integer orgId, String userId)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("orgId", orgId);
        map.put("userId", userId);
        return (CMBUserRelationInfo) sqlMapClientTemplate.queryForObject("CMBUserRelationInfo.getById", map);
    }
    
    @Override
    public List<CMBUserRelationInfo> getByOrgId(int orgId)
    {
        List<CMBUserRelationInfo> list = sqlMapClientTemplate.queryForList("CMBUserRelationInfo.getByOrgId",
            orgId);
        return list;
    }
    
    @Override
    public List<CMBUserRelationInfo> getByUserId(String userId)
    {
        List<CMBUserRelationInfo> list = sqlMapClientTemplate.queryForList("CMBUserRelationInfo.getByUserId",
            userId);
        return list;
    }
    
    @Override
    public void updateNormolStatus()
    {
        sqlMapClientTemplate.update("CMBUserRelationInfo.updateNormolStatus");
    }
    
    @Override
    public void updateUnNormolStatus()
    {
        sqlMapClientTemplate.update("CMBUserRelationInfo.updateUnNormolStatus");
    }
    
    @Override
    public List<CMBSapUser> listCMBSapUserByGroupIds(List<String> list)
    {
        return sqlMapClientTemplate.queryForList("CMBUserRelationInfo.getByGroupIds", list);
    }
}
