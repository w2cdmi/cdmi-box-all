package com.huawei.sharedrive.uam.user.dao.impl;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.core.dao.util.HashTool;
import com.huawei.sharedrive.uam.user.dao.UserImageDao;
import com.huawei.sharedrive.uam.user.domain.UserImage;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Component
public class UserImageDaoImpl extends AbstractDAOImpl implements UserImageDao
{
    private static final int TABLE_COUNT = 100;
    
    @SuppressWarnings("deprecation")
    @Override
    public void insert(UserImage userImage)
    {
        userImage.setTableSuffix(getTableSuffix(userImage.getAccountId()));
        sqlMapClientTemplate.insert("UserImage.insert", userImage);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void update(UserImage userImage)
    {
        userImage.setTableSuffix(getTableSuffix(userImage.getAccountId()));
        sqlMapClientTemplate.update("UserImage.update", userImage);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public UserImage get(UserImage userImage)
    {
        userImage.setTableSuffix(getTableSuffix(userImage.getAccountId()));
        return (UserImage) sqlMapClientTemplate.queryForObject("UserImage.get", userImage);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int getUserId(UserImage userImage)
    {
        userImage.setTableSuffix(getTableSuffix(userImage.getAccountId()));
        return (Integer) sqlMapClientTemplate.queryForObject("UserImage.getUid", userImage);
    }
    
    private String getTableSuffix(long accountId)
    {
        String tableSuffix = null;
        int table = (int) (HashTool.apply(String.valueOf(accountId)) % TABLE_COUNT);
        if (table >= 0)
        {
            tableSuffix = "_" + table;
        }
        return tableSuffix;
    }
}
