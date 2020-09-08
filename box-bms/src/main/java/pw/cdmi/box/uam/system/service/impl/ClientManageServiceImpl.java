package pw.cdmi.box.uam.system.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.box.uam.authapp.service.AuthAppService;
import pw.cdmi.box.uam.system.dao.ClientManageDAO;
import pw.cdmi.box.uam.system.service.ClientManageService;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.ClientManage;
import pw.cdmi.uam.domain.AuthApp;

@Component
public class ClientManageServiceImpl implements ClientManageService
{
    @Autowired
    private ClientManageDAO clientManageDAO;
    
    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private ConfigManager configManager;
    
    private ClientManageService proxySelf;
    
    @Autowired
    private AuthAppService authAppService;
    
    @PostConstruct
    public void setSelf()
    {
        proxySelf = context.getBean(ClientManageService.class);
    }
    
    @Override
    public List<ClientManage> getAll(String appId)
    {
        return clientManageDAO.getAll(appId);
    }
    
    @Override
    public void updateClient(ClientManage clientManage)
    {
        if (clientManage == null)
        {
            return;
        }
        proxySelf.doUpdate(clientManage);
        clientManage.setContent(null);
        
        AuthApp authApp = authAppService.getByAuthAppID(clientManage.getAppId());
        if (authApp.getType() == Constants.DEFAULT_WEB_APP_TYPE)
        {
            configManager.setConfig(ClientManage.class.getSimpleName(), clientManage);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doUpdate(ClientManage clientManage)
    {
        clientManageDAO.delete(clientManage.getAppId(), clientManage.getType());
        clientManageDAO.insert(clientManage);
    }
    
}
