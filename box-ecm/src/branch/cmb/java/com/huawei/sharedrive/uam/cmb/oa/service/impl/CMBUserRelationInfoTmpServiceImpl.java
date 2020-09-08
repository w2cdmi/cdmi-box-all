package com.huawei.sharedrive.uam.cmb.oa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.cmb.oa.dao.CMBUserRelationInfoTmpDao;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBUserRelationInfo;
import com.huawei.sharedrive.uam.cmb.oa.service.CMBUserRelationInfoTmpService;

@Service
public class CMBUserRelationInfoTmpServiceImpl implements CMBUserRelationInfoTmpService
{
    @Autowired
    private CMBUserRelationInfoTmpDao cMBUserRelationInfoTmpDao;
    
    @Override
    public void insert(CMBUserRelationInfo cmbUserRelationInfo)
    {
        cMBUserRelationInfoTmpDao.insert(cmbUserRelationInfo);
    }
    
    @Override
    public List<CMBUserRelationInfo> getAll()
    {
        List<CMBUserRelationInfo> list = cMBUserRelationInfoTmpDao.getAll();
        return list;
    }
    
    @Override
    public void deleteAll()
    {
        cMBUserRelationInfoTmpDao.deleteAll();
    }
}
