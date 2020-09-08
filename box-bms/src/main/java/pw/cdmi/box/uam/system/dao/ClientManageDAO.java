package pw.cdmi.box.uam.system.dao;

import java.util.List;

import pw.cdmi.common.domain.ClientManage;

public interface ClientManageDAO
{
    List<ClientManage> getAll(String appId);
    
    void insert(ClientManage clientManage);
    
    void delete(String appId, String type);
    
}