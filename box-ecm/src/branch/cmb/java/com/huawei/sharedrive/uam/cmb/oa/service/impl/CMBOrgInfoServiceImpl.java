package com.huawei.sharedrive.uam.cmb.oa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.cmb.oa.dao.CMBOrgInfoDao;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBOrgInfo;
import com.huawei.sharedrive.uam.cmb.oa.service.CMBOrgInfoService;
import com.huawei.sharedrive.uam.core.domain.Limit;

@Service
public class CMBOrgInfoServiceImpl implements CMBOrgInfoService
{
    @Autowired
    private CMBOrgInfoDao cMBOrgInfoDao;
    
    @Override
    public void insert(CMBOrgInfo cmbOrgInfo)
    {
        cMBOrgInfoDao.insert(cmbOrgInfo);
    }
    
    @Override
    public void updateById(CMBOrgInfo cmbOrgInfo)
    {
        cMBOrgInfoDao.updateById(cmbOrgInfo);
    }
    
    @Override
    public CMBOrgInfo getById(Integer orgId)
    {
        return (CMBOrgInfo) cMBOrgInfoDao.getById(orgId);
    }
    
    @Override
    public List<CMBOrgInfo> getByFatherGroupId(String fatherGroupId, Limit limit)
    {
        return cMBOrgInfoDao.getByFatherGroupId(fatherGroupId, limit);
    }
    
    @Override
    public int getByFatherGroupIdCount(String fatherGroupId)
    {
        return cMBOrgInfoDao.getByFatherGroupIdCount(fatherGroupId);
    }
    
    @Override
    public List<String> getByExistsFatherGroupId(String fatherGroupId)
    {
        return cMBOrgInfoDao.getByExistsFatherGroupId(fatherGroupId);
    }
}
