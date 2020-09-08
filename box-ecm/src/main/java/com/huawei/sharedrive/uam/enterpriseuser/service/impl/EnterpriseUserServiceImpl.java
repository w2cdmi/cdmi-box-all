package com.huawei.sharedrive.uam.enterpriseuser.service.impl;

import com.huawei.sharedrive.uam.enterpriseuser.dao.EnterpriseUserDao;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseSecurityPrivilege;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.dto.EnterpriseUserStatus;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.util.PasswordGenerateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.utils.HashPasswordUtil;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;

@Component
public class EnterpriseUserServiceImpl implements EnterpriseUserService
{
    private static Logger logger = LoggerFactory.getLogger(EnterpriseUserServiceImpl.class);

    @Autowired
    private EnterpriseUserDao enterpriseUserDao;
    
    @Override
    public long create(EnterpriseUser enterpriseUser)
    {
        return enterpriseUserDao.create(enterpriseUser);
    }
    
    @Override
    public void update(EnterpriseUser enterpriseUser) {
        enterpriseUser.setModifiedAt(new Date());
        enterpriseUserDao.update(enterpriseUser);
    }
    
    @Override
    public List<EnterpriseUser> getFilterd(String filter, Long authServerId, Long departmentId,Long enterpriseId, Order order,
        Limit limit)
    {
        
        return enterpriseUserDao.getFilterd(filter, authServerId,departmentId,enterpriseId, order, limit);
    }
    
    @Override
    public int getFilterdCount(String filter, Long authServerId, Long enterpriseId, Long departmentId)
    {
        
        return enterpriseUserDao.getFilterdCount(filter, authServerId, enterpriseId,departmentId);
    }
    
    @Override
    public EnterpriseUser get(long userId, long enterpriseId)
    {
        return enterpriseUserDao.get(userId, enterpriseId);
    }
    
    @Override
    public EnterpriseUser getUserInfo(long userId, long enterpriseId, String authType)
    {
        return enterpriseUserDao.getUserInfo(userId, enterpriseId, authType);
    }
    
    @Override
    public EnterpriseUser getByObjectSid(String objectSid, long enterpriseId)
    {
        return enterpriseUserDao.getByObjectSid(objectSid, enterpriseId);
    }
    
    @Override
    public List<EnterpriseUser> getAccountUser(long accountId, long enterpriseId, long userSource,
        String filter, String ids)
    {
        
        return enterpriseUserDao.getAccountUser(accountId, enterpriseId, userSource, filter, ids);
    }
    
    @Override
    public void deleteByIds(long enterpriseId, String ids)
    {
        enterpriseUserDao.deleteByIds(enterpriseId, ids);
        
    }
    
    @Override
    public void deleteById(long enterpriseId, Long id)
    {
        enterpriseUserDao.deleteById(enterpriseId, id);
        
    }
    
    @Override
    public List<EnterpriseUser> getAllADEnterpriseUser(Long enterpriseId, Long authServerId)
    {
        return enterpriseUserDao.getAllADEnterpriseUser(enterpriseId, authServerId);
    }
    
    @Override
    public void updateLdapStatus(List<EnterpriseUser> list, Byte ldapStatus, Long enterpriseId)
    {
        enterpriseUserDao.updateLdapStatus(list, ldapStatus, enterpriseId);
    }
    
    @Override
    public int getByLdapStatusCount(Byte ldapStatus, Long enterpriseId)
    {
        return enterpriseUserDao.getByLdapStatusCount(ldapStatus, enterpriseId);
    }
    
    @Override
    public List<EnterpriseUser> getByLdapStatus(Byte ldapStatus, Long enterpriseId, Limit limit)
    {
        return enterpriseUserDao.getByLdapStatus(ldapStatus, enterpriseId, limit);
    }
    
    @Override
    public void updateLdapStatusByNotIn(List<EnterpriseUser> list, Byte ldapStatus, Long enterpriseId)
    {
        enterpriseUserDao.updateLdapStatusByNotIn(list, ldapStatus, enterpriseId);
    }
    
    @Override
    public void updateEnterpriseUser(EnterpriseUser enterpriseUser)
    {
        enterpriseUserDao.updateEnterpriseUser(enterpriseUser);
        
    }

    @Override
    public void updateStatus(List<Long> userIds, EnterpriseUserStatus status, Long enterpriseId) {
        enterpriseUserDao.updateStatus(userIds, status, enterpriseId);
    }

	@Override
	public List<EnterpriseUser> getAllEnterpriseUser(long enterpriseId) {
		return enterpriseUserDao.getAllEnterpriseUser(enterpriseId);
	}


    @Override
    public EnterpriseUser getByEnterpriseIdAndName(long enterpriseId, String name) {
        return enterpriseUserDao.getByEnterpriseIdAndName(enterpriseId, name);
    }

    @Override
    public EnterpriseUser resetPassword(long enterpriseId, long enterpriseUserId) {
        EnterpriseUser user = enterpriseUserDao.get(enterpriseUserId, enterpriseId);
        if(user == null) {
            logger.warn("no user found, enterpriseId={}, userId={}", enterpriseId, enterpriseUserId);
            return null;
        }

        try {
            String password = PasswordGenerateUtil.getRandomPassword(6);
            HashPassword hashPassword = HashPasswordUtil.generateHashPassword(password);

            user.setPassword(hashPassword.getHashPassword());
            user.setIterations(hashPassword.getIterations());
            user.setSalt(hashPassword.getSalt());
            enterpriseUserDao.update(user);

            //返回未加密的密码，以便发送通知
            user.setPassword(password);

            return user;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return null;
    }

	@Override
	public int getFilterdCount(String filter, Long authServerId, long enterpriseId, Long departmentId, long type,String status) {
		 return enterpriseUserDao.getFilterdCount(filter, authServerId, enterpriseId,departmentId,type,status);
	}

	@Override
	public List<EnterpriseUser> getFilterd(String filter, Long authServerId, Long departmentId, long enterpriseId, Order order, Limit limit, long type,String status) {
		 return enterpriseUserDao.getFilterd(filter, authServerId,departmentId,enterpriseId, order, limit,type,status);
	}

    public void changeStatus(long enterpriseId, byte status) {
        EnterpriseUser user = new EnterpriseUser();
        user.setEnterpriseId(enterpriseId);
        user.setStatus(status);
        user.setModifiedAt(new Date());

        enterpriseUserDao.changeStatus(user);
    }

    public List<EnterpriseUser> getByEnterpriseIdAndStatus(long enterpriseId, byte status) {
        return enterpriseUserDao.getByEnterpriseIdAndStatus(enterpriseId, status);
    }

    //获取某个部门下的用户信息
    @Override
    public List<EnterpriseUser> getByDeptId(long enterpriseId, long deptId) {
        return enterpriseUserDao.getByDeptId(enterpriseId, deptId);
    }

    @Override
    public void changeDepartment(long enterpriseUserId, long enterpriseId, long deptId) {
    	enterpriseUserDao.updateDepartmentById(enterpriseUserId, enterpriseId, deptId);
    }

}
