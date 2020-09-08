package com.huawei.sharedrive.uam.cmb.oa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.cmb.oa.dao.CMBSapUserDao;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;
import com.huawei.sharedrive.uam.cmb.oa.service.CMBSapUserService;

@Service
public class CMBSapUserServiceImpl implements CMBSapUserService
{
    @Autowired
    private CMBSapUserDao cMBSapUserDao;
    
    @Override
    public void insert(CMBSapUser cmbSapUser)
    {
        cMBSapUserDao.insert(cmbSapUser);
    }
    
    @Override
    public void updateById(CMBSapUser cmbSapUser)
    {
        cMBSapUserDao.updateById(cmbSapUser);
    }
    
    @Override
    public CMBSapUser getById(String sapId)
    {
        return cMBSapUserDao.getById(sapId);
    }
    
    @Override
    public List<CMBSapUser> getAll()
    {
        return cMBSapUserDao.getAll();
    }
}
