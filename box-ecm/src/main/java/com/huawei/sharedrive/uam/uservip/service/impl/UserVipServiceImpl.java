package com.huawei.sharedrive.uam.uservip.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.uservip.dao.UserVipDao;
import com.huawei.sharedrive.uam.uservip.domain.UserVip;
import com.huawei.sharedrive.uam.uservip.service.UserVipService;

@Service
public class UserVipServiceImpl implements UserVipService{
	
	@Autowired
	private UserVipDao userVipDao;

	@Override
	public void create(UserVip userVip) {
		userVipDao.create(userVip);
	}

	@Override
	public void update(UserVip userVip) {
		userVipDao.update(userVip);
	}

	@Override
	public UserVip get(UserVip userVip) {
		return userVipDao.get(userVip);
	}

	@Override
	public List<UserVip> listAll() {
		return userVipDao.listAll();
	}

	@Override
	public void delete(UserVip userVip) {
		userVipDao.delete(userVip);
	}

}
