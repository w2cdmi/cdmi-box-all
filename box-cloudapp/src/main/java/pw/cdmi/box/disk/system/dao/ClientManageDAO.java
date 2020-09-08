package pw.cdmi.box.disk.system.dao;

import java.util.List;

import pw.cdmi.common.domain.ClientManage;

public interface ClientManageDAO
{
    List<ClientManage> getAll(String appId);
    
    ClientManage getClient(String appId, String type);
    
    /**
     * 
     * @param appId
     * @param type
     * @return
     */
    ClientManage getVersionInfoByType(String appId, String type);
    
    ClientManage getClientManageByVer(String type, String version);
}