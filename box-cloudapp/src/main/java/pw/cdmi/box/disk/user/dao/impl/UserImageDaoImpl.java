package pw.cdmi.box.disk.user.dao.impl;

import org.springframework.stereotype.Component;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.disk.core.dao.util.HashTool;
import pw.cdmi.box.disk.user.dao.UserImageDao;
import pw.cdmi.box.disk.user.domain.UserImage;

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
        return (Integer)sqlMapClientTemplate.queryForObject("UserImage.getUid", userImage);
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
