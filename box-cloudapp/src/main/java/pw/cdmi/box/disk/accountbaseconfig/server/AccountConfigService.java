package pw.cdmi.box.disk.accountbaseconfig.server;

import java.util.List;

import pw.cdmi.common.domain.AccountConfig;
import pw.cdmi.common.domain.enterprise.Enterprise;

public interface AccountConfigService
{
	 void create(AccountConfig accountConfig);
	    
	    AccountConfig get(long accountId, String name);
	    
	    List<AccountConfig> list(long accountId);
	    
	    void update(AccountConfig accountConfig);
	    AccountConfig getByDomainName(String domainName);
    
}
