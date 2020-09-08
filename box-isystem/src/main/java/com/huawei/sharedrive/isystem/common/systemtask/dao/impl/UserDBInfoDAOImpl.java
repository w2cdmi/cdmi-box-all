package com.huawei.sharedrive.isystem.common.systemtask.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.isystem.common.systemtask.dao.UserDBInfoDAO;
import com.huawei.sharedrive.isystem.common.systemtask.domain.UserDBInfo;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Repository
@SuppressWarnings({"unchecked", "deprecation"})
public class UserDBInfoDAOImpl extends AbstractDAOImpl implements UserDBInfoDAO
{
    
    @Override
    public List<UserDBInfo> listAllUserdbInfo()
    {
        return sqlMapClientTemplate.queryForList("UserDBInfo.getAll");
    }
    
}
