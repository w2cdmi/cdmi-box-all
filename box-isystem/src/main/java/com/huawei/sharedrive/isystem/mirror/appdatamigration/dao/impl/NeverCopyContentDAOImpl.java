package com.huawei.sharedrive.isystem.mirror.appdatamigration.dao.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.isystem.mirror.appdatamigration.dao.NeverCopyContentDAO;
import com.huawei.sharedrive.isystem.mirror.appdatamigration.domain.NeverCopyContent;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Component
public class NeverCopyContentDAOImpl extends AbstractDAOImpl implements NeverCopyContentDAO
{
    
    @SuppressWarnings("deprecation")
    @Override
    public void create(NeverCopyContent object)
    {
        sqlMapClientTemplate.insert("NeverCopyContent.insert", object);
    }

    @Override
    public List<NeverCopyContent> get(String objectId)
    {
        return null;
    }

    @Override
    public List<NeverCopyContent> getByMD5(String md5, String blockMD5, long size)
    {
        return null;
    }

    @Override
    public void delete(NeverCopyContent object)
    {
        
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<NeverCopyContent> getNeverCopyContentByPolicyId(int policyId)
    {
        return sqlMapClientTemplate.queryForList("NeverCopyContent.getNeverCopyContentByPolicyId");
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<NeverCopyContent> getNeverCopyContentByEveryDayProcessId(String parent)
    {
        return sqlMapClientTemplate.queryForList("NeverCopyContent.getNeverCopyContentByEveryDayProcessId");
    }

}
