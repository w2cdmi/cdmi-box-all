package pw.cdmi.box.disk.system.service;

import java.util.List;

import pw.cdmi.box.disk.openapi.rest.v2.domain.ClientObject;
import pw.cdmi.common.domain.ClientManage;
import pw.cdmi.core.exception.InvalidParamException;
import pw.cdmi.core.exception.NoSuchClientException;

public interface ClientManageService
{
    
    List<ClientManage> getAll();
    
    ClientManage getAndroidClient();
    
    ClientObject getClientObject(String type) throws InvalidParamException, NoSuchClientException;
    
    ClientManage getIOSClient();
    
    ClientManage getPcClient();
    
    ClientManage getVersionInfoByType(String type);
    
    String getTDCodePath();
    
    ClientManage getClouderClient();
    
    ClientManage getClientManage(ClientManage clientManage);
    
    ClientManage getClientManageService(ClientManage client);
}
