package pw.cdmi.box.uam.enterprise.manager;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.enterprise.domain.EnterpriseAccountModifyRequestInfo;
import pw.cdmi.box.uam.enterprise.domain.EnterpriseAccountV1;
import pw.cdmi.box.uam.httpclient.domain.RestEnterpriseAccountRequest;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.domain.enterprise.EnterpriseAccountVo;

public interface EnterpriseAccountManager
{
    
    void create(RestEnterpriseAccountRequest restEnterpriseAccountRequest);
    
    List<String> getAppByEnterpriseId(long enterpriseId);
    
    EnterpriseAccount getByEnterpriseApp(long enterpriseId, String authAppId);
    
    EnterpriseAccount getByAccountId(long accountId);
    
    Page<EnterpriseAccountVo> getPagedEnterpriseAccount(PageRequest equest, String filter, String authAppId,
        String status);
    
    void modifyAccountConfiguration(HttpServletRequest request, EnterpriseAccount enterpriseAccount,
        EnterpriseAccountModifyRequestInfo modifyInfo);
    
    void modifyAccountStatus(HttpServletRequest request, EnterpriseAccount enterpriseAccount, Byte status);
    
    EnterpriseAccountV1 getByAccountAndAppId(String authAppId,long accountId);
    
}
