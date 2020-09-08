package com.huawei.sharedrive.app.system.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.system.dao.LicenseNodeDAO;
import com.huawei.sharedrive.common.license.LicenseNode;
import com.huawei.sharedrive.common.license.LicenseStatus;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("licenseNodeDAO")
@SuppressWarnings({"unchecked", "deprecation"})
public class LicenseNodeDAOImpl extends AbstractDAOImpl implements LicenseNodeDAO
{
    
    @Override
    public LicenseNode getCurrentLicenseNode(String esn, String licenseId)
    {
        LicenseNode licenseNode = new LicenseNode();
        licenseNode.setEsn(esn);
        licenseNode.setLicenseId(licenseId);
        licenseNode.setServerType(LicenseNode.NODE_AC);
        return (LicenseNode)sqlMapClientTemplate.queryForObject("LicenseNodeMapper.getByEsnAndLicenseId", licenseNode);
    }
    
    @Override
    public LicenseNode getLicenseNode(String esn, byte nodeType)
    {
        Map<String, Object> filter = new HashMap<String, Object>(2);
        filter.put("esn", esn);
        filter.put("serverType", nodeType);
        LicenseNode licenseNode = (LicenseNode)sqlMapClientTemplate.queryForObject("LicenseNodeMapper.get", filter);
        return licenseNode;
    }
    
    @Override
    public List<LicenseNode> getLicenseNodeList()
    {
        List<LicenseNode> list = sqlMapClientTemplate.queryForList("LicenseNodeMapper.getAll");
        return list;
    }
    
    @Override
    public int getNormalCountWithoutMe(byte nodeType, String licenseId, String esn)
    {
        LicenseNode query = new LicenseNode();
        query.setEsn(esn);
        query.setStatus(LicenseStatus.NORMAL);
        query.setServerType(nodeType);
        query.setLicenseId(licenseId);
        return (int) sqlMapClientTemplate.queryForObject("LicenseNodeMapper.getNormalCountWithoutMeLid", query);
    }

    @Override
    public void save(LicenseNode licenseNode)
    {
        sqlMapClientTemplate.insert("LicenseNodeMapper.save", licenseNode);
    }

    @Override
    public int update(LicenseNode licenseNode)
    {
        return sqlMapClientTemplate.update("LicenseNodeMapper.update", licenseNode);
    }
    
}
