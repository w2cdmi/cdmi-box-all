package com.huawei.sharedrive.uam.enterprise.manager.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseService;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.exception.ForbiddenException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.user.service.AdminService;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.enterprise.Enterprise;

@Component
public class EnterpriseManagerImpl implements EnterpriseManager
{
    private static Logger logger = LoggerFactory.getLogger(EnterpriseManagerImpl.class);
    
    @Autowired
    private EnterpriseService enterpriseService;
    
    @Autowired
    private AdminService adminService;
    
    @Override
    public Page<Enterprise> getFilterd(String filter, String appId, PageRequest pageRequest)
    {
        return enterpriseService.getFilterd(filter, appId, pageRequest);
    }
    
    @Override
    public Enterprise getById(long id)
    {
        return enterpriseService.getById(id);
    }
    
    @Override
    public void updateNetworkAuthStatus(Long id, byte networkAuthStatus)
    {
        if (null == id)
        {
            logger.error("id is null");
            throw new BusinessException("id is null");
        }
        Enterprise enterprise = enterpriseService.getById(id);
        if (null == enterprise)
        {
            logger.error("no such enterprise id:" + id);
            throw new BusinessException("no such enterprise id:" + id);
        }
        enterpriseService.updateNetworkAuthStatus(networkAuthStatus, id);
    }
    
    @Override
    public String paramValidate(Enterprise oldInfo, Enterprise enterprise)
    {
        String name = enterprise.getName();
        String email = enterprise.getContactEmail();
        String domainName = enterprise.getDomainName();
        String person = enterprise.getContactPerson();
        String phone = enterprise.getContactPhone();
        if (name.length() > 255 || name.length() <= 0)
        {
            return "invalidName";
        }
        if (!email.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$") || email.length() > 64
            || email.length() <= 0)
        {
            return "invalidEmail";
        }
        if (!domainName.matches("^[a-zA-Z0-9-_]*$") || domainName.length() > 64 || domainName.length() <= 0)
        {
            return "invalidDomianName";
        }
        if (!phone.matches("^[0-9 +-]*$") || phone.length() > 255)
        {
            return "invalidContactPhone";
        }
        if (person.length() > 255)
        {
            return "invalidContactPerson";
        }
        if (!oldInfo.getContactEmail().equals(enterprise.getContactEmail())
            || !oldInfo.getDomainName().equals(enterprise.getDomainName()))
        {
            return "invalid request";
        }
        return null;
    }
    
    @Override
    public String updateEnterpriseInfo(Enterprise enterprise)
    {
        boolean isModifyEmail = false;
        
        Enterprise enterpriseInfo = getById(enterprise.getId());
        if (null == enterpriseInfo)
        {
            logger.error("no such enterprise by admin'enterpriseId:" + enterprise.getId());
            throw new InvalidParamterException("no such enterprise by admin'enterpriseId:"
                + enterprise.getId());
        }
        /*if (!enterpriseInfo.getContactEmail().equals(enterprise.getContactEmail()))
        {
            isModifyEmail = true;
        }*/
        enterpriseInfo.setName(enterprise.getName());
        enterpriseInfo.setContactEmail(enterprise.getContactEmail());
        enterpriseInfo.setContactPerson(enterprise.getContactPerson());
        enterpriseInfo.setContactPhone(enterprise.getContactPhone());
       /*  增加修改密码复杂度   pwdLevel*/
        enterpriseInfo.setPwdLevel(enterprise.getPwdLevel());
        long count = getByDomainExclusiveId(enterpriseInfo);
        if (count != 0)
        {
            return "domainOrEmailConflict";
        }
        
       /* if (isModifyEmail)
        {
            Admin checkAdmin = adminService.getAdminByLoginName(enterpriseInfo.getContactEmail());
            Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
            if (checkAdmin != null)
            {
                if (checkAdmin.getId() == sessAdmin.getId())
                {
                    return "userNameNotChange";
                }
                return "repeatLoginName";
            }
            sessAdmin.setLoginName(enterpriseInfo.getContactEmail());
            adminService.changeLoginName(sessAdmin);
            adminService.updateEmail(sessAdmin.getId(), enterpriseInfo.getContactEmail());
        }*/
        
        enterpriseService.updateEnterpriseInfo(enterpriseInfo);
        return null;
    }
    
    @Override
    public long getByDomainExclusiveId(Enterprise enterprise)
    {
        
        return enterpriseService.getByDomainExclusiveId(enterprise);
    }
    
    @Override
    public Enterprise getByDomainName(String domainName)
    {
        return enterpriseService.getByDomainName(domainName);
    }

    @Override
    public Enterprise getByName(String name) {
        return enterpriseService.getByName(name);
    }


	@Override
	public boolean checkOrganizeEnabled(long enterpriseId) {
		Enterprise enterprise = getById(enterpriseId);
		boolean isDepartmentEnable = enterprise.isIsdepartment();
		if (isDepartmentEnable) {
			return true;
		}
		return true;
	}

	@Override
	public void checkOrganizeOperPrivilege(long enterpriseId) {
		boolean isDeptPrivilege = checkOrganizeEnabled(enterpriseId);
		if (!isDeptPrivilege) {
			throw new ForbiddenException("Current enterprise privilege does not support organization management!");
		}
	}

	@Override
	public boolean firstEnterCheckOrganizeOperPrivilege(long enterpriseId) {
		
		boolean isDeptPrivilege = true;
		isDeptPrivilege = checkOrganizeEnabled(enterpriseId);
		return isDeptPrivilege;
	}
	@Override
    public void updateEnterprise(Enterprise enterprise) {
		
		enterpriseService.updateEnterpriseInfo(enterprise);
	}
}
