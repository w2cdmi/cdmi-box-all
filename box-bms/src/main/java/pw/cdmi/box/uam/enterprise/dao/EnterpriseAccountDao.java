package pw.cdmi.box.uam.enterprise.dao;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.domain.enterprise.EnterpriseAccountVo;

public interface EnterpriseAccountDao
{
    
    List<EnterpriseAccount> getByEnterpriseId(long enterpriseId);
    
    List<EnterpriseAccount> getAppContextByEnterpriseId(long enterpriseId);
    
    int deleteByAccountId(long accountId);
    
    void create(EnterpriseAccount enterpriseAccount);
    
    List<String> getAppByEnterpriseId(long enterpriseId);
    
    List<Long> getAccountIdByEnterpriseId(long enterpriseId);
    
    EnterpriseAccount getByEnterpriseApp(long enterpriseId, String authAppId);
    
    EnterpriseAccount getByAccessKeyId(String accessKeyId);
    
    EnterpriseAccount getByAccountId(long accountId);
    
    List<EnterpriseAccountVo> getEnterpriseAccountFilterd(EnterpriseAccountVo account, Order order,
        Limit limit);
    
    int getEnterpriseAccountFilterdCount(EnterpriseAccountVo accountVo);
    
    void modifyEnterpriseAccount(EnterpriseAccount enterpriseAccount);
}
