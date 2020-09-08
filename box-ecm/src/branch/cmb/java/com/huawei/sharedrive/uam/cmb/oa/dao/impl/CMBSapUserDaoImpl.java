package com.huawei.sharedrive.uam.cmb.oa.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.cmb.oa.dao.CMBSapUserDao;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;
import com.huawei.sharedrive.uam.core.dao.impl.CacheableSqlMapClientDAO;

@Service
@SuppressWarnings({"deprecation", "unchecked"})
public class CMBSapUserDaoImpl extends CacheableSqlMapClientDAO implements CMBSapUserDao
{
    @Override
    public void insert(CMBSapUser cmbSapUser)
    {
        sqlMapClientTemplate.insert("CMBSapUser.insert", cmbSapUser);
    }
    
    @Override
    public void updateById(CMBSapUser cmbSapUser)
    {
        sqlMapClientTemplate.update("CMBSapUser.updateById", cmbSapUser);
    }
    
    @Override
    public CMBSapUser getById(String sapId)
    {
        if (StringUtils.isBlank(sapId))
        {
            return null;
        }
        return (CMBSapUser) sqlMapClientTemplate.queryForObject("CMBSapUser.getById", sapId);
    }
    
    @Override
    public List<CMBSapUser> getAll()
    {
        return sqlMapClientTemplate.queryForList("CMBSapUser.getAll");
    }
}
