package com.huawei.sharedrive.isystem.license.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.common.license.LicenseNode;
import com.huawei.sharedrive.isystem.license.dao.LicenseNodeDAO;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("licenseNodeDAO")
@SuppressWarnings("deprecation")
public class LicenseNodeDAOImpl extends AbstractDAOImpl implements LicenseNodeDAO
{
    
    @SuppressWarnings("unchecked")
    @Override
    public List<LicenseNode> listAll()
    {
        return (List<LicenseNode>) sqlMapClientTemplate.queryForList("LicenseNode.getAll");
    }
    
}
