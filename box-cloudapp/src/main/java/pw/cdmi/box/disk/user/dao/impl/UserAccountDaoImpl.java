package pw.cdmi.box.disk.user.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.disk.core.dao.util.HashTool;
import pw.cdmi.box.disk.user.dao.UserAccountDao;
import pw.cdmi.box.disk.user.domain.UserAccount;

@Service
public class UserAccountDaoImpl extends AbstractDAOImpl implements UserAccountDao
{
    
    private static final int TABLE_COUNT = 100;
    
    @SuppressWarnings("deprecation")
    @Override
    public UserAccount getByCloudUserId(long accountId, long cloudUserId)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("cloudUserId", cloudUserId);
        map.put("tableSuffix", getTableSuffix(accountId));
        return (UserAccount) sqlMapClientTemplate.queryForObject("UserAccount.getByCloudUserId", map);
    }
    @SuppressWarnings("deprecation")
    @Override
    public UserAccount getByUserId(long accountId, long userId)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("userid", userId);
        map.put("tableSuffix", getTableSuffix(accountId));
        return (UserAccount) sqlMapClientTemplate.queryForObject("UserAccount.getByUserId", map);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void updateFirstLogin(UserAccount userAccount)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("accountId", userAccount.getAccountId());
        map.put("userId", userAccount.getUserId());
        map.put("firstLogin", userAccount.getFirstLogin());
        map.put("tableSuffix", getTableSuffix(userAccount.getAccountId()));
       sqlMapClientTemplate.update("UserAccount.updateFirstLogin", map);
    }
    
    private static String getTableSuffix(long accountId)
    {
        int table = (int) (HashTool.apply(String.valueOf(accountId)) % TABLE_COUNT);
        return "_" + table;
    }
}
