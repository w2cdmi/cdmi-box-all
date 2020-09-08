package pw.cdmi.box.uam.enterprise.service;

import java.util.List;

import pw.cdmi.common.domain.AccountConfig;
public interface AccountConfigService
{
    /**
     * 新增应用账户配置
     * 
     * @param accountConfig
     */
    void create(AccountConfig accountConfig);
    
    /**
     * 查询应用账户配置项
     * 
     * @param accountId
     * @param name
     * @return
     */
    AccountConfig get(long accountId, String name);
    
    /**
     * 列举应用账户所有配置项
     * 
     * @param accountId
     * @return
     */
    List<AccountConfig> list(long accountId);
    
    /**
     * 更新应用账户配置
     * 
     * @param accountConfig
     * 
     */
    void update(AccountConfig accountConfig);
    
}
