package com.huawei.sharedrive.app.dns.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.dns.dao.DssDomainDao;
import com.huawei.sharedrive.app.dns.domain.DssDomain;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("dssDomainDao")
public class DssDomainDaoImpl extends AbstractDAOImpl implements DssDomainDao
{
    
    @Override
    @SuppressWarnings({"deprecation", "unchecked"})
    public List<DssDomain> getAll()
    {
        return sqlMapClientTemplate.queryForList("DssDomain.getAll");
    }
    
    @Override
    @SuppressWarnings({"deprecation", "unchecked"})
    public List<DssDomain> getByDssId(int dssId)
    {
        return sqlMapClientTemplate.queryForList("DssDomain.getByDssId", dssId);
    }
}
