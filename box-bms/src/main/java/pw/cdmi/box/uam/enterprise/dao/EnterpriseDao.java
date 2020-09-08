package pw.cdmi.box.uam.enterprise.dao;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.common.domain.enterprise.Enterprise;

public interface EnterpriseDao
{
    
    long create(Enterprise enterprise);
    
    long queryMaxExecuteRecordId();
    
    boolean isDuplicateValues(Enterprise enterprise);
    
    int getFilterdCount(String filter, String appId, Integer status);
    
    List<Enterprise> getFilterd(String filter, Integer status, String appId, Order order, Limit limit);
    
    List<Enterprise> getByCombOrder(String filter, Integer status, String order, Limit limit);
    
    Enterprise getById(long id);
    
    Enterprise getByOwnerId(long id);
    
    long getByDomainExclusiveId(Enterprise enterprise);
    
    void updateEnterpriseInfo(Enterprise enterprise);
    
    void updateStatus(Enterprise enterprise);
    
    void updateNetworkAuthStatus(Byte networkAuthStatus, Long id);
    
    void deleteById(long id);
    
    Enterprise getByDomainName(String domainName);
    
    Enterprise getByContactEmail(String email);
    
    List<Enterprise> listForUpdate();
    
    Enterprise getByPhone(String phone);
    
}
