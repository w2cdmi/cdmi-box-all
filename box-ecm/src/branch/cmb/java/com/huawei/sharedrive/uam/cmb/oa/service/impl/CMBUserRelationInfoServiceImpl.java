package com.huawei.sharedrive.uam.cmb.oa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.cmb.oa.dao.CMBUserRelationInfoDao;
import com.huawei.sharedrive.uam.cmb.oa.dao.CMBUserRelationInfoTmpDao;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBUserRelationInfo;
import com.huawei.sharedrive.uam.cmb.oa.service.CMBUserRelationInfoService;

@Service
public class CMBUserRelationInfoServiceImpl implements CMBUserRelationInfoService
{
    @Autowired
    private CMBUserRelationInfoDao cMBUserRelationInfoDao;
    
    @Autowired
    private CMBUserRelationInfoTmpDao cMBUserRelationInfoTmpDao;
    
    @Override
    public void insert(CMBUserRelationInfo cmbUserRelationInfo)
    {
        cMBUserRelationInfoDao.insert(cmbUserRelationInfo);
    }
    
    @Override
    public void updateById(CMBUserRelationInfo cmbUserRelationInfo)
    {
        cMBUserRelationInfoDao.updateById(cmbUserRelationInfo);
    }
    
    @Override
    public CMBUserRelationInfo getById(Integer orgId, String userId)
    {
        return (CMBUserRelationInfo) cMBUserRelationInfoDao.getById(orgId, userId);
    }
    
    @Override
    public List<CMBUserRelationInfo> getByUserId(String userId)
    {
        return cMBUserRelationInfoDao.getByUserId(userId);
    }
    
    @Override
    public List<CMBUserRelationInfo> getByOrgId(int orgId)
    {
        return cMBUserRelationInfoDao.getByOrgId(orgId);
    }
    
    @Override
    public void updateNormolStatus()
    {
        cMBUserRelationInfoDao.updateNormolStatus();
    }
    
    @Override
    public void updateUnNormolStatus()
    {
        List<CMBUserRelationInfo> list = cMBUserRelationInfoTmpDao.getAll();
        if (null != list && list.size() > 0)
        {
            cMBUserRelationInfoDao.updateUnNormolStatus();
        }
    }
    
    @Override
    public List<CMBSapUser> listCMBSapUserByGroupIds(List<String> list)
    {
        return cMBUserRelationInfoDao.listCMBSapUserByGroupIds(list);
    }
}
