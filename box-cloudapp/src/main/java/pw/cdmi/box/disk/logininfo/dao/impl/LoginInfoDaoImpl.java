package pw.cdmi.box.disk.logininfo.dao.impl;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.disk.core.dao.util.HashTool;
import pw.cdmi.box.disk.logininfo.dao.LoginInfoDao;
import pw.cdmi.box.disk.logininfo.domain.LoginInfo;
import pw.cdmi.common.cache.CacheClient;

@Service
public class LoginInfoDaoImpl extends CacheableSqlMapClientDAO implements LoginInfoDao
{
    
    private static final int TABLE_COUNT = 100;
    @Qualifier("uamCacheClient")
    private CacheClient cacheClient;
   
    
    private String getTableSuffix(String loginName)
    {
        String tableSuffix;
        int table = (int) (HashTool.apply(loginName.toLowerCase(Locale.ENGLISH)) % TABLE_COUNT);
        tableSuffix = "_" + table;
        return tableSuffix;
    }



	@Override
	public int getCountByLoginName(String loginName) {
		LoginInfo loginInfo = new LoginInfo();
	        loginInfo.setTableSuffix(getTableSuffix(loginName));
	        loginInfo.setLoginName(loginName);
		return (Integer) sqlMapClientTemplate.queryForObject("LoginInfo.getCountByLoginName", loginInfo);
	}



	@Override
	public CacheClient getCacheClient() {
		return cacheClient;
	}
    
}
