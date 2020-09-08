package pw.cdmi.box.uam.enterprise.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.enterprise.dao.EnterpriseDao;
import pw.cdmi.box.uam.enterprise.service.EnterpriseService;
import pw.cdmi.common.domain.enterprise.Enterprise;

@Component
public class EnterpriseServiceImpl implements EnterpriseService
{
    
    @Autowired
    private EnterpriseDao enterpriseDao;
    
    @Override
    public long create(Enterprise enterprise)
    {
        return enterpriseDao.create(enterprise);
        
    }
    
    @Override
    public boolean isDuplicateValues(Enterprise enterprise)
    {
        return enterpriseDao.isDuplicateValues(enterprise);
    }
    
    @Override
    public int getFilterdCount(String filter, String appId, Integer status)
    {
        int total = enterpriseDao.getFilterdCount(filter, appId, status);
        return total;
    }
    
    @Override
    public Page<Enterprise> getFilterd(String filter, Integer status, String appId, PageRequest pageRequest)
    {
        int total = enterpriseDao.getFilterdCount(filter, appId, status);
        List<Enterprise> content;
        if (pageRequest != null)
        {
            content = enterpriseDao.getFilterd(filter,
                status,
                appId,
                pageRequest.getOrder(),
                pageRequest.getLimit());
        }
        else
        {
            content = enterpriseDao.getFilterd(filter, status, appId, null, null);
        }
        
        Page<Enterprise> page = new PageImpl<Enterprise>(content, pageRequest, total);
        return page;
        
    }
    
    @Override
    public List<Enterprise> getByCombOrder(String filter, Integer status, String order, Limit limit)
    {
        List<Enterprise> contentEnterprises = enterpriseDao.getByCombOrder(filter, status, order, limit);
        return contentEnterprises;
    }
    
    @Override
    public Enterprise getById(long id)
    {
        
        return enterpriseDao.getById(id);
    }
    
    @Override
    public Enterprise getByOwnerId(long id)
    {
        
        return enterpriseDao.getByOwnerId(id);
    }
    
    @Override
    public void updateStatus(Enterprise enterprise)
    {
        
        enterpriseDao.updateStatus(enterprise);
    }
    
    @Override
    public void updateEnterpriseInfo(Enterprise enterprise)
    {
        enterpriseDao.updateEnterpriseInfo(enterprise);
        
    }
    
    @Override
    public long getByDomainExclusiveId(Enterprise enterprise)
    {
        return enterpriseDao.getByDomainExclusiveId(enterprise);
    }
    
    @Override
    public void deleteById(long id)
    {
        enterpriseDao.deleteById(id);
        
    }
    
    @Override
    public Enterprise getByDomainName(String domainName)
    {
        return enterpriseDao.getByDomainName(domainName);
    }
    
    @Override
    public void updateNetworkAuthStatus(Byte networkAuthStatus, Long id)
    {
        enterpriseDao.updateNetworkAuthStatus(networkAuthStatus, id);
    }
    
    @Override
    public List<Enterprise> listForUpdate()
    {
        List<Enterprise> list = enterpriseDao.listForUpdate();
        return list;
    }

	@Override
	public Enterprise getByContactEmail(String email) {
		return enterpriseDao.getByContactEmail(email);
	}

	@Override
	public Enterprise getByContactPhone(String phone) {
		return enterpriseDao.getByPhone(phone);
	}
}
