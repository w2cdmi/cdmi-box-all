package pw.cdmi.box.uam.system.service;

import java.util.List;

import pw.cdmi.common.domain.ClientManage;

public interface ClientManageService
{
    
    /**
     * @param appId
     * @return
     */
    List<ClientManage> getAll(String appId);
    
    /**
     * @param clientManage
     */
    void updateClient(ClientManage clientManage);
    
    /**
     * @param clientManage
     */
    void doUpdate(ClientManage clientManage);
}
