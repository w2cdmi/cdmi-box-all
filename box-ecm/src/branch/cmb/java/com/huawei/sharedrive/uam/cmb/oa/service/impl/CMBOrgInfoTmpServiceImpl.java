package com.huawei.sharedrive.uam.cmb.oa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.cmb.oa.dao.CMBOrgInfoTmpDao;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBOrgInfo;
import com.huawei.sharedrive.uam.cmb.oa.service.CMBOrgInfoTmpService;

@Service
public class CMBOrgInfoTmpServiceImpl implements CMBOrgInfoTmpService
{
    @Autowired
    private CMBOrgInfoTmpDao cMBOrgInfoTmpDap;
    
    @Override
    public void insert(CMBOrgInfo cmbOrgInfo)
    {
        cMBOrgInfoTmpDap.insert(cmbOrgInfo);
    }
    
    @Override
    public List<CMBOrgInfo> getAll()
    {
        List<CMBOrgInfo> list = cMBOrgInfoTmpDap.getAll();
        return list;
    }
    
    @Override
    public void deleteAll()
    {
        cMBOrgInfoTmpDap.deleteAll();
    }
}
