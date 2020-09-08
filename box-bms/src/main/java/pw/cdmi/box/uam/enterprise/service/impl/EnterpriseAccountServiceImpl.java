package pw.cdmi.box.uam.enterprise.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.enterprise.dao.EnterpriseAccountDao;
import pw.cdmi.box.uam.enterprise.service.EnterpriseAccountService;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.domain.enterprise.EnterpriseAccountVo;

@Service
public class EnterpriseAccountServiceImpl implements EnterpriseAccountService
{
    
    @Autowired
    private EnterpriseAccountDao enterpriseAccountDao;
    
    @Override
    public List<EnterpriseAccount> getByEnterpriseId(long enterpriseId)
    {
        
        return enterpriseAccountDao.getByEnterpriseId(enterpriseId);
    }
    
    @Override
    public int deleteByAccountId(long accountId)
    {
        
        return enterpriseAccountDao.deleteByAccountId(accountId);
    }
    
    @Override
    public void create(EnterpriseAccount enterpriseAccount)
    {
        
        enterpriseAccountDao.create(enterpriseAccount);
    }
    
    @Override
    public EnterpriseAccount getByEnterpriseApp(long enterpriseId, String authAppId)
    {
        return enterpriseAccountDao.getByEnterpriseApp(enterpriseId, authAppId);
    }
    
    @Override
    public List<String> getAppByEnterpriseId(long enterpriseId)
    {
        
        return enterpriseAccountDao.getAppByEnterpriseId(enterpriseId);
    }
    
    @Override
    public List<Long> getAccountIdByEnterpriseId(long enterpriseId)
    {
        
        return enterpriseAccountDao.getAccountIdByEnterpriseId(enterpriseId);
    }
    
    @Override
    public List<EnterpriseAccount> getAppContextByEnterpriseId(long enterpriseId)
    {
        
        return enterpriseAccountDao.getAppContextByEnterpriseId(enterpriseId);
    }
    
    @Override
    public EnterpriseAccount getByAccessKeyId(String accessKeyId)
    {
        return enterpriseAccountDao.getByAccessKeyId(accessKeyId);
    }
    
    @Override
    public EnterpriseAccount getByAccountId(long accountId)
    {
        return enterpriseAccountDao.getByAccountId(accountId);
    }
    
    @Override
    public Page<EnterpriseAccountVo> getPageEnterpriseAccount(PageRequest request,
        EnterpriseAccountVo accountVo)
    {
        int total = enterpriseAccountDao.getEnterpriseAccountFilterdCount(accountVo);
        
        List<EnterpriseAccountVo> content = enterpriseAccountDao.getEnterpriseAccountFilterd(accountVo,
            request.getOrder(),
            request.getLimit());
        Page<EnterpriseAccountVo> page = new PageImpl<EnterpriseAccountVo>(content, request, total);
        return page;
    }
    
    @Override
    public void modifyEnterpriseAccount(EnterpriseAccount enterpriseAccount)
    {
        enterpriseAccountDao.modifyEnterpriseAccount(enterpriseAccount);
    }
}
