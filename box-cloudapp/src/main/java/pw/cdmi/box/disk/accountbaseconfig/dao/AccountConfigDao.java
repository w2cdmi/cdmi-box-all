package pw.cdmi.box.disk.accountbaseconfig.dao;

import java.util.List;

import pw.cdmi.common.domain.AccountConfig;

public interface AccountConfigDao {
void create(AccountConfig accountConfig);
    
    AccountConfig get(long accountId, String name);
    
    List<AccountConfig> list(long accountId);
    
    int update(AccountConfig accountConfig);

	AccountConfig getByDomainName(String domainName);

}
