package pw.cdmi.box.disk.logininfo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.disk.logininfo.dao.LoginInfoDao;
import pw.cdmi.box.disk.logininfo.service.LoginInfoService;

@Service
public class LoginInfoServiceImpl implements LoginInfoService{

	 @Autowired
	 private LoginInfoDao loginInfoDao;
	
	@Override
	public int getCountByLoginName(String loginName) {
		return loginInfoDao.getCountByLoginName(loginName);
	}

}
