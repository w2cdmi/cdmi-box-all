package com.huawei.sharedrive.app.system.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.system.dao.LicenseDAO;
import com.huawei.sharedrive.common.license.LicenseFile;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("licenseDAO")
@SuppressWarnings("deprecation")
public class LicenseDAOImpl extends AbstractDAOImpl implements LicenseDAO
{
    @Override
    public LicenseFile get(String id)
    {
        return (LicenseFile) sqlMapClientTemplate.queryForObject("LicenseFileMapper.get", id);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<LicenseFile> getLicenseFileListByStatus(byte status)
    {
        LicenseFile licenseFile = new LicenseFile();
        licenseFile.setStatus(status);
        return (List<LicenseFile>)sqlMapClientTemplate.queryForList("LicenseFileMapper.getLicenseFileListByStatus", licenseFile);
    }

    @Override
    public LicenseFile getLastest()
    {
        LicenseFile licenseFile = new LicenseFile();
        licenseFile.setStatus((byte)2);
        return (LicenseFile) sqlMapClientTemplate.queryForObject("LicenseFileMapper.getLastest", licenseFile);
    }
    
}
