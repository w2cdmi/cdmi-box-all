package com.huawei.sharedrive.uam.cmb.oa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.cmb.oa.dao.CMBSapUserTmpDao;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;
import com.huawei.sharedrive.uam.cmb.oa.service.CMBSapUserTmpService;

@Service
public class CMBSapUserTmpServiceImpl implements CMBSapUserTmpService
{
    @Autowired
    private CMBSapUserTmpDao cMBSapUserTmpDao;
    
    @Override
    public void insert(CMBSapUser cmbSapUserTmp)
    {
        cMBSapUserTmpDao.insert(cmbSapUserTmp);
    }
    
    @Override
    public List<CMBSapUser> getAll()
    {
        List<CMBSapUser> list = cMBSapUserTmpDao.getAll();
        return list;
    }
    
    @Override
    public void deleteAll()
    {
        cMBSapUserTmpDao.deleteAll();
    }
}
