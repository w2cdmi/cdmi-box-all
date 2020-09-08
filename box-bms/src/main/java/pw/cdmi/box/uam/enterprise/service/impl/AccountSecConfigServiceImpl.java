package pw.cdmi.box.uam.enterprise.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.enterprise.dao.AccountSecConfigDao;
import pw.cdmi.box.uam.enterprise.domain.AccountSecConfig;
import pw.cdmi.box.uam.enterprise.service.AccountSecConfigService;

@Component
public class AccountSecConfigServiceImpl implements AccountSecConfigService
{
    @Autowired
    private AccountSecConfigDao accountSecConfigDao;
    
    @Override
    public AccountSecConfig get(int accountId)
    {
        return this.accountSecConfigDao.get(accountId);
    }
    
    @Override
    public void setConfig(int accountId, Byte enableSpaceSec, Byte enalbeFileSec, Byte enalbeFileCopySec)
    {
        AccountSecConfig config = this.accountSecConfigDao.get(accountId);
        if (null == config)
        {
            createAccountSecConfig(accountId, enableSpaceSec, enalbeFileSec, enalbeFileCopySec);
        }
        else
        {
            updateAccountSecConfig(enableSpaceSec, enalbeFileSec, config, enalbeFileCopySec);
        }
    }
    
    /**
     * @param accountId
     * @param enableSpaceSec
     * @param enalbeFileSec
     * @param enalbeFileCopySec
     */
    private void createAccountSecConfig(int accountId, Byte enableSpaceSec, Byte enalbeFileSec,
        Byte enalbeFileCopySec)
    {
        AccountSecConfig config;
        config = new AccountSecConfig();
        config.setAccountId(accountId);
        if (null != enableSpaceSec)
        {
            config.setEnableSpaceSec(enableSpaceSec);
        }
        else
        {
            config.setEnableSpaceSec(AccountSecConfig.DISABLE);
        }
        if (null != enalbeFileSec)
        {
            config.setEnableFileSec(enalbeFileSec);
        }
        else
        {
            config.setEnableFileSec(AccountSecConfig.DISABLE);
        }
        if (null != enalbeFileCopySec)
        {
            config.setEnableFileCopySec(enalbeFileCopySec);
        }
        else
        {
            config.setEnableFileCopySec(AccountSecConfig.DISABLE);
        }
        this.accountSecConfigDao.create(config);
    }
    
    /**
     * @param enableSpaceSec
     * @param enalbeFileSec
     * @param config
     * @param enalbeFileCopySec
     */
    private void updateAccountSecConfig(Byte enableSpaceSec, Byte enalbeFileSec, AccountSecConfig config,
        Byte enalbeFileCopySec)
    {
        if (null != enableSpaceSec)
        {
            config.setEnableSpaceSec(enableSpaceSec);
        }
        if (null != enalbeFileSec)
        {
            config.setEnableFileSec(enalbeFileSec);
        }
        if (null != enalbeFileCopySec)
        {
            config.setEnableFileCopySec(enalbeFileCopySec);
        }
        this.accountSecConfigDao.update(config);
    }
    
}
