package com.huawei.sharedrive.app.mirror.dao.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.mirror.dao.UserMirrorStatisticInfoDAO;
import com.huawei.sharedrive.app.mirror.domain.UserMirrorStatisticInfo;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.core.utils.HashTool;

@Component
public class UserMirrorStatisticInfoDAOImpl extends AbstractDAOImpl implements UserMirrorStatisticInfoDAO
{
    private static final int TABLE_COUNT = 100;
    
    private int getTableSuffix(UserMirrorStatisticInfo userMirrorStatisticInfo)
    {
        long accountId = userMirrorStatisticInfo.getAccountId();
        
        int tableNum = (int) (HashTool.apply(accountId) % TABLE_COUNT);
        return tableNum;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void create(UserMirrorStatisticInfo info)
    {
        info.setTableSuffix(getTableSuffix(info));
        sqlMapClientTemplate.insert("UserMirrorStatisticInfo.insert", info);
    }
    

    
    @SuppressWarnings("deprecation")
    @Override
    public int  update(UserMirrorStatisticInfo info)
    {
        info.setTableSuffix(getTableSuffix(info));
        return sqlMapClientTemplate.update("UserMirrorStatisticInfo.update", info);
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<UserMirrorStatisticInfo> listStatisticByUserId(UserMirrorStatisticInfo info)
    {
        info.setTableSuffix(getTableSuffix(info));
        return sqlMapClientTemplate.queryForList("UserMirrorStatisticInfo.getByUserId", info);
    }

    @SuppressWarnings("deprecation")
    @Override
    public UserMirrorStatisticInfo getLastStatisticInfo(UserMirrorStatisticInfo info)
    {
        info.setTableSuffix(getTableSuffix(info));
        return (UserMirrorStatisticInfo) sqlMapClientTemplate.queryForObject("UserMirrorStatisticInfo.getLastStatisticInfo", info);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int delete(UserMirrorStatisticInfo info)
    {
        info.setTableSuffix(getTableSuffix(info));
        return sqlMapClientTemplate.delete("UserMirrorStatisticInfo.delete", info);
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    public UserMirrorStatisticInfo get(UserMirrorStatisticInfo info)
    {
        info.setTableSuffix(getTableSuffix(info));
        return (UserMirrorStatisticInfo) sqlMapClientTemplate.queryForObject("UserMirrorStatisticInfo.get", info);
    }
    
    
}
