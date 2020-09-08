package pw.cdmi.box.uam.enterprise.dao;

import pw.cdmi.box.uam.enterprise.domain.AccountSecConfig;

public interface AccountSecConfigDao
{
    
    void create(AccountSecConfig accountSecConfig);
    
    void update(AccountSecConfig accountSecConfig);
    
    AccountSecConfig get(int account);
}
