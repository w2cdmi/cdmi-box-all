package pw.cdmi.box.uam.user.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.exception.ExistUserConflictException;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.user.manager.AdminManager;
import pw.cdmi.box.uam.user.service.AdminService;

@Component
public class AdminManagerImpl implements AdminManager {

	@Autowired
	private AdminService adminService;

	@Override
	public void create(Admin admin) {
		Admin dbAdmin = adminService.getAdminByLoginName(admin.getLoginName());
		if (null != dbAdmin) {
			throw new ExistUserConflictException();
		}
		adminService.create(admin);

	}

	@Override
	public void delete(Long id) {
		Admin dbAdmin = adminService.get(id);
		if (null != dbAdmin) {
			adminService.delete(id);
		}
	}

	@Override
	public Admin queryByLoginName(String loginName) {

		Admin admin = adminService.getAdminByLoginName(loginName);

		return admin;
	}

}
