package com.huawei.sharedrive.uam.enterprise.manager.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.common.domain.AccountConfig;
import com.huawei.sharedrive.uam.enterprise.domain.AccountConfigAttribute;
import com.huawei.sharedrive.uam.enterprise.manager.AccountConfigManager;
import com.huawei.sharedrive.uam.enterprise.service.AccountConfigService;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.openapi.domain.account.AccountAttribute;

@Component
public class AccountConfigManagerImpl implements AccountConfigManager
{
    private static List<String> protocols = new ArrayList<String>(10);
    static
    {
        protocols.add("imap");
        protocols.add("pop3");
    }
    
    private static final String SERVER_RULE = "^[a-zA-Z0-9-_.]{1,127}$";
    
    private static final String PROTOCOL_SMTP = "smtp";
    
    @Autowired
    private AccountConfigService accountConfigService;
    
    @Override
    public void configEmailReceive(long accountId, String[] arrConfig)
    {
        checkParameter(arrConfig, true);
        
        AccountConfig config = new AccountConfig();
        config.setAccountId(accountId);
        config.setName(AccountAttribute.SERVER_RECEIVE.getName());
        config.setValue(arrConfig[0]);
        createOrUpdate(config);
        config.setName(AccountAttribute.PROTOCOL_RECEIVE.getName());
        config.setValue(arrConfig[1]);
        createOrUpdate(config);
        config.setName(AccountAttribute.PORT_RECEIVE.getName());
        config.setValue(arrConfig[2]);
        createOrUpdate(config);
    }
    
    
    
    @Override
    public void configEmailSend(long accountId, String[] arrConfig)
    {
        checkParameter(arrConfig, false);
        
        AccountConfig config = new AccountConfig();
        config.setAccountId(accountId);
        config.setName(AccountAttribute.SERVER_SEND.getName());
        config.setValue(arrConfig[0]);
        createOrUpdate(config);
        config.setName(AccountAttribute.PROTOCOL_SEND.getName());
        config.setValue(arrConfig[1]);
        createOrUpdate(config);
        config.setName(AccountAttribute.PORT_SEND.getName());
        config.setValue(arrConfig[2]);
        createOrUpdate(config);
    }
    
    @Override
    public AccountConfig get(long accountId, String name)
    {
        return accountConfigService.get(accountId, name);
    }
    
    @Override
    public List<AccountConfig> list(long accountId)
    {
        return accountConfigService.list(accountId);
    }
    @Override
    public void createOrUpdate(AccountConfig accountConfig)
    {
        AccountConfig dbAccountConfig = accountConfigService.get(accountConfig.getAccountId(),
            accountConfig.getName());
        if (null == dbAccountConfig)
        {
            accountConfigService.create(accountConfig);
        }
        accountConfigService.update(accountConfig);
    }
    
    private void checkParameter(String[] arrConfig, boolean isReceive)
    {
        if (arrConfig[0] == null || !arrConfig[0].matches(SERVER_RULE))
        {
            throw new InvalidParamterException("Invalid server name.");
        }
        if (isReceive)
        {
            if (!protocols.contains(arrConfig[1].toLowerCase(Locale.ENGLISH)))
            {
                throw new InvalidParamterException("Invalid protocol.");
            }
        }
        else
        {
            if (!PROTOCOL_SMTP.equalsIgnoreCase(arrConfig[1]))
            {
                throw new InvalidParamterException("Invalid protocol.");
            }
        }
        try
        {
            int port = Integer.parseInt(arrConfig[2]);
            if (port < 1 || port > 65535)
            {
                throw new InvalidParamterException("Invalid port.");
            }
        }
        catch (NumberFormatException e)
        {
            throw new InvalidParamterException("Invalid port." + e);
        }
    }



//	@Override
//	public void configfunctionParameter(long accountId, String[] arrConfig) {
//		// TODO Auto-generated method stub
//        checkParameter(arrConfig, false);
//        
//        AccountConfig config = new AccountConfig();
//        config.setAccountId(accountId);
//        config.setName(AccountConfigAttribute.DEPARTMENT_ENABLE.getName());
//        config.setValue(arrConfig[0]);
//        createOrUpdate(config);
//        config.setName(AccountConfigAttribute.STAFF_STAFFID_ENABLE.getName());
//        config.setValue(arrConfig[1]);
//        createOrUpdate(config);
//        config.setName(AccountConfigAttribute.DOC_SECRETLEVEL_ENABLE.getName());
//        config.setValue(arrConfig[2]);
//        createOrUpdate(config);
//        config.setName(AccountConfigAttribute.STAFF_SECRETLEVEL_ENABLE.getName());
//        config.setValue(arrConfig[3]);
//        createOrUpdate(config);
//        config.setName(AccountConfigAttribute.DOC_VIRUSSCAN_ENABLE.getName());
//        config.setValue(arrConfig[4]);
//        createOrUpdate(config);
//        config.setName(AccountConfigAttribute.SMS_ENABLE.getName());
//        config.setValue(arrConfig[5]);
//        createOrUpdate(config);
//        config.setName(AccountConfigAttribute.MAIL_STMP_ENABLE.getName());
//        config.setValue(arrConfig[6]);
//        createOrUpdate(config);
//	}



	@Override
	public void createOrUpdate(long accountId, String name, String value) {
		// TODO Auto-generated method stub
		 AccountConfig accountConfig=new AccountConfig();
		 accountConfig.setAccountId(accountId);
		 accountConfig.setName(name);
		 accountConfig.setValue(value);
	     AccountConfig dbAccountConfig = accountConfigService.get(accountConfig.getAccountId(),
	             accountConfig.getName());
	         if (null == dbAccountConfig)
	         {
	             accountConfigService.create(accountConfig);
	         }
	         accountConfigService.update(accountConfig);
		
	}



	@Override
	public List<AccountConfig> getAll(long accountId) {
		// TODO Auto-generated method stub
		return accountConfigService.list(accountId);
	}
    
}
