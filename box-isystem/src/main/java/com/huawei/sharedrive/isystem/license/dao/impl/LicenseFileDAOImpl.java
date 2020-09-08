package com.huawei.sharedrive.isystem.license.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.common.license.LicenseFile;
import com.huawei.sharedrive.isystem.license.dao.LicenseFileDAO;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("licenseFileDAO")
@SuppressWarnings("deprecation")
public class LicenseFileDAOImpl extends AbstractDAOImpl implements LicenseFileDAO
{
    @Override
    public LicenseFile get(String id)
    {
        return (LicenseFile)sqlMapClientTemplate.queryForObject("LicenseFile.get", id);
    }

    @Override
    public void save(LicenseFile licenseFile)
    {
        sqlMapClientTemplate.insert("LicenseFile.save", licenseFile);
    }
    

    @Override
    public void updateStatusWithId(String id, byte status)
    {
        LicenseFile licenseFile = new LicenseFile();
        licenseFile.setId(id);
        licenseFile.setStatus(status);
        sqlMapClientTemplate.update("LicenseFile.updateStatusWithId", licenseFile);
    }

    @Override
    public void updateStatusWithoutId(String id, byte status)
    {
        LicenseFile licenseFile = new LicenseFile();
        licenseFile.setId(id);
        licenseFile.setStatus(status);
        sqlMapClientTemplate.update("LicenseFile.updateStatusWithoutId", licenseFile);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LicenseFile> getLicenseFileListByStatus(byte status)
    {
        LicenseFile licenseFile = new LicenseFile();
        licenseFile.setStatus(status);
        return (List<LicenseFile>)sqlMapClientTemplate.queryForList("LicenseFile.getLicenseFileListByStatus", licenseFile);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LicenseFile> getAllExceptCurrentFile(byte status)
    {
        LicenseFile licenseFile = new LicenseFile();
        licenseFile.setStatus(status);
        return (List<LicenseFile>)sqlMapClientTemplate.queryForList("LicenseFile.getAllExceptCurrentFile", licenseFile);
    }

    @Override
    public void deleteById(String id)
    {
        LicenseFile licenseFile = new LicenseFile();
        licenseFile.setId(id);
        sqlMapClientTemplate.delete("LicenseFile.deleteById", licenseFile);
    }
    
}
