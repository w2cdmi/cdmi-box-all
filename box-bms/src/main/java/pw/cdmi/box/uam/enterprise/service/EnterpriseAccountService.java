package pw.cdmi.box.uam.enterprise.service;

import java.util.List;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.domain.enterprise.EnterpriseAccountVo;

public interface EnterpriseAccountService
{
    
    List<EnterpriseAccount> getByEnterpriseId(long enterpriseId);
    
    int deleteByAccountId(long accountId);
    
    void create(EnterpriseAccount enterpriseAccount);
    
    List<String> getAppByEnterpriseId(long enterpriseId);
    
    List<Long> getAccountIdByEnterpriseId(long enterpriseId);
    
    EnterpriseAccount getByEnterpriseApp(long enterpriseId, String authAppId);
    
    List<EnterpriseAccount> getAppContextByEnterpriseId(long enterpriseId);
    
    EnterpriseAccount getByAccessKeyId(String accessKeyId);
    
    EnterpriseAccount getByAccountId(long accountId);
    
    Page<EnterpriseAccountVo> getPageEnterpriseAccount(PageRequest equest, EnterpriseAccountVo accountVo);
    
    void modifyEnterpriseAccount(EnterpriseAccount enterpriseAccount);
}
