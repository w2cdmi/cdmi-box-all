package com.huawei.sharedrive.uam.cmb.oa.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.cmb.oa.dao.CMBOrgInfoTmpDao;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBOrgInfo;
import com.huawei.sharedrive.uam.core.dao.impl.CacheableSqlMapClientDAO;

@Service
@SuppressWarnings({"deprecation", "unchecked"})
public class CMBOrgInfoTmpDaoImpl extends CacheableSqlMapClientDAO implements CMBOrgInfoTmpDao
{
    @Override
    public void insert(CMBOrgInfo cmbOrgInfo)
    {
        sqlMapClientTemplate.insert("CMBOrgInfoTmp.insert", cmbOrgInfo);
    }
    
    @Override
    public List<CMBOrgInfo> getAll()
    {
        List<CMBOrgInfo> list = sqlMapClientTemplate.queryForList("CMBOrgInfoTmp.getAll");
        return list;
    }
    
    @Override
    public void deleteAll()
    {
        sqlMapClientTemplate.delete("CMBOrgInfoTmp.deleteAll");
    }
}
