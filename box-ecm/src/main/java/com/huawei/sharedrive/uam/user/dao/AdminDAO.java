package com.huawei.sharedrive.uam.user.dao;

import java.util.HashSet;
import java.util.List;

import org.apache.shiro.authc.AuthenticationToken;

import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.domain.AdminRole;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.encrypt.HashPassword;

public interface AdminDAO {
	void updateStatus(Byte status, Long id);

	Admin get(Long id);

	Admin getByEmail(String email);

	Admin getByLoginName(String loginName);

	Admin getAdminByObjectSID(String objectSID);

	List<Admin> getFilterd(Admin filter, Order order, Limit limit);

	int getFilterdCount(Admin filter);

	void delete(Long id);

	void create(Admin admin);

	void update(Admin admin);

	long getNextAvailableAdminId();

	void updateValidKeyAndDynamicPwd(long id, String validateKey, String dynamicPwd);

	void updateEmail(long id, String email);

	void updatePassword(long id, HashPassword hashPassword);

	void updateLoginName(long id, String loginName);

	void updateLastLoginTime(long id);

	void updateLastLoginIP(long id, AuthenticationToken authcToken);

	void updateLastLoginInfo(long id, String loginIP);

	@SuppressWarnings("PMD.LooseCoupling")
	void updateRoles(long id, HashSet<AdminRole> roles);

	List<Admin> getByRole(AdminRole role);

	List<Admin> getAdminByIds(Long[] ids);

	List<Admin> getAdminExcludeIds(Long[] ids);

	Admin getByLoginNameWithoutCache(String loginName);

	Admin getByEmailWithoutCache(String email);

	Admin getByLoginNameAndEnterpriseIdWithoutCache(String loginName, long enterpriseId);
}