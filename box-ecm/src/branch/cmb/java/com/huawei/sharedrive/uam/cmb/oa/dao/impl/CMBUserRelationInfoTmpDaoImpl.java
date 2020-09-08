package com.huawei.sharedrive.uam.cmb.oa.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.cmb.oa.dao.CMBUserRelationInfoTmpDao;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBUserRelationInfo;
import com.huawei.sharedrive.uam.core.dao.impl.CacheableSqlMapClientDAO;

@Service
@SuppressWarnings({"deprecation", "unchecked"})
public class CMBUserRelationInfoTmpDaoImpl extends CacheableSqlMapClientDAO implements
    CMBUserRelationInfoTmpDao
{
    @Override
    public void insert(CMBUserRelationInfo cmbUserRelationInfo)
    {
        sqlMapClientTemplate.insert("CMBUserRelationInfoTmp.insert", cmbUserRelationInfo);
    }
    
    @Override
    public List<CMBUserRelationInfo> getAll()
    {
        List<CMBUserRelationInfo> list = sqlMapClientTemplate.queryForList("CMBUserRelationInfoTmp.getAll");
        return list;
    }
    
    @Override
    public void deleteAll()
    {
        sqlMapClientTemplate.delete("CMBUserRelationInfoTmp.deleteAll");
    }
}
