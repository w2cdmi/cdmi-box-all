package pw.cdmi.box.disk.user.dao;

import pw.cdmi.box.disk.user.domain.EnterpriseUser;

public interface EnterpriseUserDao
{
    EnterpriseUser get(long userId, long enterpriseId);
    
    EnterpriseUser getByLoginname(String name, long enterpriseId);
}
