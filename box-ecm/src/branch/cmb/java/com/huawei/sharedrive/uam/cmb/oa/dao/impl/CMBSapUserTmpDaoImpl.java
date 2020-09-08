package com.huawei.sharedrive.uam.cmb.oa.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.cmb.oa.dao.CMBSapUserTmpDao;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;
import com.huawei.sharedrive.uam.core.dao.impl.CacheableSqlMapClientDAO;

@Service
@SuppressWarnings({"deprecation", "unchecked"})
public class CMBSapUserTmpDaoImpl extends CacheableSqlMapClientDAO implements CMBSapUserTmpDao
{
    @Override
    public void insert(CMBSapUser cmbSapUserTmp)
    {
        sqlMapClientTemplate.insert("CMBSapUserTmp.insert", cmbSapUserTmp);
    }
    
    @Override
    public List<CMBSapUser> getAll()
    {
        List<CMBSapUser> list = sqlMapClientTemplate.queryForList("CMBSapUserTmp.getAll");
        return list;
    }
    
    @Override
    public void deleteAll()
    {
        sqlMapClientTemplate.delete("CMBSapUserTmp.deleteAll");
    }
}
